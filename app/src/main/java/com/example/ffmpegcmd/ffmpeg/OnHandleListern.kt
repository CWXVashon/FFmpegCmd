package com.example.ffmpegcmd.ffmpeg

interface OnHandleListener {

    fun onStart()
    fun onMessage()
    fun onProgress()
    fun onFinish()
}