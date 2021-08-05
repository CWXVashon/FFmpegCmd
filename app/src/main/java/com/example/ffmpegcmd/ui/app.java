package com.example.ffmpegcmd.ui;

import android.app.Application;
import android.content.Context;

import x.com.base.app.AppInit;


public class app extends Application {

    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        AppInit.isDebug = true;
        AppInit.initToast(this);
        AppInit.initLog();
        AppInit.setNeverCrash();
    }
}
