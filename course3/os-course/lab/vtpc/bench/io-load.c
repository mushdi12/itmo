#define _GNU_SOURCE

#include <errno.h>
#include <fcntl.h>
#include <inttypes.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>

#include "vtpc.h"

#define ALIGNMENT 4096
#define BASE_10 10
#define MAX(a, b) ((a) > (b) ? (a) : (b))
#define FILE_MODE 0644
#define NSEC_IN_SEC 1000000000.0
#define ALPHABET_SIZE 26

static const char PREFIX_RW[] = "--rw=";
static const char PREFIX_BLOCK_SIZE[] = "--block_size=";
static const char PREFIX_BLOCK_COUNT[] = "--block_count=";
static const char PREFIX_FILE[] = "--file=";
static const char PREFIX_RANGE[] = "--range=";
static const char PREFIX_DIRECT[] = "--direct=";
static const char PREFIX_TYPE[] = "--type=";
static const char PREFIX_BACKEND[] = "--backend=";  // libc|vtpc

typedef enum { MODE_READ, MODE_WRITE } rw_mode_t;
typedef enum { ORDER_SEQUENCE, ORDER_RANDOM } order_t;
typedef enum { BACKEND_LIBC, BACKEND_VTPC } backend_t;

typedef struct {
  rw_mode_t mode;
  size_t block_size;
  size_t block_count;
  const char* file_path;
  off_t range_start;
  off_t range_end;  // exclusive; 0 означает "до конца файла"
  int use_direct;
  order_t order;
  backend_t backend;
} config_t;

static void print_usage(const char* prog) {
  (void)fprintf(
      stderr,
      "Usage: %s --rw=read|write --block_size=N --block_count=N "
      "--file=PATH [--range=A-B] [--direct=on|off] [--type=sequence|random] "
      "[--backend=libc|vtpc]\n",
      prog
  );
}

static int parse_range(const char* str, off_t* start, off_t* end) {
  char* copy = strdup(str);
  if (copy == NULL) {
    return -1;
  }
  char* dash = strchr(copy, '-');
  if (!dash) {
    free(copy);
    return -1;
  }
  *dash = '\0';
  errno = 0;
  long long start_val = strtoll(copy, NULL, BASE_10);
  long long end_val = strtoll(dash + 1, NULL, BASE_10);
  if (errno != 0 || start_val < 0 || end_val < 0) {
    free(copy);
    return -1;
  }
  *start = (off_t)start_val;
  *end = (off_t)end_val;
  free(copy);
  return 0;
}

