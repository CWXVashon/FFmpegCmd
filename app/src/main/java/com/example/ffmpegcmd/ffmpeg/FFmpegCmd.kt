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
    }


    fun execute(array: Array<String?>?,  handleListener: OnHandleListener) {
        ThreadPoolExecutor.executeSingleThreadPool(Runnable {
            handleListener.onStart()
            // call jni method
            runFFmpeg(array)
            handleListener.onFinish()
            cancelTaskJNI(0);
        })

    }

    // 执行命令
    private external fun runFFmpeg(array: Array<String?>?): Int
    // 取消进行中的任务，0：不取消，1：取消
    private external fun cancelTaskJNI(cancel: Int)
}