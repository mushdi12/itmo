#include "vtpc.h"

#include <errno.h>
#include <fcntl.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#ifndef O_DIRECT
#define O_DIRECT 040000
#endif

#define VTPC_FD_BASE 10000
#define VTPC_MAX_FILES 1024

#define VTPC_DEFAULT_PAGE_SIZE 4096
#define VTPC_DEFAULT_CACHE_PAGES 64

enum {
  VTPC_MIN_PAGE_SIZE = 512,
  VTPC_MIN_MAP_CAP = 8,
  VTPC_DEFAULT_FILE_MODE = 0666,
};

static const uint64_t VTPC_SPLITMIX64_GAMMA = 0x9e3779b97f4a7c15ULL;
static const uint64_t VTPC_SPLITMIX64_MUL1 = 0xbf58476d1ce4e5b9ULL;
static const uint64_t VTPC_SPLITMIX64_MUL2 = 0x94d049bb133111ebULL;
static const unsigned VTPC_SPLITMIX64_SHIFT1 = 30U;
static const unsigned VTPC_SPLITMIX64_SHIFT2 = 27U;
static const unsigned VTPC_SPLITMIX64_SHIFT3 = 31U;

static size_t vtpc_min_size(size_t a, size_t b) {
  return a < b ? a : b;
}

static uint64_t vtpc_env_u64(const char* name, uint64_t def) {
  const char* v = getenv(name);  // NOLINT(concurrency-mt-unsafe)
  if (v == NULL || v[0] == '\0') {
    return def;
  }
  errno = 0;
  char* end = NULL;
  const unsigned long long parsed = strtoull(v, &end, 10);
  if (errno != 0 || end == v) {
    return def;
  }
  return (uint64_t)parsed;
}

enum { MAP_EMPTY = 0, MAP_USED = 1, MAP_TOMB = 2 };

struct map_entry {
  uint64_t key;
  uint64_t value;
  unsigned char state;
};

struct u64i_map {
  size_t cap;
  size_t size;
  struct map_entry* entries;
};

static uint64_t vtpc_hash_u64(uint64_t x) {
  // SplitMix64
  x += VTPC_SPLITMIX64_GAMMA;
  x = (x ^ (x >> VTPC_SPLITMIX64_SHIFT1)) * VTPC_SPLITMIX64_MUL1;
  x = (x ^ (x >> VTPC_SPLITMIX64_SHIFT2)) * VTPC_SPLITMIX64_MUL2;
  return x ^ (x >> VTPC_SPLITMIX64_SHIFT3);
}

static int map_init(struct u64i_map* m, size_t cap) {
  m->cap = cap;
  m->size = 0;
  m->entries = (struct map_entry*)calloc(cap, sizeof(struct map_entry));
  if (m->entries == NULL) {
    return -1;
  }
  return 0;
}

static void map_free(struct u64i_map* m) {
  free(m->entries);
  m->entries = NULL;
  m->cap = 0;
  m->size = 0;
}

