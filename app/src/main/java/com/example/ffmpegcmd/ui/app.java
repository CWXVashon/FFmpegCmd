package com.example.ffmpegcmd.ui;

import android.app.Application;

import x.com.base.app.AppInit;


public class app extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppInit.isDebug = true;
        AppInit.initToast(this);
        AppInit.initLog();
        AppInit.setNeverCrash();
    }
}
