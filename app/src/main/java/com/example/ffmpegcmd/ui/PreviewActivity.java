package com.example.ffmpegcmd.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.ffmpegcmd.databinding.ActivityPreviewBinding;

/**
 * 视频合成预览页面
 */
public class PreviewActivity extends VideoPlayerActivity {
    ActivityPreviewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        panelStartTime = binding.seekLayout.panelStartTime;
        panelEndTime = binding.seekLayout.panelEndTime;
        panelSeek = binding.seekLayout.panelSeek;
        informationText = binding.informationText;
        playBtn = binding.playBtn;
        initPlayerUI();
        path = getIntent().getStringExtra("path");
        binding.informationText.setText(path);
        initPlayer(binding.playerLayout);
        startPlayer();
        binding.backBtn.setOnClickListener(v -> finish());
    }

    public static void start(Context context, String path) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }
}
