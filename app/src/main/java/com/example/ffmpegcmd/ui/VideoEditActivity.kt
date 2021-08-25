package com.example.ffmpegcmd.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.example.ffmpegcmd.R
import com.example.ffmpegcmd.bean.TestBean
import com.example.ffmpegcmd.databinding.ActivityEditBinding
import com.example.ffmpegcmd.databinding.IncludeCutBinding
import com.example.ffmpegcmd.ffmpegjava.FFmpegCmd
import com.example.ffmpegcmd.ffmpegjava.OnHandleListener
import com.example.ffmpegcmd.ui.preview.ImgPreviewActivity
import com.example.ffmpegcmd.ui.preview.VideoPreviewActivity
import com.example.ffmpegcmd.ui.widget.ChooseAreaView
import com.example.ffmpegcmd.ui.widget.RangeSeekBarView
import com.example.ffmpegcmd.ui.widget.RangeSeekBarView.OnRangeSeekBarChangeListener
import com.example.ffmpegcmd.util.FFmpegVideoUtils
import x.com.base.toast.U_Toast
import x.com.dialog.CProgressDialog
import x.com.fliepick.bean.FileBean
import x.com.fliepick.media.CMediaPickDialog
import x.com.log.ViseLog
import x.com.media.U_media
import x.com.rxHttp.task.TaskDelayManager
import x.com.util.U_file
import x.com.util.U_time