// NOLINTNEXTLINE(readability-function-cognitive-complexity)
static int parse_args(int argc, char* argv[], config_t* cfg) {
  // значения по умолчанию
  cfg->mode = MODE_READ;
  cfg->block_size = 0;
  cfg->block_count = 0;
  cfg->file_path = NULL;
  cfg->range_start = 0;
  cfg->range_end = 0;  // 0 означает "до конца файла"
  cfg->use_direct = 0;
  cfg->order = ORDER_SEQUENCE;
  cfg->backend = BACKEND_LIBC;

  for (int i = 1; i < argc; i++) {
    if (strncmp(argv[i], PREFIX_RW, strlen(PREFIX_RW)) == 0) {
      const char* val = argv[i] + strlen(PREFIX_RW);
      if (strcmp(val, "read") == 0) {
        cfg->mode = MODE_READ;
      } else if (strcmp(val, "write") == 0) {
        cfg->mode = MODE_WRITE;
      } else {
        return -1;
      }
    } else if (strncmp(argv[i], PREFIX_BLOCK_SIZE, strlen(PREFIX_BLOCK_SIZE)) == 0) {
      errno = 0;
      long long parsed_block_size =
          strtoll(argv[i] + strlen(PREFIX_BLOCK_SIZE), NULL, BASE_10);
      if (errno != 0 || parsed_block_size <= 0) {
        return -1;
      }
      cfg->block_size = (size_t)parsed_block_size;
    } else if (strncmp(argv[i], PREFIX_BLOCK_COUNT, strlen(PREFIX_BLOCK_COUNT)) == 0) {
      errno = 0;
      long long parsed_block_count =
          strtoll(argv[i] + strlen(PREFIX_BLOCK_COUNT), NULL, BASE_10);
      if (errno != 0 || parsed_block_count <= 0) {
        return -1;
      }
      cfg->block_count = (size_t)parsed_block_count;
    } else if (strncmp(argv[i], PREFIX_FILE, strlen(PREFIX_FILE)) == 0) {
      cfg->file_path = argv[i] + strlen(PREFIX_FILE);
    } else if (strncmp(argv[i], PREFIX_RANGE, strlen(PREFIX_RANGE)) == 0) {
      if (parse_range(argv[i] + strlen(PREFIX_RANGE), &cfg->range_start, &cfg->range_end) != 0) {
        return -1;
      }
    } else if (strncmp(argv[i], PREFIX_DIRECT, strlen(PREFIX_DIRECT)) == 0) {
      const char* val = argv[i] + strlen(PREFIX_DIRECT);
      if (strcmp(val, "on") == 0) {
        cfg->use_direct = 1;
      } else if (strcmp(val, "off") == 0) {
        cfg->use_direct = 0;
      } else {
        return -1;
      }
    } else if (strncmp(argv[i], PREFIX_TYPE, strlen(PREFIX_TYPE)) == 0) {
      const char* val = argv[i] + strlen(PREFIX_TYPE);
      if (strcmp(val, "sequence") == 0) {
        cfg->order = ORDER_SEQUENCE;
      } else if (strcmp(val, "random") == 0) {
        cfg->order = ORDER_RANDOM;
      } else {
        return -1;
      }
    } else if (strncmp(argv[i], PREFIX_BACKEND, strlen(PREFIX_BACKEND)) == 0) {
      const char* val = argv[i] + strlen(PREFIX_BACKEND);
      if (strcmp(val, "libc") == 0) {
        cfg->backend = BACKEND_LIBC;
      } else if (strcmp(val, "vtpc") == 0) {
        cfg->backend = BACKEND_VTPC;
      } else {
        return -1;
      }
    } else {
      return -1;
    }
  }

  if (cfg->block_size == 0 || cfg->block_count == 0 || cfg->file_path == NULL) {
    return -1;
  }

  return 0;
}

static off_t get_file_size(const char* path) {
  struct stat stat_buf;
  if (stat(path, &stat_buf) == 0) {
    return stat_buf.st_size;
  }
  return 0;
}

static int open_file_libc(const config_t* cfg) {
  unsigned int flags = (cfg->mode == MODE_READ) ? O_RDONLY : (O_WRONLY | O_CREAT);
  if (cfg->use_direct) {
    flags |= O_DIRECT;
  }
  int file_descriptor = open(cfg->file_path, (int)flags, FILE_MODE);
  return file_descriptor;
}

static int open_file_vtpc(const config_t* cfg) {
  unsigned int flags = (cfg->mode == MODE_READ) ? O_RDONLY : (O_WRONLY | O_CREAT);
  // vtpc_open сам пытается включить O_DIRECT для обхода page cache ОС.
  // Флаг --direct здесь оставляем только для режима libc.
  return vtpc_open(cfg->file_path, (int)flags, FILE_MODE);
}

static void* alloc_aligned(size_t size) {
  void* ptr = NULL;
  if (posix_memalign(&ptr, ALIGNMENT, size) != 0) {
    return NULL;
  }
  return ptr;
}

static off_t choose_offset_random_byte(off_t start, off_t end, size_t block_size) {
  // случайный режим (любой байтовый оффсет)
  off_t span = end - start;
  if (span < (off_t)block_size) {
    return start;
  }
  // NOLINTNEXTLINE
  off_t r = (off_t)(rand() % (span - (off_t)block_size + 1));
  return start + r;
}

