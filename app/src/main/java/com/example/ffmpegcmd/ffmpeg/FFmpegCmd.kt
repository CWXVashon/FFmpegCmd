package com.example.ffmpegcmd.ffmpeg

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


}