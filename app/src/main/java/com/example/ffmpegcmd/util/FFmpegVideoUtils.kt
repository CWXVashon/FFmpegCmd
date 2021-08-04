package com.example.ffmpegcmd.util

object FFmpegVideoUtils {
    /**
     * 视频区域裁剪
     * @param srcFile    输入视频
     * @param width      裁剪区域宽
     * @param height     裁剪区域高
     * @param x          起点位置x
     * @param y          起点位置y
     * @param targetFile 输出文件
     */
    @JvmStatic
    fun cutVideoArea(//宽，高，x,y
        srcFile: String, width: Float, height: Float, x: Float, y: Float,
        targetFile: String
    ): Array<String> {
        val cmd = "ffmpeg -i %s -vf crop=%f:%f:%f:%f %s"
        val command = String.format(cmd, srcFile, width, height, x, y, targetFile)
        return command.split(" ").toTypedArray()//以空格分割为字符串数组
    }
}