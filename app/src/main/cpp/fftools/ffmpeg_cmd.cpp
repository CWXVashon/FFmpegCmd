
#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>
#include "ffmpeg.h"
#include "android_log.h"
//
// Created by  wangchao on 2021/7/30.
//

// 用于进度回调的结构体
typedef struct tick_context {
    jclass ffmpegCmdClz;
    jmethodID progressMID;
    JNIEnv *env;
} TickContext;
TickContext globalCtx;

//Java_com_example_ffmpegcmd_ffmpeg_FFmpegCmd_run(JNIEnv *env, jobject thiz, jobjectArray array) {
JNIEXPORT jint JNICALL cmdRun(JNIEnv *env, jobject thiz, jobjectArray array) {
    int argc = env->GetArrayLength(array);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        auto js = (jstring) env->GetObjectArrayElement(array, i);
        argv[i] = (char *) env->GetStringUTFChars(js, 0);
    }
    return run(argc, argv);
}

JNIEXPORT void JNICALL cancelTaskJNI(JNIEnv *env, jobject thiz, jint cancel) {
    cancelTask(cancel);
}

// native -> java 进度回调
void progressCallback(int position, int duration, int state) {
    if (globalCtx.env) {
        globalCtx.env->CallStaticVoidMethod(globalCtx.ffmpegCmdClz, globalCtx.progressMID, position, duration, state);
    }
}

// ------------------------------------------------------------------ 华丽的分割线 ------------------------------------------------------------------
// 弄成下面这样的自己手动注册本地函数的好处是：
// 1.方便在任何地方写存放本地方法的类，因为只需通过定义一个宏即可，无需绑定函数名
// 2.方便库的迁移，如果库用在不同的包中，只需修改对应的宏即可，无需改变函数名
// 3.有时候通过包名形式的函数名映射会发生莫名奇妙的映射失败情况

#define NATIVE_FFMPEG_CMD_CLASS_NAME "com/example/ffmpegcmd/ffmpeg/FFmpegCmd"

static JNINativeMethod ffmpegCmdMethods[] = {
        // 函数名，函数签名，函数指针 java.lang.reflect
        {"cmdRun", "(Ljava/lang/reflect/Array;)I", (void *) cmdRun},
        {"cancelTaskJNI", "(I)V", (void *) cancelTaskJNI}
};

jint RegisterNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *methods,
                           jint methodNum) {
    jclass clazz = env->FindClass(className);
    if (clazz == NULL)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, methodNum) != JNI_OK)
        return JNI_ERR;
    globalCtx.ffmpegCmdClz = static_cast<jclass>(env->NewGlobalRef(clazz));
    return JNI_OK;
}

void UnregisterNativeMethods(JNIEnv *env, const char *className) {
    jclass clazz = env->FindClass(className);
    if (clazz == NULL)
        return;
    env->UnregisterNatives(clazz);
}

// 将需要回调的函数查询并保存下来
void queryRuntimeInfo(JNIEnv *env) {
    jmethodID progressFunc = env->GetStaticMethodID(globalCtx.ffmpegCmdClz, "onProgressCallback", "(III)V");
    if (!progressFunc) {
        XLOGE("查询回调进度函数失败，错误代码行数：%d", __LINE__);
        return;
    }
    globalCtx.progressMID = progressFunc;
}

// 加载动态库时，自动运行
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    memset(&globalCtx, 0, sizeof(globalCtx));
    JNIEnv *env;
    jint jniRet = vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (jniRet != JNI_OK) {
        return jniRet;
    }
    // 这里可能有问题，将 env 保存下来，如果后续出问题，那么这里就不保存了，在每次 native 函数调用的时候才保存
    globalCtx.env = env;

    jniRet = RegisterNativeMethods(
            env,
            NATIVE_FFMPEG_CMD_CLASS_NAME,
            ffmpegCmdMethods,
            sizeof(ffmpegCmdMethods) / sizeof(ffmpegCmdMethods[0]));

    if (jniRet != JNI_OK) {
        return jniRet;
    }

    queryRuntimeInfo(env);

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

    env->DeleteGlobalRef(globalCtx.ffmpegCmdClz);
    globalCtx.ffmpegCmdClz = NULL;
    globalCtx.progressMID = NULL;
    globalCtx.env = NULL;
}

#ifdef __cplusplus
}
#endif