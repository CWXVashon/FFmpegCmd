package com.example.ffmpegcmd.ui

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.example.ffmpegcmd.BaseActivity
import com.example.ffmpegcmd.R
import com.example.ffmpegcmd.mediaplayer.MediaPlayer
import com.example.ffmpegcmd.util.ThreadPoolExecutor

class MediaPlayerActivity: BaseActivity(), SurfaceHolder.Callback {

    var mediaPlayer: MediaPlayer? = null

    var surfaceCreated = false

    var surfaceHolder: SurfaceHolder? = null

    override val layoutId: Int
        get() = R.layout.layout_media_player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initPlayer()

    }

    private fun initPlayer() {
        mediaPlayer = MediaPlayer()
    }

    private fun initView() {
        val surfaceView = getView<SurfaceView>(R.id.surface_container)
        surfaceHolder = surfaceView.holder
        surfaceHolder!!.addCallback(this)
        initViewsWithClick(R.id.media_file_select)
    }

    override fun onViewClick(view: View) {
        selectFile()
    }

    override fun onSelectedFile(filePath: String) {
        if (!FileUtils.isFileExists(filePath)){
            return
        }
        if (surfaceCreated){
            ThreadPoolExecutor.executeSingleThreadPool(Runnable {
                startPlay(filePath)
            })
        }
    }

    private fun startPlay(path: String) {
        mediaPlayer?.setDataSource(path, surfaceHolder?.surface)
        mediaPlayer?.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceCreated = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mediaPlayer?.release()
    }
}