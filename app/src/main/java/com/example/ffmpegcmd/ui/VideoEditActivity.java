package com.example.ffmpegcmd.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.example.ffmpegcmd.R;
import com.example.ffmpegcmd.bean.TestBean;
import com.example.ffmpegcmd.databinding.ActivityEditBinding;
import com.example.ffmpegcmd.databinding.IncludeCutBinding;
import com.example.ffmpegcmd.ffmpegjava.FFmpegCmd;
import com.example.ffmpegcmd.ffmpegjava.OnHandleListener;
import com.example.ffmpegcmd.ui.widget.ChooseAreaView;
import com.example.ffmpegcmd.ui.widget.RangeSeekBarView;
import com.example.ffmpegcmd.util.FFmpegVideoUtils;

import x.com.base.toast.U_Toast;
import x.com.dialog.CProgressDialog;
import x.com.fliepick.media.CMediaPickDialog;
import x.com.log.ViseLog;
import x.com.util.U_file;
import x.com.util.U_time;

public class VideoEditActivity extends VideoPlayerActivity {
    ActivityEditBinding binding;
    IncludeCutBinding cutBinding;
    private String outputPath;
    private ChooseAreaView areaView;
    private CProgressDialog progressDialog;
    private long leftTimeMs, rightTimeMs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        panelStartTime = binding.seekLayout.panelStartTime;
        panelEndTime = binding.seekLayout.panelEndTime;
        panelSeek = binding.seekLayout.panelSeek;
        informationText = binding.informationText;
        playBtn = binding.playBtn;
        initPlayerUI();
        binding.selectImgBtn.setOnClickListener(v -> CMediaPickDialog.builder().setOnSingleListener(fileBean -> ViseLog.d(fileBean)).startSelectImg(this));
        binding.selectMusicBtn.setOnClickListener(v -> CMediaPickDialog.builder().setNeedPreview(false).setOnSingleListener(fileBean1 -> {
            ViseLog.d(fileBean1);
            initPlayer(binding.playerLayout);
            this.fileBean = fileBean1;
            tip = "音频：" + fileBean1.getFileName();
            startPlayer();
        }).startSelectMusic(this));
        binding.selectVideBtn.setOnClickListener(v -> CMediaPickDialog.builder().setNeedPreview(false).setShowCamera(false).setOnSingleListener(fileBean1 -> {
            ViseLog.d(fileBean1);
            initPlayer(binding.playerLayout);
            this.fileBean = fileBean1;
            tip = "视频：" + fileBean1.getFileName() + " 宽高：" + fileBean1.getVideoFileBean().getVWidth() + "x" + fileBean1.getVideoFileBean().getVHeight();
            startPlayer();
        }).startSelectVideo(this));
    }

    private void initProgress() {
        progressDialog = new CProgressDialog("执行中");
        progressDialog.showDialog(this);
    }

    //去预览
    private void gotoPreview() {
        PreviewActivity.start(this, outputPath);
    }

    public void cutBtn(View view) {
        chooseAreaView();
    }

    private void chooseAreaView() {
        if (areaView != null) {
            player.removeExtraView(areaView);
            areaView = null;
        } else {
            areaView = new ChooseAreaView(VideoEditActivity.this);
            player.addExtraView(areaView);
            View playView = player.getPlayerView().findViewById(R.id.video_normal_id);
            areaView.setLayoutSize(playView.getWidth(), playView.getHeight());
        }
    }

    public void exportVideo(View view) {
        //选取框取出来的是比例，要和视频宽高相乘得到实际的宽高数据
//        float left = (areaView.getResultF()[0] * fileBean.getVideoFileBean().getVWidth());
//        float top = (areaView.getResultF()[1] * fileBean.getVideoFileBean().getVHeight());
//        float width = (areaView.getResultF()[2] - areaView.getResultF()[0]) * fileBean.getVideoFileBean().getVWidth();
//        float height = (areaView.getResultF()[3] - areaView.getResultF()[1]) * fileBean.getVideoFileBean().getVHeight();
//
//        String[] cmd = FFmpegVideoUtils.cutVideoArea(fileBean.getFilePath(),
//                width, height, left, top, U_file.DOWNLOADS + "/" + TestBean.outputMp4Name);
        String[] cmd = FFmpegVideoUtils.cutVideoDurationWithFrame(fileBean.getFilePath(),
                leftTimeMs, rightTimeMs - leftTimeMs, U_file.DOWNLOADS + "/" + TestBean.outputMp4Name);
        ViseLog.d(cmd);
        initProgress();
        FFmpegCmd.getInstance().executeFFmpeg(cmd, new OnHandleListener() {
            @Override
            public void onStart() {
                U_Toast.show("开始");
            }

            @Override
            public void onMessage(String message) {
                ViseLog.d(message);
            }

            @Override
            public void onProgress(int position, int duration) {
                progressDialog.showProgress(position);
                if (position == 100) {
                    progressDialog.dismiss();
                }
                ViseLog.showLog(position + " " + duration);
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                U_Toast.show("完成 " + U_file.DOWNLOADS + "/" + TestBean.outputMp4Name);
                PreviewActivity.start(VideoEditActivity.this, U_file.DOWNLOADS + "/" + TestBean.outputMp4Name);
            }
        });
    }

    public void timeBtn(View view) {
        if (binding.cutLayout.findViewWithTag("cutView") == null) {
            View cutView = LayoutInflater.from(this).inflate(R.layout.include_cut, null);
            cutView.setTag("cutView");
            RangeSeekBarView seekBar = cutView.findViewById(R.id.avt_seekBar);
            seekBar.setAbsoluteMinValuePrim(0);
            seekBar.setAbsoluteMaxValuePrim(fileBean.getVideoFileBean().getMediaDuration());
            seekBar.setSelectedMinValue(0L);
            seekBar.setSelectedMaxValue(fileBean.getVideoFileBean().getMediaDuration());
            seekBar.setMin_cut_time(3);//设置最小裁剪时间
            seekBar.setNotifyWhileDragging(true);
            seekBar.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener);
            binding.cutLayout.addView(cutView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            cutBinding = IncludeCutBinding.bind(cutView);
        } else {
            binding.cutLayout.removeAllViews();
        }
    }

    private long scrollPos;
    private final RangeSeekBarView.OnRangeSeekBarChangeListener mOnRangeSeekBarChangeListener = new RangeSeekBarView.OnRangeSeekBarChangeListener() {
        @Override//minValue左边值，maxValue右边值，action：1松开，2按下，pressedThumb min左边 ， max右边
        public void onRangeSeekBarValuesChanged(RangeSeekBarView bar, long minValue, long maxValue, int action, boolean isMin, RangeSeekBarView.Thumb pressedThumb) {
            ViseLog.showLog(minValue + " " + maxValue);
            ViseLog.showLog(action + " " + isMin);
            ViseLog.showLog(pressedThumb);
            leftTimeMs = minValue + scrollPos;
            rightTimeMs = maxValue + scrollPos;
            switch (action) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:
//                    if (videoController != null) {
//                        videoController.seekTo(pressedThumb == RangeSeekBarView.Thumb.MIN ? leftTimeMs : rightTimeMs);
//                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //从minValue开始播
//                    if (videoController != null) {
//                        videoController.seekTo(leftTimeMs);
//                        videoController.setPlayRange(leftTimeMs, rightTimeMs);
//                        videoController.doStart();
//                    }
                    if (cutBinding != null) {
                        cutBinding.avtLeftTime.setText(U_time.convertLongToTime(leftTimeMs, U_time.HH_mm_ss, "GMT"));
                        cutBinding.avtRightTime.setText(U_time.convertLongToTime(rightTimeMs, U_time.HH_mm_ss, "GMT"));
                        cutBinding.avtSelectTime.setText("已选择 " + (rightTimeMs / 1000 - leftTimeMs / 1000) + " 秒");
                        ViseLog.d(leftTimeMs + " " + rightTimeMs);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
