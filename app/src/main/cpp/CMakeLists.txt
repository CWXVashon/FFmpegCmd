cmake_minimum_required(VERSION 3.10.2)

project("mylib")

set(APP_LIBS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${ANDROID_ABI})
set(CONFIG_HEAD_DIR ${CMAKE_CURRENT_SOURCE_DIR}/config/${ANDROID_ABI})

include_directories(${CONFIG_HEAD_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/fftools)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

# libffmpeg.so
add_library(lib_ffmpeg SHARED IMPORTED)
set_target_properties(lib_ffmpeg PROPERTIES IMPORTED_LOCATION ${APP_LIBS_DIR}/libffmpeg.so)

# ffmpeg-cmd
set(FFMPEG_CMD_DIR ${CMAKE_CURRENT_SOURCE_DIR}/fftools)
set(FFMPEG_CMD_SRC
        ${FFMPEG_CMD_DIR}/cmdutils.c
        ${FFMPEG_CMD_DIR}/ffmpeg.c
        ${FFMPEG_CMD_DIR}/ffmpeg_filter.c
        ${FFMPEG_CMD_DIR}/ffmpeg_hw.c
        ${FFMPEG_CMD_DIR}/ffmpeg_opt.c)
add_library(ffmpeg_cmd SHARED ${FFMPEG_CMD_SRC})



find_library(log-lib log)


target_link_libraries(ffmpeg_cmd
        android
        lib_ffmpeg
        ${log-lib})