static off_t choose_offset_random_block(off_t start, off_t end, size_t block_size) {
  // случайный режим (оффсет кратен block_size; подходит для O_DIRECT)
  const off_t span = end - start;
  if (span < (off_t)block_size) {
    return start;
  }
  const off_t blocks = (span - (off_t)block_size) / (off_t)block_size + 1;
  // NOLINTNEXTLINE
  const off_t idx = (off_t)(rand() % (int)blocks);
  return start + idx * (off_t)block_size;
}

// NOLINTNEXTLINE
static off_t choose_offset(order_t order, off_t start, off_t end, size_t block_size, int aligned) {
  if (order == ORDER_SEQUENCE) {
    return start;  // последовательный режим: смещение накапливается снаружи
  }
  if (aligned) {
    return choose_offset_random_block(start, end, block_size);
  }
  return choose_offset_random_byte(start, end, block_size);
}

static double elapsed_sec(struct timespec start_ts, struct timespec end_ts) {
  const double seconds = (double)(end_ts.tv_sec - start_ts.tv_sec);
  const double nanoseconds = (double)(end_ts.tv_nsec - start_ts.tv_nsec) / NSEC_IN_SEC;
  return seconds + nanoseconds;
}

enum { MAP_EMPTY = 0, MAP_USED = 1, MAP_TOMB = 2 };

typedef struct {
  uint64_t key;
  uint64_t value;
  unsigned char state;
} map_entry_t;

typedef struct {
  size_t cap;
  map_entry_t* entries;
} u64u64_map_t;

static uint64_t hash_u64(uint64_t x) {
  x += 0x9e3779b97f4a7c15ULL;
  x = (x ^ (x >> 30U)) * 0xbf58476d1ce4e5b9ULL;
  x = (x ^ (x >> 27U)) * 0x94d049bb133111ebULL;
  return x ^ (x >> 31U);
}

static int map_init(u64u64_map_t* m, size_t cap_pow2) {
  m->cap = cap_pow2;
  m->entries = (map_entry_t*)calloc(cap_pow2, sizeof(map_entry_t));
  return m->entries == NULL ? -1 : 0;
}

static void map_free(u64u64_map_t* m) {
  free(m->entries);
  m->entries = NULL;
  m->cap = 0;
}

