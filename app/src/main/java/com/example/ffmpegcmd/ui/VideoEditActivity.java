package com.example.ffmpegcmd.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ffmpegcmd.databinding.ActivityEditBinding;
import com.xing.hhplayer.common.CVPlayer;
import com.xing.hhplayer.common.base.player.HHMediaPlayer;
import com.xing.hhplayer.common.bean.TvList.TvView;
import com.xing.hhplayer.common.bean.Type.PlayerState;

import x.com.fliepick.media.CMediaPickDialog;
import x.com.log.ViseLog;

public class VideoEditActivity extends AppCompatActivity {
    ActivityEditBinding binding;
    private CVPlayer player;
    private Uri path;
    private String tip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.selectImgBtn.setOnClickListener(v -> {
            CMediaPickDialog.builder().setOnSingleListener(fileBean -> ViseLog.d(fileBean)).startSelectImg(this);
        });
        binding.selectMusicBtn.setOnClickListener(v -> {
            CMediaPickDialog.builder().setOnSingleListener(fileBean -> {
                ViseLog.d(fileBean);
                initPlayer();
                path = fileBean.getFilePathUri();
                tip = "音频：" + fileBean.getFileName() + " 时长：" + fileBean.getVideoFileBean().getMediaDuration();
                startPlayer();
            }).startSelectMusic(this);
        });
        binding.selectVideBtn.setOnClickListener(v -> {
            CMediaPickDialog.builder().setOnSingleListener(fileBean -> {
                ViseLog.d(fileBean);
                initPlayer();
                path = fileBean.getFilePathUri();
                tip = "视频：" + fileBean.getFileName() + " 宽高：" + fileBean.getVideoFileBean().getVWidth() + "x" + fileBean.getVideoFileBean().getVHeight() + " 时长：" + fileBean.getVideoFileBean().getMediaDuration();
                startPlayer();
            }).startSelectVideo(this);
        });
    }

    //先用个播放器做预览吧，后面再换
    private void initPlayer() {
        if (player == null) {
            player = new CVPlayer(this, new HHMediaPlayer(), TvView.NEWSURFACE, binding.playerLayout);
            player.setPlayWhenPrepared(true);
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
        player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}
