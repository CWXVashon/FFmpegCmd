package com.example.ffmpegcmd.ui

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ffmpegcmd.util.CVPlayer2
import com.xing.hhplayer.common.base.player.HHMediaPlayer
import com.xing.hhplayer.common.bean.TvList.TvView
import com.xing.hhplayer.common.bean.Type.PlayerState
import com.xing.hhplayer.common.util.V_time
import x.com.fliepick.bean.FileBean

abstract class VideoPlayerActivity : AppCompatActivity() {
    @JvmField
    var player: CVPlayer2? = null

    @JvmField
    var videoBean: FileBean? = null

    @JvmField
    var tip: String? = null

    @JvmField
    var path: String? = null

    @JvmField
    var panelStartTime: TextView? = null

    @JvmField
    var panelEndTime: TextView? = null

    @JvmField
    var informationText: TextView? = null

    @JvmField
    var panelSeek: SeekBar? = null

    @JvmField
    var playBtn: ImageView? = null

    fun initPlayerUI() {
        playBtn?.setOnClickListener {
            if (player == null) return@setOnClickListener
            if (player!!.isPlaying) {
                pausePlayer()
            } else {
                startPlayer()
            }
        }
        panelSeek?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (player != null) player!!.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    //先用个播放器做预览吧，后面再换
    fun initPlayer(playerLayout: FrameLayout?) {
        if (player == null) {
            player = CVPlayer2(this, HHMediaPlayer(), TvView.NEWSURFACE, playerLayout)
            player!!.setPlayWhenPrepared(true)
            player!!.setOnProgressListener { time: Long ->
                runOnUiThread {
                    panelSeek?.progress = time.toString().toInt()
                    panelStartTime?.text = V_time.formatTime(time)
                }
            }
            player!!.setOnPreparedListener { o: Any? ->
                panelEndTime?.text = V_time.formatTime(player!!.mediaDurationMS)
                panelSeek?.max = player!!.mediaDurationMS.toInt()
                initChooseUIWhenPrepare()
            }
        }
    }

    open fun initChooseUI() {

    }

    open fun initChooseUIWhenPrepare() {

    }

    fun startPlayer() {
        if (player!!.playerState == PlayerState.PAUSE) {
            player!!.start()
        } else {
            player!!.reset()
            if (videoBean != null) {
                player!!.prepare(videoBean?.filePathUri)
            } else {
                player!!.prepare(path)
            }
            informationText?.text = tip
            initChooseUI()
        }
    }

    fun pausePlayer() {
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
}