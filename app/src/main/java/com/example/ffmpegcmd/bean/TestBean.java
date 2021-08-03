package com.example.ffmpegcmd.bean;

import android.content.Context;

import java.io.File;

import x.com.util.U_file;
import x.com.util.U_time;

public class TestBean {
    //测试在线mp4视频，15秒
    public static String mp4Url = "http://xiyou.sc.diyixin.com/dev-video-laidian/20200715/1594815638235.mp4";
    //测试在线mp3,34秒
    public static String mp3Url = "https://m3.8js.net:99//20181222/xuemaojiao%20xiaofengfeng%20xiaopanpan.mp3";
    public static String localMp3Url = U_file.SDROOT + "/Android/data/com.example.ffmpegcmd/cache/source.mp3";

    //输出文件名
    public static String outputMp4Name = "ff_" + U_time.getNowTimeLong() + ".mp4";
    public static String outputMp3Name = "ff_" + U_time.getNowTimeLong() + ".mp3";
    public static String outputAACName = "ff_" + U_time.getNowTimeLong() + ".aac";

    //输出路径文件夹
    public static String outputFolder(Context context) {
        return U_file.getAppCacheFolder(context) + File.separator;
    }
}
