cmake_minimum_required(VERSION 3.4.1)

aux_source_directory(src/main/cpu CPU_PATH)

add_library( # Sets the name of the library.
             cpu-info

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpu/CpuJniBridge.c src/main/cpu/cpu-features.c)


find_library(log-lib log)


target_link_libraries(cpu-info
                      ${log-lib})