package com.example.ffmpegcmd.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ffmpegcmd.R;
import com.example.ffmpegcmd.databinding.ActivityVideoHandleBinding;
import com.example.ffmpegcmd.ffmpegjava.FFmpegHandler;
import com.example.ffmpegcmd.util.FFmpegUtils;

import java.io.File;

/**
 * Created by Vashon on 2021/8/2.
 */
public class VideoHandleActivity_Java extends AppCompatActivity implements View.OnClickListener {

    private ActivityVideoHandleBinding binding;
    private VideoHandler mVideoHandler;
    private FFmpegHandler mFFmpegHandler;
    private TextView ffprobeMsgTv;

    public static void start(Context context) {
        Intent starter = new Intent(context, VideoHandleActivity_Java.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoHandleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button button = binding.testFfprobeBtn;
        button.setOnClickListener(this);
        ffprobeMsgTv = binding.ffprobeMsgTv;

        mVideoHandler = new VideoHandler(this);
        mFFmpegHandler = new FFmpegHandler(mVideoHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_ffprobe_btn:
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "cat.mp4");
                if (file.exists()) {
                    Log.e("--------------", "文件存在");
                } else {
                    Log.e("--------------", "文件不存在");
                }
                String path = file.getAbsolutePath();
                Log.e("--------------", path);
                String jsonStr = mFFmpegHandler.executeFFprobeCmd(FFmpegUtils.probeFormat(path));
                Log.e("--------------", jsonStr);
                ffprobeMsgTv.setText(jsonStr);
                break;
        }
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
