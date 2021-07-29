package com.example.ffmpegcmd.ffmpeg

import android.os.Handler


class FFmpegHandler(handler: Handler) {

    companion object{
        const val MSG_START: Int = 10
        const val MSG_INFO: Int = 10
        const val MSG_PROGRESS: Int = 10
        const val MSG_FINISH: Int = 10
    }

    private var mHandler: Handler? = null

    init {
        mHandler = handler
    }


    /**
     * 执行ffmpeg命令
     * @param 命令字符串
     * */
    fun executeFFmpegCmd(array: Array<String?>?){
        FFmpegCmd.instance?.execute(array, object :OnHandleListener{
            override fun onStart() {
                mHandler?.obtainMessage(MSG_START)?.sendToTarget()
            }

            override fun onMessage() {
                mHandler?.obtainMessage(MSG_INFO)?.sendToTarget()
            }

            override fun onProgress() {
                mHandler?.obtainMessage(MSG_PROGRESS)?.sendToTarget()
            }

            override fun onFinish() {
                mHandler?.obtainMessage(MSG_FINISH)?.sendToTarget()
            }

        })
    }
}