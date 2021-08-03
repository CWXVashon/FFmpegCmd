//
// Created by Administrator on 2021/8/3.
//

#ifndef FFMPEGCMD_FFPROBE_H
#define FFMPEGCMD_FFPROBE_H

#ifdef __cplusplus
extern "C" {
#endif

char *run_ffprobe(int argc, char **argv);

/**
 * 打印输出为json字符串
 * 在调用 printf_json 的地方源码为 printf
 */
void printf_json(char *fmt, ...);

/**
 * 释放资源
 */
void release_source();

#ifdef __cplusplus
}
#endif

#endif //FFMPEGCMD_FFPROBE_H
