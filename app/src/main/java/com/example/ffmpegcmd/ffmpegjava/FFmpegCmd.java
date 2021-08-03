package com.example.ffmpegcmd.ffmpegjava;

import androidx.annotation.Keep;

import com.example.ffmpegcmd.util.ThreadPoolExecutor;

/**
 * Created by Vashon on 2021/8/2.
 */
public class FFmpegCmd {

    private static final int STATE_INIT = 0;
    private static final int STATE_RUNNING = 1;
    private static final int STATE_FINISH = 2;
    private static final int STATE_ERROR = 3;

    private static FFmpegCmd sFFmpegCmd;
    private OnHandleListener mListener;

    private FFmpegCmd() {
    }

    public static FFmpegCmd getInstance() {
        if (sFFmpegCmd == null) {
            synchronized (FFmpegCmd.class) {
                if (sFFmpegCmd == null)
                    sFFmpegCmd = new FFmpegCmd();
            }
        }
        return sFFmpegCmd;
    }

    static {
        System.loadLibrary("ffmpeg-cmd");
    }

    void executeFFmpeg(String[] commands, OnHandleListener handleListener) {
        mListener = handleListener;
        ThreadPoolExecutor.INSTANCE.executeSingleThreadPool(new Runnable() {
            @Override
            public void run() {
                if (handleListener != null) {
                    handleListener.onStart();
                    runFFmpeg(commands);
                    handleListener.onFinish();
                }
            }
        });
    }

    public String executeFFprobe(String[] commands) {
        return runFFprobe(commands);
    }

    /**
     * native 层自动回调这个方法
     * @param state 0-初始化，1-运行中，2-运行结束，3-运行错误
     */
    @Keep
    private void onProgressCallback(int position, int duration, int state) {
        if (position > duration && duration > 0)
            return;
        if (mListener != null) {
            if (position > 0 && duration > 0) {
                int progress = position * 100 / duration;
                if (progress < 100 || state == STATE_FINISH || state == STATE_ERROR) {
                    mListener.onProgress(progress, duration);
                }
            } else {
                mListener.onProgress(position, duration);
            }
        }
    }

    /**
     * native 层自动回调这个方法
     * @param message
     */
    @Keep
    private void onMsgCallback(String message) {
        if (mListener != null)
            mListener.onMessage(message);
    }

    // 执行 ffmpeg 命令
    private native int runFFmpeg(String[] commands);

    // 执行 ffprobe 命令
    private native String runFFprobe(String[] commands);

    // 取消进行中的任务，0：不取消，1：取消
    private native void cancelTaskJNI(int cancel);
}