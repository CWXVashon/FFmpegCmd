
cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("ffmpegcmd")

#1. 定义so库和头文件所在目录，方面后面使用
set(ffmpeg_lib_dir ../../../../libs/${CMAKE_ANDROID_ARCH_ABI}/libffmpeg.so)

# 2. 添加头文件目录
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg)
#适配不同架构的include文件
if(${CMAKE_ANDROID_ARCH_ABI} MATCHES "armeabi-v7a")
    include_directories(src/main/cpp/include/armeabi-v7a)
    message("This is armeabi-v7a")
elseif(${CMAKE_ANDROID_ARCH_ABI} MATCHES "arm64-v8a")
    include_directories(src/main/cpp/include/arm64-v8a)
    message("This is arm64-v8a")
endif()

add_library(
        ffmpeg
        SHARED
        IMPORTED
)
set_target_properties(
        ffmpeg
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}
)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             ffmpeg-cmd

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
               ${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/ffmpeg.c
               ${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/ffmpeg_cmd.c
              )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       # Links the target library to the log library
                       # included in the NDK.
        ffmpeg-cmd
        ffmpeg
                       ${log-lib} )