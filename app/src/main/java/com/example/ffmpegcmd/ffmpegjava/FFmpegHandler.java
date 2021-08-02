package com.example.ffmpegcmd.ffmpegjava;

import android.os.Handler;

/**
 * Created by Vashon on 2021/8/2.
 */
public class FFmpegHandler {

    private Handler mHandler;

    public FFmpegHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    void executeFFmpegCmd(String[] commands) {
        if (mHandler != null) {
            FFmpegCmd.getInstance().execute(commands, new OnHandleListener() {
                @Override
                public void onStart() {
                    mHandler.obtainMessage(0).sendToTarget();
                }

                @Override
                public void onMessage(String message) {
                    mHandler.obtainMessage(0, message).sendToTarget();
                }

                @Override
                public void onProgress(int position, int duration) {
                    mHandler.obtainMessage(0, position, duration).sendToTarget();
                }

                @Override
                public void onFinish() {
                    mHandler.obtainMessage(0).sendToTarget();
                }
            });
        }
    }
}
