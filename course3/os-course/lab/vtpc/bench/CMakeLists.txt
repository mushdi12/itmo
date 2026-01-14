add_executable(vtpc_loader_optimal loader_optimal.c)
target_link_libraries(vtpc_loader_optimal PRIVATE vtpc)
target_include_directories(vtpc_loader_optimal PRIVATE ../lib)

add_executable(io-load io-load.c)
target_link_libraries(io-load PRIVATE vtpc)
target_include_directories(io-load PRIVATE ../lib)
target_compile_options(io-load PRIVATE -Wall -Wextra -Wpedantic)

