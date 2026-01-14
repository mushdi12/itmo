#include <errno.h>
#include <fcntl.h>
#include <inttypes.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>

#include "vtpc.h"

// Trace format (one operation per line):
//   R <offset> <size>
//   W <offset> <size>
//   S
// Lines starting with '#' are ignored.
//
// For Optimal, we treat every page touched by an operation as an "access" at time t.

enum op_kind { OP_READ, OP_WRITE, OP_SYNC };

struct hint {
  uint64_t page_index;
  uint64_t next_use;
};

struct op {
  enum op_kind kind;
  off_t offset;
  size_t size;
  struct hint* hints;
  size_t hints_count;
};

enum { MAP_EMPTY = 0, MAP_USED = 1, MAP_TOMB = 2 };

struct map_entry {
  uint64_t key;
  uint64_t value;
  unsigned char state;
};

struct u64u64_map {
  size_t cap;
  size_t size;
  struct map_entry* entries;
};

static uint64_t hash_u64(uint64_t x) {
  x += 0x9e3779b97f4a7c15ULL;
  x = (x ^ (x >> 30U)) * 0xbf58476d1ce4e5b9ULL;
  x = (x ^ (x >> 27U)) * 0x94d049bb133111ebULL;
  return x ^ (x >> 31U);
}

static int map_init(struct u64u64_map* m, size_t cap_pow2) {
  m->cap = cap_pow2;
  m->size = 0;
  m->entries = calloc(cap_pow2, sizeof(struct map_entry));
  return m->entries == NULL ? -1 : 0;
}

static void map_free(struct u64u64_map* m) {
  free(m->entries);
  m->entries = NULL;
  m->cap = 0;
  m->size = 0;
}

static int map_get(const struct u64u64_map* m, uint64_t key, uint64_t* out_value) {
  if (m->cap == 0) {
    return 0;
  }
  const size_t mask = m->cap - 1;
  size_t idx = (size_t)hash_u64(key) & mask;
  for (size_t i = 0; i < m->cap; ++i) {
    const struct map_entry* e = &m->entries[idx];
    if (e->state == MAP_EMPTY) {
      return 0;
    }
    if (e->state == MAP_USED && e->key == key) {
      *out_value = e->value;
      return 1;
    }
    idx = (idx + 1) & mask;
  }
  return 0;
}

static int map_put(struct u64u64_map* m, uint64_t key, uint64_t value) {
  const size_t mask = m->cap - 1;
  size_t idx = (size_t)hash_u64(key) & mask;
  size_t first_tomb = (size_t)(-1);
  for (size_t i = 0; i < m->cap; ++i) {
    struct map_entry* e = &m->entries[idx];
    if (e->state == MAP_EMPTY) {
      if (first_tomb != (size_t)(-1)) {
        e = &m->entries[first_tomb];
      }
      e->state = MAP_USED;
      e->key = key;
      e->value = value;
      m->size++;
      return 0;
    }
    if (e->state == MAP_TOMB && first_tomb == (size_t)(-1)) {
      first_tomb = idx;
    } else if (e->state == MAP_USED && e->key == key) {
      e->value = value;
      return 0;
    }
    idx = (idx + 1) & mask;
  }
  errno = ENOSPC;
  return -1;
}

static uint64_t env_u64(const char* name, uint64_t def) {
  const char* v = getenv(name);
  if (v == NULL || v[0] == '\0') {
    return def;
  }
  errno = 0;
  char* end = NULL;
  unsigned long long parsed = strtoull(v, &end, 10);
  if (errno != 0 || end == v) {
    return def;
  }
  return (uint64_t)parsed;
}

