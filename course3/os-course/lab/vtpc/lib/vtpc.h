#pragma once

#include <sys/types.h>

#include <stdint.h>

int vtpc_open(const char* path, int mode, int access);
int vtpc_close(int fd);
ssize_t vtpc_read(int fd, void* buf, size_t count);
ssize_t vtpc_write(int fd, const void* buf, size_t count);
off_t vtpc_lseek(int fd, off_t offset, int whence);
int vtpc_fsync(int fd);

// Variant "Optimal": user can provide hint when the next access to the page
// containing `offset` will happen. Larger value means further in the future.
// Use UINT64_MAX to mean "never".
int vtpc_advice(int fd, off_t offset, uint64_t next_use);

// Lab2-style aliases (so the library can be used with the API from README).
// These are thin wrappers around vtpc_*.
int lab2_open(const char* path);
int lab2_close(int fd);
ssize_t lab2_read(int fd, void* buf, size_t count);
ssize_t lab2_write(int fd, const void* buf, size_t count);
off_t lab2_lseek(int fd, off_t offset, int whence);
int lab2_fsync(int fd);
int lab2_advice(int fd, off_t offset, uint64_t next_use);
