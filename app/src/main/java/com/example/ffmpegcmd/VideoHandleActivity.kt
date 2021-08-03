package com.example.ffmpegcmd

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ffmpegcmd.ffmpeg.FFmpegHandler
import com.example.ffmpegcmd.util.FFmpegUtils

class VideoHandleActivity: BaseActivity() {

    override val layoutId: Int
        get() = R.layout.layout_video_handle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //执行操作
//        handleVideo()
    }

    override fun onViewClick(view: View) {
        selectFile()
    }


    /**
     * 获取手机媒体数据
     * */
    override fun onSelectedFile(filePath: String) {
        handleVideo(filePath)
    }

    fun initView(){
        initViewsWithClick(R.id.add_water_mask)
    }

    /**
     *  所有video操作的入口
     * */
    private fun handleVideo(path: String) {
//        val array = FFmpegUtils.addWaterMark()
//        fFmpegHandler.executeFFmpegCmd(array)
    }

    /**
     * handleMessage中处理进行video处理的callback
     * */
    @SuppressLint("HandlerLeak")
    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }
    val fFmpegHandler = FFmpegHandler(handler)

}