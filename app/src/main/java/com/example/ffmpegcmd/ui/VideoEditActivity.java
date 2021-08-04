package com.example.ffmpegcmd.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ffmpegcmd.databinding.ActivityEditBinding;
import com.xing.hhplayer.common.CVPlayer;
import com.xing.hhplayer.common.base.player.HHMediaPlayer;
import com.xing.hhplayer.common.bean.TvList.TvView;
import com.xing.hhplayer.common.bean.Type.PlayerState;
import com.xing.hhplayer.common.util.V_time;

import x.com.fliepick.media.CMediaPickDialog;
import x.com.log.ViseLog;

public class VideoEditActivity extends AppCompatActivity {
    ActivityEditBinding binding;
    private CVPlayer player;
    private Uri path;
    private String tip;
    private String outputPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.selectImgBtn.setOnClickListener(v -> CMediaPickDialog.builder().setOnSingleListener(fileBean -> ViseLog.d(fileBean)).startSelectImg(this));
        binding.selectMusicBtn.setOnClickListener(v -> CMediaPickDialog.builder().setNeedPreview(false).setOnSingleListener(fileBean -> {
            ViseLog.d(fileBean);
            initPlayer();
            path = fileBean.getFilePathUri();
            tip = "音频：" + fileBean.getFileName();
            binding.seekLayout.panelEndTime.setText(V_time.formatTime(fileBean.getMusicFileBean().getMediaDuration()));
            binding.seekLayout.panelSeek.setMax((int) (fileBean.getMusicFileBean().getMediaDuration()));
            startPlayer();
        }).startSelectMusic(this));
        binding.selectVideBtn.setOnClickListener(v -> CMediaPickDialog.builder().setNeedPreview(false).setOnSingleListener(fileBean -> {
            ViseLog.d(fileBean);
            initPlayer();
            path = fileBean.getFilePathUri();
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
            player = new CVPlayer(this, new HHMediaPlayer(), TvView.NEWSURFACE, binding.playerLayout);
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
            player.prepare(path);
            binding.informationText.setText(tip);
        }
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
        ViseLog.d("view");
    }
}
