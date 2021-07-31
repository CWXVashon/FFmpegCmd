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
            cmdRun(array)
            handleListener.onFinish()
        })

    }

    private external fun cmdRun(array: Array<String?>?): Int

}