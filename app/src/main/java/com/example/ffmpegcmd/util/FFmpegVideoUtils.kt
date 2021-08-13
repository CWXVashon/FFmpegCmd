package com.example.ffmpegcmd.util

import android.annotation.SuppressLint

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

    /**
     * 使用ffmpeg命令行进行视频剪切(不包含)
     *
     * @param srcFile    源文件
     * @param startTime  剪切的开始时间(单位为毫秒)
     * @param duration   剪切时长(单位为毫秒)
     * @param targetFile 目标文件
     * @return 剪切后的文件
     */
    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun cutVideoDuration(
        srcFile: String, startTime: Long, duration: Long, targetFile: String
    ): Array<String> {
        var command = "ffmpeg -y -i %s -ss %d -t %d -c copy %s"
        command = String.format(command, srcFile, startTime / 1000, duration / 1000, targetFile)
        return command.split(" ").toTypedArray() //以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行视频剪切(包含关键帧)
     *
     * @param srcFile    源文件
     * @param startTime  剪切的开始时间(单位为毫秒)
     * @param duration   剪切时长(单位为毫秒)
     * @param targetFile 目标文件
     * @return 剪切后的文件
     */
    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun cutVideoDurationWithFrame(
        srcFile: String, startTime: Long, duration: Long, targetFile: String
    ): Array<String> {
        var command = "ffmpeg -y -ss %d -t %d -accurate_seek -i %s -codec copy " +
                "-avoid_negative_ts 1 %s"
        command = String.format(command, startTime / 1000, duration / 1000, srcFile, targetFile)
        return command.split(" ").toTypedArray()//以空格分割为字符串数组

    }

}