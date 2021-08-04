package com.example.ffmpegcmd.util

object FFmpegInfoUtils {
    /**
     * 获取ffmpeg支持的格式
     *
     * @return 转码后的文件
     */
    @JvmStatic
    fun ffmpegFormat(): Array<String?> {
        val command = "ffmpeg -formats"
        //以空格分割为字符串数组
        return command.split(" ").toTypedArray()
    }

}