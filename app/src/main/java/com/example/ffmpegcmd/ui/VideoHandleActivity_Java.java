package com.example.ffmpegcmd.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ffmpegcmd.R;
import com.example.ffmpegcmd.databinding.ActivityVideoHandleBinding;
import com.example.ffmpegcmd.ffmpegjava.FFmpegHandler;

/**
 * Created by Vashon on 2021/8/2.
 */
public class VideoHandleActivity_Java extends AppCompatActivity implements View.OnClickListener {

    private ActivityVideoHandleBinding binding;
    private VideoHandler mVideoHandler;
    private FFmpegHandler mFFmpegHandler;

    public static void start(Context context) {
        Intent starter = new Intent(context, VideoHandleActivity_Java.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoHandleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVideoHandler = new VideoHandler(this);
        mFFmpegHandler = new FFmpegHandler(mVideoHandler);
    }

    @Override
    public void onClick(View v) {
        // TODO: 2021/8/2 处理对应的点击事件，通过命令行工具 FFmpegUtils 得到命令，然后调用命令行处理器
        String[] commands = new String[]{"命令行..."};
        mFFmpegHandler.executeFFmpegCmd(commands);
    }

    @Override
    protected void onDestroy() {
        mVideoHandler.onDestroy();
        super.onDestroy();
    }

    private static class VideoHandler extends Handler {

        private VideoHandleActivity_Java mActivity;

        public VideoHandler(VideoHandleActivity_Java mActivity) {
            super(Looper.getMainLooper());
            this.mActivity = mActivity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            // TODO: 2021/8/2 刷新UI
            switch (msg.what) {
                case FFmpegHandler.STATE_START:
                    break;
                case FFmpegHandler.STATE_MESSAGE:
                    break;
                case FFmpegHandler.STATE_PROGRESS:
                    break;
                case FFmpegHandler.STATE_FINISH:
                    break;
                default:
            }
        }

        void onDestroy() {
            mActivity = null;
        }
    }
}
