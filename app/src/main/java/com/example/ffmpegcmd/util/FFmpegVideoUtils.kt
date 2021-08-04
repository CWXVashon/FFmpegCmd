package com.example.ffmpegcmd.util

object FFmpegVideoUtils {


    @JvmStatic
    fun cutVideoArea(//宽，高，x,y
        srcFile: String, width: Float, height: Float, x: Float, y: Float,
        targetFile: String
    ): Array<String> {
        val cmd = "ffmpeg -i %s -strict -2 -vf crop=%f:%f:%f:%f %s"
        val command = String.format(cmd, srcFile, width, height, x, y, targetFile)
        return command.split(" ").toTypedArray()//以空格分割为字符串数组
    }
}