package com.example.ffmpegcmd.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ffmpegcmd.databinding.ActivityPreviewBinding;
import com.xing.hhplayer.common.CVPlayer;
import com.xing.hhplayer.common.base.player.HHMediaPlayer;
import com.xing.hhplayer.common.bean.TvList.TvView;
import com.xing.hhplayer.common.bean.Type.PlayerState;
import com.xing.hhplayer.common.util.V_time;

/**
 * 视频合成预览页面
 */
public class PreviewActivity extends AppCompatActivity {
    ActivityPreviewBinding binding;
    private CVPlayer player;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        path = getIntent().getStringExtra("path");
        binding.informationText.setText(path);
        initPlayer();
        startPlayer();
        binding.playBtn.setOnClickListener(v -> {
            if (player == null) return;
            if (player.isPlaying()) {
                pausePlayer();
            } else {
                startPlayer();
            }
        });
        binding.backBtn.setOnClickListener(v -> finish());
        binding.seekLayout.panelSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (player == null) return;
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

    public static void start(Context context, String path) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }

    //先用个播放器做预览吧，后面再换
    private void initPlayer() {
        if (player == null) {
            player = new CVPlayer(this, new HHMediaPlayer(), TvView.NEWSURFACE, binding.playerLayout);
            player.setPlayWhenPrepared(true);
            player.setOnPreparedListener(o -> {
                binding.seekLayout.panelEndTime.setText(V_time.formatTime(player.getMediaDurationMS()));
                binding.seekLayout.panelSeek.setMax((int) (player.getMediaDurationMS()));
            });
            player.setOnProgressListener(time -> {
                if (player == null) return;
                runOnUiThread(() -> {
                    binding.seekLayout.panelSeek.setProgress((int) (time * 1));
                    binding.seekLayout.panelStartTime.setText(V_time.formatTime(time));
                });
            });
        }
    }

    private void startPlayer() {
        if (player.getPlayerState() == PlayerState.PAUSE) {
            player.start();
        } else {
            player.reset();
            player.prepare(path);
            binding.seekLayout.panelEndTime.setText(V_time.formatTime(player.getMediaDurationMS()));
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
