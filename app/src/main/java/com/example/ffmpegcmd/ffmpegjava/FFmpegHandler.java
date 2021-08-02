package com.example.ffmpegcmd.ffmpegjava;

import android.os.Handler;

/**
 * Created by Vashon on 2021/8/2.
 */
public class FFmpegHandler {

    public static final int STATE_START = 615;
    public static final int STATE_MESSAGE = 616;
    public static final int STATE_PROGRESS = 617;
    public static final int STATE_FINISH = 618;

    private Handler mHandler;

    public FFmpegHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void executeFFmpegCmd(String[] commands) {
        if (mHandler != null) {
            FFmpegCmd.getInstance().execute(commands, new OnHandleListener() {
                @Override
                public void onStart() {
                    mHandler.obtainMessage(STATE_START).sendToTarget();
                }

                @Override
                public void onMessage(String message) {
                    mHandler.obtainMessage(STATE_MESSAGE, message).sendToTarget();
                }

                @Override
                public void onProgress(int position, int duration) {
                    mHandler.obtainMessage(STATE_PROGRESS, position, duration).sendToTarget();
                }

                @Override
                public void onFinish() {
                    mHandler.obtainMessage(STATE_FINISH).sendToTarget();
                }
            });
        }
    }
}
