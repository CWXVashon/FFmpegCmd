package com.example.ffmpegcmd.util

import android.annotation.SuppressLint
import java.util.*

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
    fun reverseVideo(inputFile: String, targetFile: String): Array<String> {
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
    fun reverseAudioVideo(inputFile: String, targetFile: String): Array<String> {
        var command =
            "ffmpeg -y -i %s -filter_complex [0:v]reverse[v];[0:a]reverse[a] -map [v] -map [a] %s"
        command = String.format(command, inputFile, targetFile)
        return command.split(" ").toTypedArray()
    }

    /**
     * 视频叠加成画中画
     * 如何设置画中画的大小？
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
        inputFile1: String, inputFile2: String, x: Int, y: Int,
        targetFile: String
    ): Array<String> {
        var command = "ffmpeg -y -i %s -i %s -filter_complex overlay=%d:%d %s"
        command = String.format(command, inputFile1, inputFile2, x, y, targetFile)
        return command.split(" ").toTypedArray()
    }

    /**
     * 去水印
     *
     * @param inputPath 输入文件
     * @param targetFile 输出文件
     * @param x          水印起点x坐标
     * @param y          水印起点y坐标
     * @param width          水印宽
     * @param height          水印高
     * @return 去水印后的视频
     */
    @JvmStatic
    fun removeLogo(
        inputPath: String,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        targetFile: String
    ): Array<String> {
        var delogoCmd = "ffmpeg -i %s -filter_complex delogo=x=%d:y=%d:w=%d:h=%d %s"
        delogoCmd = String.format(
            Locale.getDefault(),
            delogoCmd,
            inputPath,
            x,
            y,
            width,
            height,
            targetFile
        )
        return delogoCmd.split(" ".toRegex()).toTypedArray()
    }

    /**
     * 添加视频封面
     * 不支持heic格式图片
     * @param inputPath inputFile
     * @param picturePath the path of thumbnail
     * @param outputPath targetFile
     * @return command of inserting picture
     */
    @JvmStatic
    fun insertPicIntoVideo(
        inputPath: String,
        picturePath: String,
        outputPath: String
    ): Array<String> {
        var insertPicCmd =
            "ffmpeg -i %s -i %s -map 0 -map 1 -c copy -c:v:1 png -disposition:v:1 attached_pic %s"
        insertPicCmd = String.format(insertPicCmd, inputPath, picturePath, outputPath)
        return insertPicCmd.split(" ".toRegex()).toTypedArray()
    }

    /**
     * 使用ffmpeg命令行进行视频转成Gif动图
     *
     * @param srcFile    源文件
     * @param startTime  开始时间
     * @param duration   截取时长
     * @param targetFile 目标文件
     * @return Gif文件
     */
    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun video2Gif(
        srcFile: String,
        startTime: Int,
        duration: Int,
        targetFile: String
    ): Array<String> {
        //String screenShotCmd = "ffmpeg -i %s -vframes %d -s 320x240 -f gif %s";
        var command = "ffmpeg -y -i %s -ss %d -t %d -f gif %s"
        command = String.format(command, srcFile, startTime, duration, targetFile)
        return command.split(" ").toTypedArray() //以空格分割为字符串数组
    }

    /**
     * 视频旋转
     * @param srcFile 源文件
     * @param targetFile 输出文件
     * @param transpose
     * @return 视频旋转命令行
     */
    @JvmStatic
    fun videoRotation(srcFile: String, transpose: Int, targetFile: String): Array<String> {
//        var command = "ffmpeg -y -i %s -vf transpose=%d -b:v 600k %s"//有问题
        var command = "ffmpeg -y -i %s -metadata:s:v rotate=%d -codec copy %s"
        command = String.format(Locale.CHINA, command, srcFile, transpose, targetFile)
        return command.split(" ").toTypedArray()
    }
}