add_library(
    vtpc
    SHARED
    vtpc.c
)

target_include_directories(
    vtpc
    PUBLIC
    .
)

set_target_properties(vtpc PROPERTIES POSITION_INDEPENDENT_CODE ON)
