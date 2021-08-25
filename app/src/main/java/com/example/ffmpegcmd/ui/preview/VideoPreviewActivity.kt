package com.example.ffmpegcmd.ui.preview

import android.content.Context

import android.os.Bundle
import android.content.Intent
import android.view.View
import com.example.ffmpegcmd.databinding.ActivityPreviewVideoBinding
import com.example.ffmpegcmd.ui.VideoPlayerActivity

/**
 * 视频合成预览页面
 */
class VideoPreviewActivity : VideoPlayerActivity() {
    var binding: ActivityPreviewVideoBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewVideoBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        panelStartTime = binding!!.seekLayout.panelStartTime
        panelEndTime = binding!!.seekLayout.panelEndTime
        panelSeek = binding!!.seekLayout.panelSeek
        informationText = binding!!.informationText
        playBtn = binding!!.playBtn
        initPlayerUI()
        path = intent.getStringExtra("path")
        binding!!.informationText.text = path
        initPlayer(binding!!.playerLayout)
        startPlayer()
        binding!!.backBtn.setOnClickListener { v: View? -> finish() }
    }

    companion object {
        @JvmStatic
        fun start(context: Context, path: String?) {
            val intent = Intent(context, VideoPreviewActivity::class.java)
            intent.putExtra("path", path)
            context.startActivity(intent)
        }
    }
}