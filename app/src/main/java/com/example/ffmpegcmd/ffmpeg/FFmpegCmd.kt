package com.example.ffmpegcmd.ffmpeg

import com.example.ffmpegcmd.util.ThreadPoolExecutor

internal class FFmpegCmd private constructor(){

    companion object{
        var instance: FFmpegCmd? = null
            get() {
                if (field == null) {
                    field = FFmpegCmd()
                }
                return field
            }
        init {
            System.loadLibrary("ffmpeg-cmd")
        }

        /**
         * JNI 层自动回调这里，state：0-初始化，1-运行中，2-运行结束，3-运行错误
         */
        fun onProgressCallback(position: Int, duration: Int, state: Int) {
            if (duration in 1 until position) {
                return;
            }
            if (position > 0 && duration > 0) {
                val progress = position * 100 / duration;
                if (progress < 100 || state == 2 || state == 3) {
                    // TODO: 2021/8/1 进度回调
                    // progressListener.onProgress(progress, duration);
                }
            } else {
                // TODO: 2021/8/1 进度回调
                // progressListener.onProgress(position, duration);
            }
        }
    }


    fun execute(array: Array<String?>?,  handleListener: OnHandleListener) {
        ThreadPoolExecutor.executeSingleThreadPool(Runnable {
            handleListener.onStart()
            // call jni method
            cmdRun(array)
            handleListener.onFinish()
        })

    }

    // 执行命令
    private external fun cmdRun(array: Array<String?>?): Int
    // 取消进行中的任务，0：不取消，1：取消
    private external fun cancelTaskJNI(cancel: Int)
}