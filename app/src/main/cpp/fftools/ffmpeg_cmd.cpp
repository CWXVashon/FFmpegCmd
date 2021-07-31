
#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>
#include "ffmpeg.h"
//
// Created by  wangchao on 2021/7/30.
//


extern "C"
JNIEXPORT jint JNICALL
Java_com_example_ffmpegcmd_ffmpeg_FFmpegCmd_run(JNIEnv *env, jobject thiz, jobjectArray array) {
    int argc = env->GetArrayLength(array);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        auto js = (jstring) env->GetObjectArrayElement(array, i);
        argv[i] = (char *) env->GetStringUTFChars(js, 0);
    }
    return run(argc, argv);
}
}