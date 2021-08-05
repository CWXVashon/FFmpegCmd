package com.example.ffmpegcmd.ui.iview;

/**
 * Created by Vashon on 2021/8/5.
 */
public interface IView {

    void showToast(String message);

    void showLoading(String message);

    void hideLoading();
}
