package com.example.ffmpegcmd.ui

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.play.common.KVPlayer
import com.play.common.base.listener.OnPlaySimpleListener
import com.play.common.base.listener.OnPlayerListener
import com.play.common.base.player.KMediaPlayer
import com.play.common.bean.tvlist.TvView
import com.play.common.bean.type.PlayerState
import com.play.common.util.V_time
import x.com.fliepick.bean.FileBean
import x.com.util.U_permissions
import x.com.util.U_permissions.RequestPermissionCallBack
import x.com.util.U_time

abstract class VideoPlayerActivity : AppCompatActivity() {
    @JvmField
    var player: KVPlayer? = null

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
            if (player!!.isPlaying()) {
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

        U_permissions.applyStoragePermission11(this, object : RequestPermissionCallBack {
            override fun requestPermissionSuccess() {
            }

            override fun requestPermissionFail(failPermission: List<String>) {}
        })
    }

    //先用个播放器做预览吧，后面再换
    fun initPlayer(playerLayout: FrameLayout?) {
        if (player == null) {
            player = KVPlayer(this, KMediaPlayer(), TvView.NEWSURFACE, playerLayout)
            player?.setPlayWhenPrepared(true)
            player?.setOnProgressListener(object : OnPlayerListener<Long> {
                override fun onListen(time: Long) {
                    runOnUiThread {
                        panelSeek?.progress = time.toString().toInt()
                        panelStartTime?.text = U_time.formatTime(time)
                    }
                }

            })

            player?.setOnPreparedListener(object : OnPlaySimpleListener {
                override fun onListen() {
                    panelEndTime?.text = V_time.formatTime(player!!.getMediaDurationMS())
                    panelSeek?.max = player!!.getMediaDurationMS().toInt()
                    initChooseUIWhenPrepare()
                }

            })
        }
    }

    open fun initChooseUI() {

    }

    open fun initChooseUIWhenPrepare() {

    }

    fun startPlayer() {
        if (player?.getPlayState() == PlayerState.PAUSE) {
            player?.start()
        } else {
            player?.reset()
            if (videoBean != null) {
                player?.prepare(videoBean?.filePathUri!!)
            } else {
                player?.prepare(path!!)
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