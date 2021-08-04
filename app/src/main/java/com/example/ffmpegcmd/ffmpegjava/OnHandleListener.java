package com.example.ffmpegcmd.ffmpegjava;

/**
 * Created by Vashon on 2021/8/2.
 */
public interface OnHandleListener {

    void onStart();
    void onMessage(String message);
    void onProgress(int position, int duration);
    void onFinish();
}
