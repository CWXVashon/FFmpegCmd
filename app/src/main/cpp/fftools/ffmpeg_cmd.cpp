
#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>
#include "ffmpeg.h"
#include "ffprobe.h"
#include "android_log.h"
//
// Created by  wangchao on 2021/7/30.
//

// 用于信息回调的结构体
typedef struct tick_context {
    jclass ffmpegCmdClz;
    jobject ffmpegCmdObj;
    jmethodID progressMID;
    jmethodID messageMID;
    JNIEnv *env;
} TickContext;
TickContext globalCtx;

void log_callback(void *, int, const char *, va_list);

JNIEXPORT jint JNICALL
runFFmpeg(JNIEnv *env, jobject thiz, jobjectArray array) {
    globalCtx.env = env;
    int argc = env->GetArrayLength(array);
    char *argv[argc];
    jstring jsa[argc];
    for (int i = 0; i < argc; i++) {
        auto js = (jstring) env->GetObjectArrayElement(array, i);
        jsa[i] = js;
        argv[i] = (char *) env->GetStringUTFChars(js, 0);
    }
    // 运行 ffmpeg 命令行
    int resultCode = run_ffmpeg(argc, argv);

    // 释放资源，防止内存泄露
    for (int j = 0; j < argc; ++j) {
        env->ReleaseStringUTFChars(jsa[j], argv[j]);
        env->DeleteLocalRef(jsa[j]);
    }

    return resultCode;
}

JNIEXPORT jstring JNICALL
runFFprobe(JNIEnv *env, jobject thiz, jobjectArray array) {
    globalCtx.env = env;
    int argc = env->GetArrayLength(array);
    char *argv[argc];
    jstring jsa[argc];
    for (int i = 0; i < argc; i++) {
        auto js = (jstring) env->GetObjectArrayElement(array, i);
        jsa[i] = js;
        argv[i] = (char *) env->GetStringUTFChars(js, 0);
    }
    // 运行 ffprobe 命令行
    char *jsonStr = run_ffprobe(argc, argv);

    // 释放资源，防止内存泄露
    for (int j = 0; j < argc; ++j) {
        env->ReleaseStringUTFChars(jsa[j], argv[j]);
        env->DeleteLocalRef(jsa[j]);
    }

    return env->NewStringUTF(jsonStr);
}

JNIEXPORT void JNICALL
cancelTaskJNI(JNIEnv *env, jobject thiz, jint cancel) {
    cancel_task(cancel);
}

// native -> java 进度回调
void progress_callback(int position, int duration, int state) {
    if (globalCtx.env && globalCtx.progressMID) {
        globalCtx.env->CallVoidMethod(globalCtx.ffmpegCmdObj, globalCtx.progressMID, position,
                                      duration, state);
    }
}

void msg_callback(const char *format, va_list args) {
    if (globalCtx.env && globalCtx.messageMID) {
        char *ff_msg = (char *) malloc(sizeof(char) * 1024);
        vsprintf(ff_msg, format, args);
        jstring jstr = globalCtx.env->NewStringUTF(ff_msg);
        globalCtx.env->CallVoidMethod(globalCtx.ffmpegCmdObj, globalCtx.messageMID, jstr);
        globalCtx.env->DeleteLocalRef(jstr);
        free(ff_msg);
    }
}

void log_callback(void *ptr, int level, const char *format, va_list args) {
    switch (level) {
        case AV_LOG_WARNING:
            XLOGD(format, args);
            break;
        case AV_LOG_INFO:
            XLOGD(format, args);
            if (format && strncmp("silence", format, 7) == 0) {
                msg_callback(format, args);
            }
            break;
        case AV_LOG_ERROR:
            XLOGE(format, args);
            msg_callback(format, args);
            break;
        default:
            break;
    }
}

void releaseSource() {
    release_source();
}

// ------------------------------------------------------------------ 华丽的分割线 ------------------------------------------------------------------
// 弄成下面这样的自己手动注册本地函数的好处是：
// 1.方便在任何地方写存放本地方法的类，因为只需通过定义一个宏即可，无需绑定函数名
// 2.方便库的迁移，如果库用在不同的包中，只需修改对应的宏即可，无需改变函数名
// 3.有时候通过包名形式的函数名映射会发生莫名奇妙的映射失败情况

//#define NATIVE_FFMPEG_CMD_CLASS_NAME "com/example/ffmpegcmd/ffmpeg/FFmpegCmd"
#define NATIVE_FFMPEG_CMD_CLASS_NAME "com/example/ffmpegcmd/ffmpegjava/FFmpegCmd"

static JNINativeMethod ffmpegCmdMethods[] = {
        // 函数名，函数签名，函数指针
        {"runFFmpeg",        "([Ljava/lang/String;)I", (void *) runFFmpeg},
        {"runFFprobe",        "([Ljava/lang/String;)Ljava/lang/String;", (void *) runFFprobe},
        {"cancelTaskJNI", "(I)V",                   (void *) cancelTaskJNI}
};

jint RegisterNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *methods,
                           jint methodNum) {
    jclass clazz = env->FindClass(className);
    if (clazz == NULL)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, methodNum) != JNI_OK)
        return JNI_ERR;
    // 保存需要回调的 Java 类的信息
    globalCtx.ffmpegCmdClz = static_cast<jclass>(env->NewGlobalRef(clazz));
    jmethodID ffmpegCmdCtor = env->GetMethodID(globalCtx.ffmpegCmdClz, "<init>", "()V");
    jobject ffmpegCmd = env->NewObject(globalCtx.ffmpegCmdClz, ffmpegCmdCtor);
    globalCtx.ffmpegCmdObj = env->NewGlobalRef(ffmpegCmd);
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
    jmethodID progressFunc = env->GetMethodID(globalCtx.ffmpegCmdClz, "onProgressCallback",
                                              "(III)V");
    if (!progressFunc) {
        XLOGE("查询回调进度函数失败，错误代码行数：%d", __LINE__);
        return;
    }
    globalCtx.progressMID = progressFunc;

    jmethodID messageFunc = env->GetMethodID(globalCtx.ffmpegCmdClz, "onMsgCallback",
                                             "(Ljava/lang/String;)V");
    if (!messageFunc) {
        XLOGE("查询回调信息函数失败，错误代码行数：%d", __LINE__);
        return;
    }
    globalCtx.messageMID = messageFunc;
}

// 加载动态库时，自动运行
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    memset(&globalCtx, 0, sizeof(globalCtx));
    JNIEnv *env;
    jint jniRet = vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (jniRet != JNI_OK)
        return jniRet;

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
    env->DeleteGlobalRef(globalCtx.ffmpegCmdObj);
    globalCtx.ffmpegCmdClz = NULL;
    globalCtx.ffmpegCmdObj = NULL;
    globalCtx.progressMID = NULL;
    globalCtx.messageMID = NULL;
    globalCtx.env = NULL;
    releaseSource();
}

#ifdef __cplusplus
}
#endif