static int map_get(const struct u64i_map* m, uint64_t key, uint64_t* out_value) {
  if (m->cap == 0) {
    return 0;
  }
  const size_t mask = m->cap - 1;
  size_t idx = (size_t)vtpc_hash_u64(key) & mask;
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

static int map_put(struct u64i_map* m, uint64_t key, uint64_t value) {
  if (m->cap == 0) {
    errno = ENOMEM;
    return -1;
  }
  const size_t mask = m->cap - 1;
  size_t idx = (size_t)vtpc_hash_u64(key) & mask;
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

static int map_del(struct u64i_map* m, uint64_t key) {
  if (m->cap == 0) {
    return 0;
  }
  const size_t mask = m->cap - 1;
  size_t idx = (size_t)vtpc_hash_u64(key) & mask;
  for (size_t i = 0; i < m->cap; ++i) {
    struct map_entry* e = &m->entries[idx];
    if (e->state == MAP_EMPTY) {
      return 0;
    }
    if (e->state == MAP_USED && e->key == key) {
      e->state = MAP_TOMB;
      m->size--;
      return 1;
    }
    idx = (idx + 1) & mask;
  }
  return 0;
}

struct vtpc_page {
  bool used;
  uint64_t page_index;
  char* data;  // aligned buffer of page_size
  bool dirty;
  uint64_t next_use;     // Optimal: next access time, UINT64_MAX means never
  uint64_t last_access;  // tie-breaker
};

struct vtpc_cache {
  size_t page_size;
  size_t cap_pages;
  struct vtpc_page* pages;
  struct u64i_map index_to_slot;
  struct u64i_map hints;  // page_index -> next_use
  uint64_t access_seq;
};

struct vtpc_file {
  int os_fd;
  bool direct_io;
  int accmode;
  bool append;
  off_t pos;
  off_t logical_size;
  struct vtpc_cache cache;
};

static struct vtpc_file* g_files[VTPC_MAX_FILES];

static int vtpc_fd_to_index(int fd) {
  if (fd < VTPC_FD_BASE) {
    return -1;
  }
  const int idx = fd - VTPC_FD_BASE;
  if (idx < 0 || idx >= VTPC_MAX_FILES) {
    return -1;
  }
  return idx;
}

static struct vtpc_file* vtpc_get_file(int fd) {
  const int idx = vtpc_fd_to_index(fd);
  if (idx < 0) {
    errno = EBADF;
    return NULL;
  }
  struct vtpc_file* f = g_files[idx];
  if (f == NULL) {
    errno = EBADF;
    return NULL;
  }
  return f;
}

static void vtpc_cache_page_free(struct vtpc_cache* c) {
  if (c->pages != NULL) {
    for (size_t i = 0; i < c->cap_pages; ++i) {
      free(c->pages[i].data);
      c->pages[i].data = NULL;
    }
  }
  free(c->pages);
  c->pages = NULL;
  c->cap_pages = 0;
  map_free(&c->index_to_slot);
  map_free(&c->hints);
}

static int vtpc_cache_init(struct vtpc_cache* c, size_t page_size, size_t cap_pages) {
  memset(c, 0, sizeof(*c));
  c->page_size = page_size;
  c->cap_pages = cap_pages;
  c->access_seq = 1;

  c->pages = (struct vtpc_page*)calloc(cap_pages, sizeof(struct vtpc_page));
  if (c->pages == NULL) {
    return -1;
  }

  // Hash map capacity: power of two, at least 2 * cap_pages.
  size_t map_cap = 1;
  while (map_cap < cap_pages * 2) {
    map_cap <<= 1U;
  }
  if (map_cap < VTPC_MIN_MAP_CAP) {
    map_cap = VTPC_MIN_MAP_CAP;
  }
  if (map_init(&c->index_to_slot, map_cap) != 0) {
    vtpc_cache_page_free(c);
    return -1;
  }
  if (map_init(&c->hints, map_cap) != 0) {
    vtpc_cache_page_free(c);
    return -1;
  }

  for (size_t i = 0; i < cap_pages; ++i) {
    void* mem = NULL;
    if (posix_memalign(&mem, page_size, page_size) != 0) {
      errno = ENOMEM;
      vtpc_cache_page_free(c);
      return -1;
    }
    c->pages[i].used = false;
    c->pages[i].page_index = 0;
    c->pages[i].data = (char*)mem;
    c->pages[i].dirty = false;
    c->pages[i].next_use = UINT64_MAX;
    c->pages[i].last_access = 0;
    memset(c->pages[i].data, 0, page_size);
  }
  return 0;
}

static ssize_t vtpc_pread_full(int fd, void* buf, size_t size, off_t off) {
  size_t done = 0;
  while (done < size) {
    const ssize_t r = pread(fd, (char*)buf + done, size - done, off + (off_t)done);
    if (r < 0) {
      return -1;
    }
    if (r == 0) {
      break;
    }
    done += (size_t)r;
  }
  return (ssize_t)done;
}

static ssize_t vtpc_pwrite_full(int fd, const void* buf, size_t size, off_t off) {
  size_t done = 0;
  while (done < size) {
    const ssize_t r =
        pwrite(fd, (const char*)buf + done, size - done, off + (off_t)done);
    if (r < 0) {
      return -1;
    }
    if (r == 0) {
      errno = EIO;
      return -1;
    }
    done += (size_t)r;
  }
  return (ssize_t)done;
}

static ssize_t vtpc_pread_page(struct vtpc_file* f, void* buf, size_t size, off_t off) {
  // For O_DIRECT, do a single aligned read; looping could cause unaligned tails at EOF.
  if (f->direct_io) {
    for (;;) {
      const ssize_t r = pread(f->os_fd, buf, size, off);
      if (r < 0 && errno == EINTR) {
        continue;
      }
      return r;
    }
  }
  return vtpc_pread_full(f->os_fd, buf, size, off);
}

static int vtpc_pwrite_page(struct vtpc_file* f, const void* buf, size_t size, off_t off) {
  if (f->direct_io) {
    for (;;) {
      const ssize_t r = pwrite(f->os_fd, buf, size, off);
      if (r < 0 && errno == EINTR) {
        continue;
      }
      if (r < 0) {
        return -1;
      }
      if ((size_t)r != size) {
        errno = EIO;
        return -1;
      }
      return 0;
    }
  }
  return (vtpc_pwrite_full(f->os_fd, buf, size, off) < 0) ? -1 : 0;
}

static int vtpc_load_page(struct vtpc_file* f, struct vtpc_page* p) {
  const off_t off = (off_t)(p->page_index * (uint64_t)f->cache.page_size);
  memset(p->data, 0, f->cache.page_size);

  // If the page starts beyond logical EOF, it's logically zero-filled.
  if (off >= f->logical_size) {
    return 0;
  }

  const ssize_t r = vtpc_pread_page(f, p->data, f->cache.page_size, off);
  if (r < 0) {
    return -1;
  }
  if ((size_t)r < f->cache.page_size) {
    memset(p->data + r, 0, f->cache.page_size - (size_t)r);
  }
  // Best-effort drop OS cache when O_DIRECT isn't available.
  (void)posix_fadvise(f->os_fd, off, (off_t)f->cache.page_size, POSIX_FADV_DONTNEED);
  return 0;
}

static int vtpc_flush_page(struct vtpc_file* f, struct vtpc_page* p) {
  if (!p->dirty) {
    return 0;
  }
  const off_t off = (off_t)(p->page_index * (uint64_t)f->cache.page_size);
  if (vtpc_pwrite_page(f, p->data, f->cache.page_size, off) != 0) {
    return -1;
  }
  p->dirty = false;
  (void)posix_fadvise(f->os_fd, off, (off_t)f->cache.page_size, POSIX_FADV_DONTNEED);
  return 0;
}

static int vtpc_cache_choose_victim(struct vtpc_cache* c) {
  // Choose the page with the farthest next_use (Belady). On ties, evict LRU.
  int victim = -1;
  uint64_t best_next = 0;
  uint64_t best_last = 0;
  for (size_t i = 0; i < c->cap_pages; ++i) {
    if (!c->pages[i].used) {
      return (int)i;
    }
    if (victim == -1) {
      victim = (int)i;
      best_next = c->pages[i].next_use;
      best_last = c->pages[i].last_access;
      continue;
    }
    const uint64_t nu = c->pages[i].next_use;
    const uint64_t la = c->pages[i].last_access;
    if (nu > best_next || (nu == best_next && la < best_last)) {
      victim = (int)i;
      best_next = nu;
      best_last = la;
    }
  }
  return victim;
}

static int vtpc_cache_get_slot(struct vtpc_file* f, uint64_t page_index, int* out_slot) {
  uint64_t slot_u64 = 0;
  if (map_get(&f->cache.index_to_slot, page_index, &slot_u64) != 0) {
    *out_slot = (int)slot_u64;
    return 0;
  }

  const int victim = vtpc_cache_choose_victim(&f->cache);
  if (victim < 0) {
    errno = ENOMEM;
    return -1;
  }

  struct vtpc_page* p = &f->cache.pages[victim];
  if (p->used) {
    if (vtpc_flush_page(f, p) != 0) {
      return -1;
    }
    (void)map_del(&f->cache.index_to_slot, p->page_index);
  }

  // Prepare new page slot
  p->used = true;
  p->page_index = page_index;
  p->dirty = false;
  p->last_access = 0;
  p->next_use = UINT64_MAX;

  uint64_t hinted = 0;
  if (map_get(&f->cache.hints, page_index, &hinted) != 0) {
    p->next_use = hinted;
    (void)map_del(&f->cache.hints, page_index);
  }

  if (map_put(&f->cache.index_to_slot, page_index, (uint64_t)victim) != 0) {
    return -1;
  }

  if (vtpc_load_page(f, p) != 0) {
    (void)map_del(&f->cache.index_to_slot, page_index);
    p->used = false;
    return -1;
  }

  *out_slot = victim;
  return 0;
}

static int vtpc_cache_touch(struct vtpc_file* f, struct vtpc_page* p) {
  p->last_access = f->cache.access_seq++;
  return 0;
}

static int vtpc_flush_all(struct vtpc_file* f) {
  for (size_t i = 0; i < f->cache.cap_pages; ++i) {
    if (f->cache.pages[i].used && f->cache.pages[i].dirty) {
      if (vtpc_flush_page(f, &f->cache.pages[i]) != 0) {
        return -1;
      }
    }
  }

  // Restore correct logical size (writes may have used full-page direct IO).
  if (ftruncate(f->os_fd, f->logical_size) != 0) {
    return -1;
  }
  return 0;
}

int vtpc_open(const char* path, int mode, int access) {
  size_t page_size = (size_t)vtpc_env_u64("VTPC_PAGE_SIZE", VTPC_DEFAULT_PAGE_SIZE);
  if (page_size < VTPC_MIN_PAGE_SIZE || (page_size & (page_size - 1)) != 0 ||
      (page_size % sizeof(void*)) != 0) {
    page_size = VTPC_DEFAULT_PAGE_SIZE;
  }
  size_t cache_pages = (size_t)vtpc_env_u64("VTPC_CACHE_PAGES", VTPC_DEFAULT_CACHE_PAGES);
  if (cache_pages == 0) {
    cache_pages = VTPC_DEFAULT_CACHE_PAGES;
  }

  const unsigned int umode = (unsigned int)mode;
  const int user_accmode = (int)(umode & (unsigned int)O_ACCMODE);
  unsigned int u_os_mode = umode;
  // For write-only user mode we still need read access internally to implement
  // read-modify-write for partial page writes.
  if (user_accmode == O_WRONLY) {
    u_os_mode = (umode & ~(unsigned int)O_ACCMODE) | (unsigned int)O_RDWR;
  }
  const int os_mode = (int)u_os_mode;
  const int os_mode_direct = (int)(u_os_mode | (unsigned int)O_DIRECT);

  int os_fd = open(path, os_mode_direct, (mode_t)access);
  bool direct = true;
  if (os_fd < 0) {
    const int err = errno;
    if (err == EINVAL || err == EOPNOTSUPP || err == ENOTTY) {
      os_fd = open(path, os_mode, (mode_t)access);
      direct = false;
    }
  }
  if (os_fd < 0) {
    return -1;
  }

  struct stat st;
  if (fstat(os_fd, &st) != 0) {
    const int err = errno;
    (void)close(os_fd);
    errno = err;
    return -1;
  }

  int slot = -1;
  for (int i = 0; i < VTPC_MAX_FILES; ++i) {
    if (g_files[i] == NULL) {
      slot = i;
      break;
    }
  }
  if (slot < 0) {
    (void)close(os_fd);
    errno = EMFILE;
    return -1;
  }

  struct vtpc_file* f = (struct vtpc_file*)calloc(1, sizeof(struct vtpc_file));
  if (f == NULL) {
    (void)close(os_fd);
    errno = ENOMEM;
    return -1;
  }

  f->os_fd = os_fd;
  f->direct_io = direct;
  f->accmode = user_accmode;
  f->append = (umode & (unsigned int)O_APPEND) != 0U;
  f->pos = 0;
  f->logical_size = st.st_size;

  if (vtpc_cache_init(&f->cache, page_size, cache_pages) != 0) {
    const int err = errno;
    free(f);
    (void)close(os_fd);
    errno = err;
    return -1;
  }

  g_files[slot] = f;
  return VTPC_FD_BASE + slot;
}

int vtpc_close(int fd) {
  const int idx = vtpc_fd_to_index(fd);
  if (idx < 0 || g_files[idx] == NULL) {
    errno = EBADF;
    return -1;
  }

  struct vtpc_file* f = g_files[idx];
  g_files[idx] = NULL;

  int ret = 0;
  int saved = 0;
  if (vtpc_flush_all(f) != 0) {
    ret = -1;
    saved = errno;
  }

  if (close(f->os_fd) != 0) {
    ret = -1;
    if (saved == 0) {
      saved = errno;
    }
  }

  vtpc_cache_page_free(&f->cache);
  free(f);

  if (ret != 0) {
    errno = saved;
  }
  return ret;
}

ssize_t vtpc_read(int fd, void* buf, size_t count) {
  struct vtpc_file* f = vtpc_get_file(fd);
  if (f == NULL) {
    return -1;
  }
  if (f->accmode == O_WRONLY) {
    errno = EBADF;
    return -1;
  }
  if (count == 0) {
    return 0;
  }
  if (f->pos >= f->logical_size) {
    return 0;
  }

  const uint64_t max_can =
      (uint64_t)(f->logical_size - f->pos) < (uint64_t)count
          ? (uint64_t)(f->logical_size - f->pos)
          : (uint64_t)count;
  size_t remaining = (size_t)max_can;

  size_t total = 0;
  while (remaining > 0) {
    const uint64_t page_index = (uint64_t)f->pos / (uint64_t)f->cache.page_size;
    const size_t page_off = (size_t)((uint64_t)f->pos % (uint64_t)f->cache.page_size);
    const size_t chunk = vtpc_min_size(remaining, f->cache.page_size - page_off);

    int slot = -1;
    if (vtpc_cache_get_slot(f, page_index, &slot) != 0) {
      return -1;
    }
    struct vtpc_page* p = &f->cache.pages[slot];
    (void)vtpc_cache_touch(f, p);

    memcpy((char*)buf + total, p->data + page_off, chunk);
    total += chunk;
    remaining -= chunk;
    f->pos += (off_t)chunk;
  }
  return (ssize_t)total;
}

ssize_t vtpc_write(int fd, const void* buf, size_t count) {
  struct vtpc_file* f = vtpc_get_file(fd);
  if (f == NULL) {
    return -1;
  }
  if (f->accmode == O_RDONLY) {
    errno = EBADF;
    return -1;
  }
  if (count == 0) {
    return 0;
  }
  if (f->append) {
    f->pos = f->logical_size;
  }

  size_t remaining = count;
  size_t total = 0;
  while (remaining > 0) {
    const uint64_t page_index = (uint64_t)f->pos / (uint64_t)f->cache.page_size;
    const size_t page_off = (size_t)((uint64_t)f->pos % (uint64_t)f->cache.page_size);
    const size_t chunk = vtpc_min_size(remaining, f->cache.page_size - page_off);

    int slot = -1;
    if (vtpc_cache_get_slot(f, page_index, &slot) != 0) {
      return -1;
    }
    struct vtpc_page* p = &f->cache.pages[slot];
    (void)vtpc_cache_touch(f, p);

    memcpy(p->data + page_off, (const char*)buf + total, chunk);
    p->dirty = true;

    total += chunk;
    remaining -= chunk;
    f->pos += (off_t)chunk;
    const off_t end = f->pos;
    if (end > f->logical_size) {
      f->logical_size = end;
    }
  }
  return (ssize_t)total;
}

off_t vtpc_lseek(int fd, off_t offset, int whence) {
  struct vtpc_file* f = vtpc_get_file(fd);
  if (f == NULL) {
    return (off_t)-1;
  }

  off_t base = 0;
  if (whence == SEEK_SET) {
    base = 0;
  } else if (whence == SEEK_CUR) {
    base = f->pos;
  } else if (whence == SEEK_END) {
    base = f->logical_size;
  } else {
    errno = EINVAL;
    return (off_t)-1;
  }

  const off_t new_pos = base + offset;
  if (new_pos < 0) {
    errno = EINVAL;
    return (off_t)-1;
  }
  f->pos = new_pos;
  return f->pos;
}

int vtpc_fsync(int fd) {
  struct vtpc_file* f = vtpc_get_file(fd);
  if (f == NULL) {
    return -1;
  }
  if (vtpc_flush_all(f) != 0) {
    return -1;
  }
  return fsync(f->os_fd);
}

int vtpc_advice(int fd, off_t offset, uint64_t next_use) {
  struct vtpc_file* f = vtpc_get_file(fd);
  if (f == NULL) {
    return -1;
  }
  if (offset < 0) {
    errno = EINVAL;
    return -1;
  }
  const uint64_t page_index = (uint64_t)offset / (uint64_t)f->cache.page_size;

  uint64_t slot_u64 = 0;
  if (map_get(&f->cache.index_to_slot, page_index, &slot_u64) != 0) {
    f->cache.pages[(int)slot_u64].next_use = next_use;
    return 0;
  }

  // Store hint for future loads.
  return map_put(&f->cache.hints, page_index, next_use);
}

int lab2_open(const char* path) {
  return vtpc_open(path, O_RDWR | O_CREAT, VTPC_DEFAULT_FILE_MODE);
}

int lab2_close(int fd) {
  return vtpc_close(fd);
}

ssize_t lab2_read(int fd, void* buf, size_t count) {
  return vtpc_read(fd, buf, count);
}

ssize_t lab2_write(int fd, const void* buf, size_t count) {
  return vtpc_write(fd, buf, count);
}

off_t lab2_lseek(int fd, off_t offset, int whence) {
  return vtpc_lseek(fd, offset, whence);
}

int lab2_fsync(int fd) {
  return vtpc_fsync(fd);
}

int lab2_advice(int fd, off_t offset, uint64_t next_use) {
  return vtpc_advice(fd, offset, next_use);
}
