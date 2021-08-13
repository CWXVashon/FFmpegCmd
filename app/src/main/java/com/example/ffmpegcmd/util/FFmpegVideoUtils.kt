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
     * 有问题，能生成视频，但视频很小，应该是没写入成功
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

    /**
     * 视频反序倒播
     *
     * @param inputFile  输入文件
     * @param targetFile 反序文件
     * @return 视频反序的命令行
     */
    @JvmStatic
    fun reverseVideo(inputFile: String?, targetFile: String?): Array<String?> {
        var command = "ffmpeg -y -i %s -filter_complex [0:v]reverse[v] -map [v] %s" //单纯视频反序
        command = String.format(command, inputFile, targetFile)
        return command.split(" ").toTypedArray()
    }

    /**
     * 视频反序倒播
     *
     * @param inputFile  输入文件
     * @param targetFile 反序文件
     * @return 视频反序的命令行
     */
    @JvmStatic
    fun reverseAudioVideo(inputFile: String?, targetFile: String?): Array<String?> {
        var command =
            "ffmpeg -y -i %s -filter_complex [0:v]reverse[v];[0:a]reverse[a] -map [v] -map [a] %s"
        command = String.format(command, inputFile, targetFile)
        return command.split(" ").toTypedArray()
    }

    /**
     * 视频叠加成画中画
     *
     * @param inputFile1 输入文件
     * @param inputFile2 输入文件
     * @param targetFile 输出文件
     * @param x          小视频起点x坐标
     * @param y          小视频起点y坐标
     * @return 视频画中画的命令行
     */
    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun picInPicVideo(
        inputFile1: String?, inputFile2: String?, x: Int, y: Int,
        targetFile: String?
    ): Array<String?> {
        var command = "ffmpeg -y -i %s -i %s -filter_complex overlay=%d:%d %s"
        command = String.format(command, inputFile1, inputFile2, x, y, targetFile)
        return command.split(" ").toTypedArray()
    }
}