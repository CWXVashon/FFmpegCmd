
#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>
#include "ffmpeg.h"
//
// Created by  wangchao on 2021/7/30.
//


JNIEXPORT jint JNICALL
//Java_com_example_ffmpegcmd_ffmpeg_FFmpegCmd_run(JNIEnv *env, jobject thiz, jobjectArray array) {
cmdRun(JNIEnv *env, jobject thiz, jobjectArray array) {
    int argc = env->GetArrayLength(array);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        auto js = (jstring) env->GetObjectArrayElement(array, i);
        argv[i] = (char *) env->GetStringUTFChars(js, 0);
    }
    return run(argc, argv);
}




// ------------------------------------------------------------------ 华丽的分割线 ------------------------------------------------------------------
// 弄成下面这样的自己手动注册本地函数的好处是：
// 1.方便在任何地方写存放本地方法的类，因为只需通过定义一个宏即可，无需绑定函数名
// 2.方便库的迁移，如果库用在不同的包中，只需修改对应的宏即可，无需改变函数名
// 3.有时候通过包名形式的函数名映射会发生莫名奇妙的映射失败情况

#define NATIVE_FFMPEG_CMD_CLASS_NAME "com/example/ffmpegcmd/ffmpeg/FFmpegCmd"

static JNINativeMethod ffmpegCmdMethods[] = {
        // 函数名，函数签名，函数指针
        {"cmdRun", "([Lkotlin/Array;)I", (void *) cmdRun}
};

jint RegisterNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *methods,
                           jint methodNum) {
    jclass clazz = env->FindClass(className);
    if (clazz == NULL)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, methodNum) != JNI_OK)
        return JNI_ERR;
    return JNI_OK;
}

void UnregisterNativeMethods(JNIEnv *env, const char *className) {
    jclass clazz = env->FindClass(className);
    if (clazz == NULL)
        return;
    env->UnregisterNatives(clazz);
}

// 加载动态库时，自动运行
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    jint jniRet = vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (jniRet != JNI_OK) {
        return jniRet;
    }

    jniRet = RegisterNativeMethods(
            env,
            NATIVE_FFMPEG_CMD_CLASS_NAME,
            ffmpegCmdMethods,
            sizeof(ffmpegCmdMethods) / sizeof(ffmpegCmdMethods[0]));

    if (jniRet != JNI_OK) {
        return jniRet;
    }

    // 如果最后成功，必须返回一个JNI的版本，否则JVM默认加载库失败
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    jint jniRet = vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (jniRet != JNI_OK) {
        return;
    }

    UnregisterNativeMethods(env, NATIVE_FFMPEG_CMD_CLASS_NAME);
}

#ifdef __cplusplus
}
#endif