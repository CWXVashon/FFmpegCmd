package com.example.ffmpegcmd.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ffmpegcmd.bean.TestBean;
import com.example.ffmpegcmd.ffmpegjava.FFmpegCmd;
import com.example.ffmpegcmd.ffmpegjava.OnHandleListener;
import com.example.ffmpegcmd.util.FFmpegUtils;

import java.util.Map;

import x.com.base.toast.U_Toast;
import x.com.dialog.CProgressDialog;
import x.com.fliepick.bean.FileBean;
import x.com.fliepick.bean.MediaSelectListener;
import x.com.fliepick.media.CMediaPickDialog;
import x.com.log.ViseLog;
import x.com.media.U_mediaList;
import x.com.util.U_file;
import x.com.util.U_permissions;

public class TestActivity extends AppCompatActivity {
    private CProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        U_permissions.applyStoragePermission11(this, new U_permissions.RequestPermissionCallBack() {
            @Override
            public void requestPermissionSuccess() {
                //选择视频
//                CMediaPickDialog.builder().setOnSingleListener(new MediaSelectListener<FileBean>() {
//                    @Override
//                    public void onListen(FileBean fileBean) {
//                        ViseLog.d(fileBean);
//                    }
//                }).startSelectVideo(TestActivity.this);

//                U_file.copyFile(TestBean.localMp3Url, TestBean.outputFolder(TestActivity.this) + "source.mp3", true);
                String[] cmd = FFmpegUtils.cutAudio(TestBean.localMp3Url, 3000, 8000, TestBean.outputFolder(TestActivity.this) + TestBean.outputMp3Name);
                ViseLog.d(cmd);
                FFmpegCmd.getInstance().executeFFmpeg(cmd, new OnHandleListener() {
                    @Override
                    public void onStart() {
                        U_Toast.show("开始");
                    }

                    @Override
                    public void onMessage(String message) {
                        ViseLog.d(message);
                    }

                    @Override
                    public void onProgress(int position, int duration) {
                        ViseLog.showLog(position + " " + duration);
                    }

                    @Override
                    public void onFinish() {
                        U_Toast.show("完成 " + TestBean.outputFolder(TestActivity.this) + TestBean.outputMp3Name);
                    }
                });
            }

            @Override
            public void requestPermissionFail(Map<String, Boolean> failPermission) {

            }
        });
    }
}