class VideoEditActivity : VideoPlayerActivity() {
    private var binding: ActivityEditBinding? = null
    var cutBinding: IncludeCutBinding? = null
    private var areaView: ChooseAreaView? = null
    private var progressDialog: CProgressDialog? = null
    private var leftTimeMs: Long = 0
    private var rightTimeMs: Long = 0
    private var imgBean: FileBean? = null
    private val outputMP4 = U_file.DOWNLOADS + "/" + TestBean.outputMp4Name
    private val outputMP3 = U_file.DOWNLOADS + "/" + TestBean.outputMp3Name
    private val outputGIF = U_file.DOWNLOADS + "/" + TestBean.outputGifName
    private var inputFile: String? = ""
    private var isRunning = false
    private var type = 1//1视频2音频3图片
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        panelStartTime = binding!!.seekLayout.panelStartTime
        panelEndTime = binding!!.seekLayout.panelEndTime
        panelSeek = binding!!.seekLayout.panelSeek
        informationText = binding!!.informationText
        playBtn = binding!!.playBtn
        initPlayerUI()
        binding!!.selectImgBtn.setOnClickListener { v: View? ->
            CMediaPickDialog.builder().setNeedPreview(false)
                .setOnSingleListener { fileBean: FileBean? ->
                    ViseLog.d(fileBean)
                    imgBean = fileBean
                }.startSelectImg(this)
        }
        binding!!.selectMusicBtn.setOnClickListener { v: View? ->
            CMediaPickDialog.builder().setNeedPreview(false)
                .setOnSingleListener { fileBean1: FileBean ->
                    ViseLog.d(fileBean1)
                    initPlayer(binding!!.playerLayout)
                    videoBean = fileBean1
                    tip = "音频：" + fileBean1.fileName
                    startPlayer()
                }.startSelectMusic(this)
        }
        binding!!.selectVideBtn.setOnClickListener { v: View? ->
            CMediaPickDialog.builder().setNeedPreview(false).setShowCamera(false)
                .setOnSingleListener { fileBean1: FileBean ->
                    ViseLog.d(fileBean1)
                    initPlayer(binding!!.playerLayout)
                    if (U_file.copyVideoToMovie(this, fileBean1.filePath)) videoBean = fileBean1
                    tip =
                        "视频：" + fileBean1.fileName + " 宽高：" + fileBean1.videoFileBean!!.vWidth + "x" + fileBean1.videoFileBean!!.vHeight
                    startPlayer()
                }.startSelectVideo(this)
        }
    }

    private fun initProgress() {
        progressDialog = CProgressDialog("执行中")
        progressDialog!!.showDialog(this)
    }

    override fun initChooseUIWhenPrepare() {
        object : TaskDelayManager() {
            override fun onListen(index: Long) {
                player?.removeExtraView(areaView)
                areaView = ChooseAreaView(this@VideoEditActivity)
                player?.addExtraView(areaView)
                val playView = player?.getPlayerView()?.findViewById<View>(R.id.video_normal_id)
                areaView?.setLayoutSize(playView!!.width, playView.height)
            }
        }.delay(200)
    }

    override fun initChooseUI() {
        binding!!.cutLayout.removeAllViews()
        val cutView = LayoutInflater.from(this).inflate(R.layout.include_cut, null)
        cutView.tag = "cutView"
        val seekBar: RangeSeekBarView = cutView.findViewById(R.id.avt_seekBar)
        seekBar.setAbsoluteMinValuePrim(0.0)
        seekBar.setAbsoluteMaxValuePrim(videoBean!!.videoFileBean!!.mediaDuration.toDouble())
        seekBar.selectedMinValue = 0L
        seekBar.selectedMaxValue = videoBean!!.videoFileBean!!.mediaDuration
        seekBar.setMin_cut_time(3) //设置最小裁剪时间
        seekBar.isNotifyWhileDragging = true
        seekBar.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener)
        binding!!.cutLayout.addView(
            cutView,
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        cutBinding = IncludeCutBinding.bind(cutView)
    }

    //区域裁剪
    fun cutBtn(view: View?) {
        //选取框取出来的是比例，要和视频宽高相乘得到实际的宽高数据
        val left = areaView!!.resultF[0] * videoBean!!.videoFileBean!!.vWidth
        val top = areaView!!.resultF[1] * videoBean!!.videoFileBean!!.vHeight
        val width =
            (areaView!!.resultF[2] - areaView!!.resultF[0]) * videoBean!!.videoFileBean!!.vWidth
        val height =
            (areaView!!.resultF[3] - areaView!!.resultF[1]) * videoBean!!.videoFileBean!!.vHeight
        type = 1
        dealCopyFile()
        val cmd: Array<String> =
            FFmpegVideoUtils.cutVideoArea(inputFile!!, width, height, left, top, outputMP4)
        runFFmpeg(cmd)
    }

    //复制文件到私有目录
    private fun dealCopyFile() {
        inputFile = U_file.getAppCacheFolder(this@VideoEditActivity)
        if (!videoBean!!.filePath!!.startsWith(inputFile!!)) {
            inputFile = inputFile + "/source/" + videoBean!!.fileName
            U_file.copyFile(videoBean!!.filePath, inputFile, true)
        } else {
            inputFile = videoBean!!.filePath
        }
    }

    private fun runFFmpeg(cmd: Array<String>) {
        if (isRunning) {
            return
        } else isRunning = true
        ViseLog.d(cmd)
        initProgress()
        FFmpegCmd.getInstance().executeFFmpeg(cmd, object : OnHandleListener {
            override fun onStart() {}
            override fun onMessage(message: String) {
                ViseLog.d(message)
            }

            override fun onProgress(position: Int, duration: Int) {
                progressDialog!!.showProgress(position)
                ViseLog.showLog("$position $duration")
                if (position == 100) {
                    progressDialog!!.dismiss()
                }
            }

            override fun onFinish() {
                isRunning = false
                progressDialog!!.dismiss()
                when (type) {
                    1 -> {
                        U_Toast.show("完成 $outputMP4")
                        U_media.updateMedia(this@VideoEditActivity, outputMP4)
                        VideoPreviewActivity.start(this@VideoEditActivity, outputMP4)
                    }
                    2 -> {
                        U_Toast.show("完成 $outputMP3")
                        U_media.updateMedia(this@VideoEditActivity, outputMP3)
                        VideoPreviewActivity.start(this@VideoEditActivity, outputMP3)
                    }
                    3 -> {
                        U_Toast.show("完成 $outputGIF")
                        U_media.updateMedia(this@VideoEditActivity, outputGIF)
                        ImgPreviewActivity.start(this@VideoEditActivity, outputGIF)
                    }
                }
            }
        })
    }

    fun exportVideo(view: View?) {}

    //时长裁剪
    fun timeBtn(view: View?) {
        type = 1
        dealCopyFile()
        val cmd: Array<String> = FFmpegVideoUtils.cutVideoDurationWithFrame(
            inputFile!!,
            leftTimeMs, rightTimeMs - leftTimeMs, outputMP4
        )
        runFFmpeg(cmd)
    }

    private val scrollPos: Long = 0
    private val mOnRangeSeekBarChangeListener =
        OnRangeSeekBarChangeListener { bar, minValue, maxValue, action, isMin, pressedThumb ->

            //minValue左边值，maxValue右边值，action：1松开，2按下，pressedThumb min左边 ， max右边
            ViseLog.showLog("$minValue $maxValue")
            ViseLog.showLog("$action $isMin")
            ViseLog.showLog(pressedThumb)
            leftTimeMs = minValue + scrollPos
            rightTimeMs = maxValue + scrollPos
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP -> if (cutBinding != null) {
                    cutBinding!!.avtLeftTime.text =
                        U_time.convertLongToTime(leftTimeMs, U_time.HH_mm_ss, "GMT")
                    cutBinding!!.avtRightTime.text =
                        U_time.convertLongToTime(rightTimeMs, U_time.HH_mm_ss, "GMT")
                    cutBinding!!.avtSelectTime.text =
                        "已选择 " + (rightTimeMs / 1000 - leftTimeMs / 1000) + " 秒"
                    ViseLog.d("$leftTimeMs $rightTimeMs")
                }
                else -> {
                }
            }
        }

    //倒放
    fun invertedPlay(view: View?) {
        type = 1
        dealCopyFile()
        val cmd = FFmpegVideoUtils.reverseVideo(inputFile!!, outputMP4)
        runFFmpeg(cmd)
    }

    //画中画
    fun picInPic(view: View?) {
        type = 1
        dealCopyFile()
        val left = areaView!!.resultF[0] * videoBean!!.videoFileBean!!.vWidth
        val top = areaView!!.resultF[1] * videoBean!!.videoFileBean!!.vHeight
        val width =
            (areaView!!.resultF[2] - areaView!!.resultF[0]) * videoBean!!.videoFileBean!!.vWidth
        val height =
            (areaView!!.resultF[3] - areaView!!.resultF[1]) * videoBean!!.videoFileBean!!.vHeight
        val cmd = FFmpegVideoUtils.picInPicVideo(
            inputFile!!,
            inputFile!!,
            left.toInt(),
            top.toInt(),
            outputMP4
        )
        runFFmpeg(cmd)
    }

    //去水印
    fun removeWaterLogo(view: View?) {
        type = 1
        dealCopyFile()
        val left = areaView!!.resultF[0] * videoBean!!.videoFileBean!!.vWidth
        val top = areaView!!.resultF[1] * videoBean!!.videoFileBean!!.vHeight
        val width =
            (areaView!!.resultF[2] - areaView!!.resultF[0]) * videoBean!!.videoFileBean!!.vWidth
        val height =
            (areaView!!.resultF[3] - areaView!!.resultF[1]) * videoBean!!.videoFileBean!!.vHeight
        val cmd = FFmpegVideoUtils.removeLogo(
            inputFile!!,
            left.toInt(),
            top.toInt(),
            width.toInt(),
            height.toInt(),
            outputMP4
        )
        runFFmpeg(cmd)
    }

    //添加视频封面
    fun addVideoThumb(view: View?) {
        if (imgBean == null) {
            U_Toast.show("请先选择图片")
            return
        }
        type = 1
        dealCopyFile()
        val cmd: Array<String> =
            FFmpegVideoUtils.insertPicIntoVideo(inputFile!!, imgBean!!.filePath!!, outputMP4)
        runFFmpeg(cmd)
    }

    //转gif
    fun toGif(view: View) {
        type = 3
        dealCopyFile()
        val cmd: Array<String> =
            FFmpegVideoUtils.video2Gif(
                inputFile!!, 0,
                videoBean!!.videoFileBean!!.mediaDuration.toInt(), outputGIF
            )
        runFFmpeg(cmd)
    }

    //视频旋转，失败
    fun rotate(view: View) {
        type = 1
        dealCopyFile()
        val cmd: Array<String> =
            FFmpegVideoUtils.videoRotation(inputFile!!, 180, outputMP4)
        runFFmpeg(cmd)
    }
}