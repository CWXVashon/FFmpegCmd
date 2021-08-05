package com.example.ffmpegcmd.util

import android.annotation.SuppressLint

object FFmpegAudioUtils {
    /**
     * 使用ffmpeg命令行进行音频剪切
     *
     * @param srcFile    源文件
     * @param startTime  剪切的开始时间(单位为毫秒)
     * @param duration   剪切时长(单位为毫秒)
     * @param targetFile 目标文件
     * @return 剪切后的文件
     */
    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun cutAudio(
        srcFile: String?, startTime: Long, duration: Long, targetFile: String?
    ): Array<String?> {
        var command = "ffmpeg -y -i %s -vn -acodec copy -ss %d -t %d %s"
        command = String.format(command, srcFile, startTime / 1000, duration / 1000, targetFile)
        return command.split(" ").toTypedArray() //以空格分割为字符串数组

    }

    /**
     * 使用ffmpeg命令行进行音频剪切
     * @param srcFile    源文件
     * @param startTime  剪切的开始时间(单位为秒),传00:00:10
     * @param endTime    剪切的结束时间(单位为秒),传00:00:30
     * @param targetFile 目标文件
     * @return 剪切后的文件
     */
    @JvmStatic
    fun cutAudio(
        srcFile: String?, startTime: String?, endTime: String?,
        targetFile: String?
    ): Array<String?> {
        val cmd = "ffmpeg -i %s -vn -acodec copy -ss %s -t %s %s -y"
        val command = String.format(cmd, srcFile, startTime, endTime, targetFile)
        return command.split(" ").toTypedArray()//以空格分割为字符串数组
    }
}