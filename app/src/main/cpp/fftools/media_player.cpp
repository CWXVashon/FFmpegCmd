//
// Created by  wangchao on 2021/8/6.
//

#ifdef __cplusplus
extern "C" {
#endif
#include "libavcodec/avcodec.h"
#include "jni.h"
#include "android_log.h"
#include "ffmpeg.h"

JNIEXPORT void JNICALL
Java_com_example_ffmpegcmd_mediaplayer_MediaPlayer_start(JNIEnv *env, jobject thiz) {
}


JNIEXPORT void JNICALL
Java_com_example_ffmpegcmd_mediaplayer_MediaPlayer_stop(JNIEnv *env, jobject thiz) {
}


JNIEXPORT void JNICALL
Java_com_example_ffmpegcmd_mediaplayer_MediaPlayer_seekTo(JNIEnv *env, jobject thiz) {
}


JNIEXPORT void JNICALL
Java_com_example_ffmpegcmd_mediaplayer_MediaPlayer_release(JNIEnv *env, jobject thiz) {
}


JNIEXPORT void JNICALL
Java_com_example_ffmpegcmd_mediaplayer_MediaPlayer_pause(JNIEnv *env, jobject thiz) {
}

JNIEXPORT void JNICALL
Java_com_example_ffmpegcmd_mediaplayer_MediaPlayer_setDataSource(JNIEnv *env, jobject thiz,
                                                                 jstring path) {
}

#ifdef __cplusplus
}
#endif