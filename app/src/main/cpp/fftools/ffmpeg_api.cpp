
#ifdef __cplusplus
extern "C" {
#endif
#include "libavcodec/avcodec.h"
#include "jni.h"
#include "android_log.h"
#include "ffmpeg.h"
//
// Created by  wangchao on 2021/7/30.
//

AVFormatContext *ic;
int video_stream_idx;

JNIEXPORT jint JNICALL
Java_com_example_ffmpegcmd_ffmpeg_FFmpegApi_open(JNIEnv *env, jobject thiz, jstring url) {
    const char *videoUrl = NULL;
    videoUrl = env->GetStringUTFChars(url, NULL);
    LOGE("FFmpegApi_open_video url : %s", videoUrl);
    ic = avformat_alloc_context();
    if (avformat_open_input(&ic, videoUrl, NULL, NULL) < 0) {
        LOGE("can not source : %s", videoUrl);
        return -1;
    }
    if (avformat_find_stream_info(ic, NULL) < 0) {
        LOGE("could not find stream information");
        return -1;
    }
    video_stream_idx = av_find_best_stream(ic, AVMEDIA_TYPE_VIDEO, -1, -1, NULL, 0);
    return 0;

}

JNIEXPORT jint JNICALL
Java_com_example_ffmpegcmd_ffmpeg_FFmpegApi_getWidth(JNIEnv *env, jobject thiz) {
    AVStream *stream = ic->streams[video_stream_idx]; //拿到打开的那一路流的信息
    AVCodecParameters *avCodecParameters = stream->codecpar;
    return avCodecParameters->width;
}

JNIEXPORT jint JNICALL
Java_com_example_ffmpegcmd_ffmpeg_FFmpegApi_getHeight(JNIEnv *env, jobject thiz) {
    AVStream *stream = ic->streams[video_stream_idx]; //拿到打开的那一路流的信息
    AVCodecParameters *avCodecParameters = stream->codecpar;
    return avCodecParameters->height;
}

JNIEXPORT jint JNICALL
Java_com_example_ffmpegcmd_ffmpeg_FFmpegApi_getDuration(JNIEnv *env, jobject thiz) {
    AVStream *stream = ic->streams[video_stream_idx]; //拿到打开的那一路流的信息
    int duration = stream->duration;
    return duration;
}

#ifdef __cplusplus
}
#endif