static int parse_trace(const char* trace_path, struct op** out_ops, size_t* out_n) {
  FILE* f = fopen(trace_path, "r");
  if (f == NULL) {
    return -1;
  }

  struct op* ops = NULL;
  size_t cap = 0;
  size_t n = 0;

  char line[4096];
  while (fgets(line, sizeof(line), f) != NULL) {
    if (line[0] == '#' || line[0] == '\n' || line[0] == '\0') {
      continue;
    }

    char kind = 0;
    long long off = 0;
    unsigned long long size = 0;

    if (line[0] == 'S') {
      kind = 'S';
    } else if (sscanf(line, " %c %lld %llu", &kind, &off, &size) != 3) {
      errno = EINVAL;
      fclose(f);
      return -1;
    }

    if (n == cap) {
      const size_t new_cap = cap == 0 ? 1024 : cap * 2;
      struct op* new_ops = realloc(ops, new_cap * sizeof(struct op));
      if (new_ops == NULL) {
        fclose(f);
        return -1;
      }
      ops = new_ops;
      cap = new_cap;
    }

    ops[n].hints = NULL;
    ops[n].hints_count = 0;

    if (kind == 'R') {
      ops[n].kind = OP_READ;
      ops[n].offset = (off_t)off;
      ops[n].size = (size_t)size;
    } else if (kind == 'W') {
      ops[n].kind = OP_WRITE;
      ops[n].offset = (off_t)off;
      ops[n].size = (size_t)size;
    } else if (kind == 'S') {
      ops[n].kind = OP_SYNC;
      ops[n].offset = 0;
      ops[n].size = 0;
    } else {
      errno = EINVAL;
      fclose(f);
      return -1;
    }
    n++;
  }

  fclose(f);
  *out_ops = ops;
  *out_n = n;
  return 0;
}

static void free_trace(struct op* ops, size_t n) {
  for (size_t i = 0; i < n; ++i) {
    free(ops[i].hints);
  }
  free(ops);
}

static int precompute_hints(struct op* ops, size_t n, size_t page_size) {
  // Use a simple map: page_index -> next_time.
  // Capacity: power of two, sized for typical traces.
  size_t map_cap = 1;
  while (map_cap < n * 4) {
    map_cap <<= 1;
    if (map_cap > (1U << 26U)) {  // cap at ~67M entries
      break;
    }
  }
  if (map_cap < 1024) {
    map_cap = 1024;
  }

  struct u64u64_map next;
  if (map_init(&next, map_cap) != 0) {
    return -1;
  }

  for (size_t ti = n; ti-- > 0;) {
    struct op* op = &ops[ti];
    if (op->kind == OP_SYNC || op->size == 0) {
      continue;
    }
    if (op->offset < 0) {
      map_free(&next);
      errno = EINVAL;
      return -1;
    }

    const uint64_t start = (uint64_t)op->offset / (uint64_t)page_size;
    const uint64_t end =
        ((uint64_t)op->offset + (uint64_t)op->size - 1) / (uint64_t)page_size;
    const size_t pages = (size_t)(end - start + 1);

    op->hints = calloc(pages, sizeof(struct hint));
    if (op->hints == NULL) {
      map_free(&next);
      return -1;
    }
    op->hints_count = pages;

    for (size_t j = 0; j < pages; ++j) {
      const uint64_t page = start + (uint64_t)j;
      uint64_t nu = UINT64_MAX;
      (void)map_get(&next, page, &nu);
      op->hints[j].page_index = page;
      op->hints[j].next_use = nu;
      if (map_put(&next, page, (uint64_t)ti) != 0) {
        map_free(&next);
        return -1;
      }
    }
  }

  map_free(&next);
  return 0;
}

static void usage(const char* argv0) {
  fprintf(
      stderr,
      "usage:\n"
      "  %s --mode=vtpc <file_path> <trace_path>\n"
      "  %s --mode=libc <file_path> <trace_path>\n"
      "\n"
      "Env:\n"
      "  VTPC_PAGE_SIZE (default 4096)\n"
      "  VTPC_CACHE_PAGES (default 64)\n",
      argv0,
      argv0
  );
}

