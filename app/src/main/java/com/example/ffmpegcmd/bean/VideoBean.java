package com.example.ffmpegcmd.bean;

/**
 * Created by Vashon on 2021/8/6.
 */
public class VideoBean {

    //"codec_tag_string": "avc1"
    public String videoCodec;

    //"width": 640
    public int width;

    //"height": 360
    public int height;

    //"display_aspect_ratio": "16:9"
    public String displayAspectRatio;

    //"pix_fmt": "yuv420p"
    public String pixelFormat;

    //"profile": "578"
    public String profile;

    //"level": 30
    public int level;

    //"r_frame_rate": "24000/1001"
    public int frameRate;
}
