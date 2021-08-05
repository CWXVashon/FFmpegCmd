package com.example.ffmpegcmd.presenter;

import com.example.ffmpegcmd.ui.iview.IView;

/**
 * Created by Vashon on 2021/8/5.
 */
public abstract class BasePresenter<V extends IView> {

    protected V mView;

    public BasePresenter(V view) {
        this.mView = view;
        init();
    }

    protected abstract void init();

    public void onDestroy() {
        mView = null;
    }
}