int main(int argc, char** argv) {
  const char* mode = "vtpc";
  int argi = 1;
  if (argc > 1 && strncmp(argv[1], "--mode=", 7) == 0) {
    mode = argv[1] + 7;
    argi++;
  }

  if (argc - argi != 2) {
    usage(argv[0]);
    return 2;
  }

  const char* file_path = argv[argi + 0];
  const char* trace_path = argv[argi + 1];

  size_t page_size = (size_t)env_u64("VTPC_PAGE_SIZE", 4096);
  if (page_size < 512 || (page_size & (page_size - 1)) != 0) {
    page_size = 4096;
  }

  struct op* ops = NULL;
  size_t n = 0;
  if (parse_trace(trace_path, &ops, &n) != 0) {
    perror("parse_trace");
    return 1;
  }

  if (strcmp(mode, "vtpc") == 0) {
    if (precompute_hints(ops, n, page_size) != 0) {
      perror("precompute_hints");
      free_trace(ops, n);
      return 1;
    }
  }

  int fd = -1;
  if (strcmp(mode, "vtpc") == 0) {
    fd = lab2_open(file_path);
  } else if (strcmp(mode, "libc") == 0) {
    fd = open(file_path, O_RDWR | O_CREAT, 0666);
  } else {
    usage(argv[0]);
    free_trace(ops, n);
    return 2;
  }
  if (fd < 0) {
    perror("open");
    free_trace(ops, n);
    return 1;
  }

  size_t buf_cap = 4096;
  char* buf = malloc(buf_cap);
  if (buf == NULL) {
    perror("malloc");
    (void)(strcmp(mode, "vtpc") == 0 ? lab2_close(fd) : close(fd));
    free_trace(ops, n);
    return 1;
  }

  for (size_t i = 0; i < n; ++i) {
    const struct op* op = &ops[i];

    if (op->kind == OP_SYNC) {
      if (strcmp(mode, "vtpc") == 0) {
        if (lab2_fsync(fd) != 0) {
          perror("lab2_fsync");
          break;
        }
      } else {
        if (fsync(fd) != 0) {
          perror("fsync");
          break;
        }
      }
      continue;
    }

    if (op->size > buf_cap) {
      char* nb = realloc(buf, op->size);
      if (nb == NULL) {
        perror("realloc");
        break;
      }
      buf = nb;
      buf_cap = op->size;
    }

    if (strcmp(mode, "vtpc") == 0) {
      // Feed hints BEFORE the access (so they are available on cache misses).
      for (size_t j = 0; j < op->hints_count; ++j) {
        const off_t page_off = (off_t)(op->hints[j].page_index * (uint64_t)page_size);
        if (lab2_advice(fd, page_off, op->hints[j].next_use) != 0) {
          perror("lab2_advice");
          goto out;
        }
      }
      if (lab2_lseek(fd, op->offset, SEEK_SET) == (off_t)-1) {
        perror("lab2_lseek");
        break;
      }
    } else {
      if (lseek(fd, op->offset, SEEK_SET) == (off_t)-1) {
        perror("lseek");
        break;
      }
    }

    if (op->kind == OP_READ) {
      if (strcmp(mode, "vtpc") == 0) {
        const ssize_t r = lab2_read(fd, buf, op->size);
        if (r < 0 || (size_t)r != op->size) {
          perror("lab2_read");
          break;
        }
      } else {
        size_t done = 0;
        while (done < op->size) {
          const ssize_t r = read(fd, buf + done, op->size - done);
          if (r < 0) {
            perror("read");
            goto out;
          }
          if (r == 0) {
            fprintf(stderr, "EOF while reading\n");
            goto out;
          }
          done += (size_t)r;
        }
      }
    } else {  // OP_WRITE
      for (size_t k = 0; k < op->size; ++k) {
        buf[k] = (char)((uint8_t)(k + i));
      }
      if (strcmp(mode, "vtpc") == 0) {
        const ssize_t r = lab2_write(fd, buf, op->size);
        if (r < 0 || (size_t)r != op->size) {
          perror("lab2_write");
          break;
        }
      } else {
        size_t done = 0;
        while (done < op->size) {
          const ssize_t r = write(fd, buf + done, op->size - done);
          if (r < 0) {
            perror("write");
            goto out;
          }
          if (r == 0) {
            fprintf(stderr, "short write\n");
            goto out;
          }
          done += (size_t)r;
        }
      }
    }
  }

out:
  free(buf);
  if (strcmp(mode, "vtpc") == 0) {
    (void)lab2_close(fd);
  } else {
    (void)close(fd);
  }
  free_trace(ops, n);
  return 0;
}