static int map_get(const u64u64_map_t* m, uint64_t key, uint64_t* out_value) {
  if (m->cap == 0) {
    return 0;
  }
  const size_t mask = m->cap - 1;
  size_t idx = (size_t)hash_u64(key) & mask;
  for (size_t i = 0; i < m->cap; ++i) {
    const map_entry_t* e = &m->entries[idx];
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

static int map_put(u64u64_map_t* m, uint64_t key, uint64_t value) {
  const size_t mask = m->cap - 1;
  size_t idx = (size_t)hash_u64(key) & mask;
  size_t first_tomb = (size_t)(-1);
  for (size_t i = 0; i < m->cap; ++i) {
    map_entry_t* e = &m->entries[idx];
    if (e->state == MAP_EMPTY) {
      if (first_tomb != (size_t)(-1)) {
        e = &m->entries[first_tomb];
      }
      e->state = MAP_USED;
      e->key = key;
      e->value = value;
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

static uint64_t get_vtpc_page_size(void) {
  const char* v = getenv("VTPC_PAGE_SIZE");
  if (v == NULL || v[0] == '\0') {
    return 4096;
  }
  errno = 0;
  char* end = NULL;
  unsigned long long parsed = strtoull(v, &end, 10);
  if (errno != 0 || end == v || parsed < 512ULL) {
    return 4096;
  }
  // must be power of two
  uint64_t ps = (uint64_t)parsed;
  if ((ps & (ps - 1)) != 0) {
    return 4096;
  }
  return ps;
}

typedef struct {
  size_t op_count;
  size_t* hint_off;      // size op_count+1
  uint64_t* hint_page;   // flat
  uint64_t* hint_next;   // flat
  size_t hint_total;
} optimal_hints_t;

static void hints_free(optimal_hints_t* h) {
  free(h->hint_off);
  free(h->hint_page);
  free(h->hint_next);
  memset(h, 0, sizeof(*h));
}

static int hints_build(
    const off_t* offsets,
    size_t op_count,
    size_t block_size,
    off_t range_start,
    off_t range_end,
    uint64_t page_size,
    optimal_hints_t* out
) {
  (void)range_start;
  (void)range_end;

  size_t* pages_per_op = (size_t*)calloc(op_count, sizeof(size_t));
  if (pages_per_op == NULL) {
    return -1;
  }

  size_t total = 0;
  for (size_t i = 0; i < op_count; ++i) {
    const off_t off = offsets[i];
    const uint64_t start = (uint64_t)off / page_size;
    const uint64_t end = ((uint64_t)off + (uint64_t)block_size - 1) / page_size;
    const size_t pages = (size_t)(end - start + 1);
    pages_per_op[i] = pages;
    total += pages;
  }

  size_t* hint_off = (size_t*)malloc((op_count + 1) * sizeof(size_t));
  uint64_t* hint_page = (uint64_t*)malloc(total * sizeof(uint64_t));
  uint64_t* hint_next = (uint64_t*)malloc(total * sizeof(uint64_t));
  if (hint_off == NULL || hint_page == NULL || hint_next == NULL) {
    free(pages_per_op);
    free(hint_off);
    free(hint_page);
    free(hint_next);
    return -1;
  }

  hint_off[0] = 0;
  for (size_t i = 0; i < op_count; ++i) {
    hint_off[i + 1] = hint_off[i] + pages_per_op[i];
  }

  // Map capacity estimate: pages in span *2
  const uint64_t span = (range_end > 0) ? (uint64_t)(range_end - range_start) : 0;
  uint64_t pages_in_span = span == 0 ? (uint64_t)op_count : (span + page_size - 1) / page_size;
  if (pages_in_span < 1024) {
    pages_in_span = 1024;
  }
  size_t map_cap = 1;
  while ((uint64_t)map_cap < pages_in_span * 2ULL) {
    map_cap <<= 1;
    if (map_cap >= (1U << 26U)) {
      break;
    }
  }

  u64u64_map_t next;
  if (map_init(&next, map_cap) != 0) {
    free(pages_per_op);
    free(hint_off);
    free(hint_page);
    free(hint_next);
    return -1;
  }

  for (size_t ti = op_count; ti-- > 0;) {
    const off_t off = offsets[ti];
    const uint64_t start = (uint64_t)off / page_size;
    const uint64_t end = ((uint64_t)off + (uint64_t)block_size - 1) / page_size;
    const size_t pages = (size_t)(end - start + 1);

    for (size_t j = 0; j < pages; ++j) {
      const uint64_t page = start + (uint64_t)j;
      uint64_t nu = UINT64_MAX;
      (void)map_get(&next, page, &nu);

      const size_t idx = hint_off[ti] + j;
      hint_page[idx] = page;
      hint_next[idx] = nu;

      if (map_put(&next, page, (uint64_t)ti) != 0) {
        map_free(&next);
        free(pages_per_op);
        free(hint_off);
        free(hint_page);
        free(hint_next);
        return -1;
      }
    }
  }

  map_free(&next);
  free(pages_per_op);

  out->op_count = op_count;
  out->hint_off = hint_off;
  out->hint_page = hint_page;
  out->hint_next = hint_next;
  out->hint_total = total;
  return 0;
}

// NOLINTNEXTLINE(readability-function-cognitive-complexity)
int main(int argc, char* argv[]) {
  config_t cfg;
  if (parse_args(argc, argv, &cfg) != 0) {
    print_usage(argv[0]);
    return 1;
  }

  if (cfg.range_end != 0 && cfg.range_end <= cfg.range_start) {
    (void)fprintf(stderr, "Invalid range\n");
    return 1;
  }

  const int libc_direct = (cfg.backend == BACKEND_LIBC && cfg.use_direct);
  if (libc_direct && (cfg.block_size % ALIGNMENT != 0)) {
    (void)fprintf(stderr, "For O_DIRECT block_size must be aligned to %d\n", ALIGNMENT);
    return 1;
  }
  if (libc_direct && (cfg.range_start % ALIGNMENT != 0)) {
    (void)fprintf(stderr, "For O_DIRECT range_start must be aligned to %d\n", ALIGNMENT);
    return 1;
  }

  int file_descriptor = -1;
  if (cfg.backend == BACKEND_LIBC) {
    file_descriptor = open_file_libc(&cfg);
  } else {
    file_descriptor = open_file_vtpc(&cfg);
  }
  if (file_descriptor == -1) {
    perror("open");
    return 1;
  }

  off_t file_size = get_file_size(cfg.file_path);
  if (cfg.range_end == 0) {
    if (cfg.mode == MODE_READ && file_size == 0) {
      (void)fprintf(stderr, "File is empty, specify range explicitly\n");
      (void)(cfg.backend == BACKEND_VTPC ? vtpc_close(file_descriptor) : close(file_descriptor));
      return 1;
    }
    if (cfg.mode == MODE_WRITE && file_size == 0) {
      cfg.range_end = cfg.range_start + (off_t)cfg.block_size * (off_t)cfg.block_count;
    } else {
      cfg.range_end = MAX(file_size, cfg.range_start + (off_t)cfg.block_size);
    }
  }
  if (libc_direct && (cfg.range_end % ALIGNMENT != 0)) {
    (void)fprintf(stderr, "For O_DIRECT range_end must be aligned to %d\n", ALIGNMENT);
    (void)(cfg.backend == BACKEND_VTPC ? vtpc_close(file_descriptor) : close(file_descriptor));
    return 1;
  }

  if ((cfg.range_end - cfg.range_start) < (off_t)cfg.block_size) {
    (void)fprintf(stderr, "Range is too small for given block_size\n");
    (void)(cfg.backend == BACKEND_VTPC ? vtpc_close(file_descriptor) : close(file_descriptor));
    return 1;
  }

  void* buffer = alloc_aligned(cfg.block_size);
  if (!buffer) {
    perror("alloc");
    (void)(cfg.backend == BACKEND_VTPC ? vtpc_close(file_descriptor) : close(file_descriptor));
    return 1;
  }

  // заполняем буфер данными для записи
  if (cfg.mode == MODE_WRITE) {
    for (size_t i = 0; i < cfg.block_size; i++) {
      ((char*)buffer)[i] = (char)('A' + (i % ALPHABET_SIZE));
    }
  }

  // NOLINTNEXTLINE
  srand((unsigned int)time(NULL));

  // Предгенерируем смещения (чтобы можно было сделать Optimal-подсказки)
  off_t* offsets = (off_t*)malloc(cfg.block_count * sizeof(off_t));
  if (offsets == NULL) {
    perror("malloc offsets");
    free(buffer);
    (void)(cfg.backend == BACKEND_VTPC ? vtpc_close(file_descriptor) : close(file_descriptor));
    return 1;
  }

  off_t seq_offset = cfg.range_start;
  for (size_t i = 0; i < cfg.block_count; i++) {
    off_t offset = cfg.range_start;
    if (cfg.order == ORDER_SEQUENCE) {
      offset = seq_offset;
      seq_offset += (off_t)cfg.block_size;
      if (seq_offset + (off_t)cfg.block_size > cfg.range_end) {
        seq_offset = cfg.range_start;  // циклически начинаем сначала
      }
    } else {
      offset = choose_offset(cfg.order, cfg.range_start, cfg.range_end, cfg.block_size, libc_direct);
    }
    offsets[i] = offset;
  }

  // Подсказки Optimal (только для backend=vtpc)
  optimal_hints_t hints;
  memset(&hints, 0, sizeof(hints));
  const uint64_t page_size = get_vtpc_page_size();
  if (cfg.backend == BACKEND_VTPC) {
    if (hints_build(
            offsets,
            cfg.block_count,
            cfg.block_size,
            cfg.range_start,
            cfg.range_end,
            page_size,
            &hints
        ) != 0) {
      perror("hints_build");
      free(offsets);
      free(buffer);
      (void)vtpc_close(file_descriptor);
      return 1;
    }
  }

  struct timespec t_start = {0};
  struct timespec t_end = {0};
  (void)clock_gettime(CLOCK_MONOTONIC, &t_start);

  for (size_t i = 0; i < cfg.block_count; i++) {
    off_t offset = offsets[i];

    // Перед доступом: сообщаем next_use для всех затронутых страниц (Optimal)
    if (cfg.backend == BACKEND_VTPC) {
      const size_t a = hints.hint_off[i];
      const size_t b = hints.hint_off[i + 1];
      for (size_t k = a; k < b; ++k) {
        const off_t page_off = (off_t)(hints.hint_page[k] * page_size);
        if (vtpc_advice(file_descriptor, page_off, hints.hint_next[k]) != 0) {
          perror("vtpc_advice");
          hints_free(&hints);
          free(offsets);
          free(buffer);
          (void)vtpc_close(file_descriptor);
          return 1;
        }
      }
    }

    ssize_t io_result = 0;
    if (cfg.backend == BACKEND_LIBC) {
      if (cfg.mode == MODE_READ) {
        io_result = pread(file_descriptor, buffer, cfg.block_size, offset);
      } else {
        io_result = pwrite(file_descriptor, buffer, cfg.block_size, offset);
      }
    } else {
      if (vtpc_lseek(file_descriptor, offset, SEEK_SET) == (off_t)-1) {
        perror("vtpc_lseek");
        hints_free(&hints);
        free(offsets);
        free(buffer);
        (void)vtpc_close(file_descriptor);
        return 1;
      }
      if (cfg.mode == MODE_READ) {
        io_result = vtpc_read(file_descriptor, buffer, cfg.block_size);
      } else {
        io_result = vtpc_write(file_descriptor, buffer, cfg.block_size);
      }
    }

    if (io_result < 0) {
      perror("I/O error");
      hints_free(&hints);
      free(offsets);
      free(buffer);
      (void)(cfg.backend == BACKEND_VTPC ? vtpc_close(file_descriptor) : close(file_descriptor));
      return 1;
    }
    if ((size_t)io_result != cfg.block_size) {
      (void)fprintf(stderr, "Short I/O at offset %ld\n", (long)offset);
      hints_free(&hints);
      free(offsets);
      free(buffer);
      (void)(cfg.backend == BACKEND_VTPC ? vtpc_close(file_descriptor) : close(file_descriptor));
      return 1;
    }
  }

  (void)clock_gettime(CLOCK_MONOTONIC, &t_end);
  double elapsed = elapsed_sec(t_start, t_end);
  double total_bytes = (double)cfg.block_size * (double)cfg.block_count;
  const double KIB_IN_MIB = 1024.0;
  double mbps = (elapsed > 0.0) ? (total_bytes / (KIB_IN_MIB * KIB_IN_MIB)) / elapsed : 0.0;

  (void)printf(
      "Backend: %s\n"
      "Mode: %s\n"
      "Order: %s\n"
      "Block size: %zu bytes\n"
      "Block count: %zu\n"
      "Range: %ld-%ld\n"
      "Direct I/O (libc only): %s\n"
      "VTPC page size (for hints): %" PRIu64 "\n"
      "Elapsed: %.3f s\n"
      "Throughput: %.2f MB/s\n",
      (cfg.backend == BACKEND_VTPC) ? "vtpc" : "libc",
      (cfg.mode == MODE_READ) ? "read" : "write",
      (cfg.order == ORDER_SEQUENCE) ? "sequence" : "random",
      cfg.block_size,
      cfg.block_count,
      (long)cfg.range_start,
      (long)cfg.range_end,
      cfg.use_direct ? "on" : "off",
      page_size,
      elapsed,
      mbps
  );

  hints_free(&hints);
  free(offsets);
  free(buffer);
  (void)(cfg.backend == BACKEND_VTPC ? vtpc_close(file_descriptor) : close(file_descriptor));
  return 0;
}


