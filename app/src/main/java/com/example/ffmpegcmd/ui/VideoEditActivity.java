package com.example.ffmpegcmd.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ffmpegcmd.R;
import com.example.ffmpegcmd.bean.TestBean;
import com.example.ffmpegcmd.databinding.ActivityEditBinding;
import com.example.ffmpegcmd.ffmpegjava.FFmpegCmd;
import com.example.ffmpegcmd.ffmpegjava.OnHandleListener;
import com.example.ffmpegcmd.ui.widget.ChooseAreaView;
import com.example.ffmpegcmd.util.CVPlayer2;
import com.example.ffmpegcmd.util.FFmpegVideoUtils;
import com.xing.hhplayer.common.base.player.HHMediaPlayer;
import com.xing.hhplayer.common.bean.TvList.TvView;
import com.xing.hhplayer.common.bean.Type.PlayerState;
import com.xing.hhplayer.common.util.V_time;

import x.com.base.toast.U_Toast;
import x.com.dialog.CProgressDialog;
import x.com.fliepick.bean.FileBean;
import x.com.fliepick.media.CMediaPickDialog;
import x.com.log.ViseLog;
import x.com.util.U_file;

public class VideoEditActivity extends AppCompatActivity {
    ActivityEditBinding binding;
    private CVPlayer2 player;
    private FileBean fileBean;
    private String tip;
    private String outputPath;
    private ChooseAreaView areaView;
    private CProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.selectImgBtn.setOnClickListener(v -> CMediaPickDialog.builder().setOnSingleListener(fileBean -> ViseLog.d(fileBean)).startSelectImg(this));
        binding.selectMusicBtn.setOnClickListener(v -> CMediaPickDialog.builder().setNeedPreview(false).setOnSingleListener(fileBean -> {
            ViseLog.d(fileBean);
            initPlayer();
            this.fileBean = fileBean;
            tip = "音频：" + fileBean.getFileName();
            binding.seekLayout.panelEndTime.setText(V_time.formatTime(fileBean.getMusicFileBean().getMediaDuration()));
            binding.seekLayout.panelSeek.setMax((int) (fileBean.getMusicFileBean().getMediaDuration()));
            startPlayer();
        }).startSelectMusic(this));
        binding.selectVideBtn.setOnClickListener(v -> CMediaPickDialog.builder().setNeedPreview(false).setOnSingleListener(fileBean -> {
            ViseLog.d(fileBean);
            initPlayer();
            this.fileBean = fileBean;
            tip = "视频：" + fileBean.getFileName() + " 宽高：" + fileBean.getVideoFileBean().getVWidth() + "x" + fileBean.getVideoFileBean().getVHeight();
            binding.seekLayout.panelEndTime.setText(V_time.formatTime(fileBean.getVideoFileBean().getMediaDuration()));
            binding.seekLayout.panelSeek.setMax((int) (fileBean.getVideoFileBean().getMediaDuration()));
            startPlayer();
        }).startSelectVideo(this));
        binding.playBtn.setOnClickListener(v -> {
            if (player == null) return;
            if (player.isPlaying()) {
                pausePlayer();
            } else {
                startPlayer();
            }
        });
        binding.seekLayout.panelSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (player != null)
                        player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //先用个播放器做预览吧，后面再换
    private void initPlayer() {
        if (player == null) {
            player = new CVPlayer2(this, new HHMediaPlayer(), TvView.NEWSURFACE, binding.playerLayout);
            player.setPlayWhenPrepared(true);
            player.setOnProgressListener(time -> runOnUiThread(() -> {
                binding.seekLayout.panelSeek.setProgress(Integer.parseInt(time.toString()));
                binding.seekLayout.panelStartTime.setText(V_time.formatTime(time));
            }));
        }
    }

    private void startPlayer() {
        if (player.getPlayerState() == PlayerState.PAUSE) {
            player.start();
        } else {
            player.reset();
            player.prepare(fileBean.getFilePathUri());
            binding.informationText.setText(tip);
        }
    }

    private void initProgress() {
        progressDialog = new CProgressDialog("执行中");
        progressDialog.showDialog(this);
    }

    private void pausePlayer() {
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
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
        float left = (areaView.getResultF()[0] * fileBean.getVideoFileBean().getVWidth());
        float top = (areaView.getResultF()[1] * fileBean.getVideoFileBean().getVHeight());
        float width = (areaView.getResultF()[2] - areaView.getResultF()[0]) * fileBean.getVideoFileBean().getVWidth();
        float height = (areaView.getResultF()[3] - areaView.getResultF()[1]) * fileBean.getVideoFileBean().getVHeight();

        String[] cmd = FFmpegVideoUtils.cutVideoArea(fileBean.getFilePath(),
                width, height, left, top, U_file.DOWNLOADS + "/" + TestBean.outputMp4Name);
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
}
