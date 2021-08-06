package com.example.ffmpegcmd.bean;

import java.util.List;

/**
 * Created by Vashon on 2021/8/6.
 */
public class AudioBean {

    //"codec_tag_string": "mp4a"
    public String audioCodec;

    //"sample_rate": "44100"
    public int sampleRate;

    //"channels": 2
    public int channels;

    //"channel_layout": "stereo"
    public String channelLayout;

    public String title;

    public String artist;

    public String album;

    public String albumArtist;

    public String composer;

    public String genre;

    public List<String> lyrics;
}
