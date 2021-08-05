package com.example.ffmpegcmd.util;

import java.util.ArrayList;
import java.util.List;

/**
 * ffmpeg命令构建
 */
public class FFmpegBuilder {
    private final List<String> cmd;
    private boolean isRepeat = true;

    public FFmpegBuilder() {
        cmd = new ArrayList<>();
        cmd.add("ffmpeg");
    }

    //添加输入文件
    public FFmpegBuilder inputPath(String path) {
        cmd.add("-i");
        cmd.add(path);
        return this;
    }

    //添加输出文件
    public FFmpegBuilder outputPath(String path) {
        cmd.add(path);
        return this;
    }

    //添加起始时间，单位毫秒
    public FFmpegBuilder addStartTime(long time) {
        cmd.add("-ss");
        cmd.add(String.valueOf(time / 1000));
        return this;
    }

    //添加起始时间，格式如00:01:00
    public FFmpegBuilder addStartTime(String time) {
        cmd.add("-ss");
        cmd.add(time);
        return this;
    }

    //添加截止时间
    public FFmpegBuilder addToTime(long time) {
        cmd.add("-to");
        cmd.add(String.valueOf(time / 1000));
        return this;
    }

    //添加截止时间
    public FFmpegBuilder addToTime(String time) {
        cmd.add("-to");
        cmd.add(time);
        return this;
    }

    //添加持续时间
    public FFmpegBuilder addDurationTime(long time) {
        cmd.add("-t");
        cmd.add(String.valueOf(time / 1000));
        return this;
    }

    //添加持续时间
    public FFmpegBuilder addDurationTime(String time) {
        cmd.add("-t");
        cmd.add(time);
        return this;
    }

    //是否覆盖文件，默认覆盖
    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    //添加标题
    public FFmpegBuilder addTitle(String title) {
        cmd.add("-title");
        cmd.add(title);
        return this;
    }

    //添加作者
    public FFmpegBuilder addAuthor(String author) {
        cmd.add("-author");
        cmd.add(author);
        return this;
    }

    //添加版权
    public FFmpegBuilder addCopyright(String copyright) {
        cmd.add("-copyright");
        cmd.add(copyright);
        return this;
    }

    //设置fps
    public FFmpegBuilder setFps(int fps) {
        cmd.add("-r");
        cmd.add(String.valueOf(fps));
        return this;
    }

    //设置比特率
    public FFmpegBuilder setBitrate(int bitrate) {
        cmd.add("-b");
        cmd.add(String.valueOf(bitrate));
        return this;
    }

    //设置vcodec
    public FFmpegBuilder setVCodec(String codec) {
        cmd.add("-vcodec");
        cmd.add(codec);
        return this;
    }

    //设置acodec
    public FFmpegBuilder setACodec(String codec) {
        cmd.add("-acodec");
        cmd.add(codec);
        return this;
    }

    //设置大小
    public FFmpegBuilder setSize(int width, int height) {
        cmd.add("-s");
        cmd.add(width + "x" + height);
        return this;
    }

    //设置宽高比
    public FFmpegBuilder setAspect(float aspect) {
        cmd.add("-aspect");
        cmd.add(String.valueOf(aspect));
        return this;
    }

    //不处理视频
    public FFmpegBuilder addVn() {
        cmd.add("-vn");
        return this;
    }

    //不处理音频
    public FFmpegBuilder addAn() {
        cmd.add("-an");
        return this;
    }

    /**
     * 其他自定参数
     *
     * @param param 参数
     * @return this
     */
    public FFmpegBuilder addParam(String param) {
        cmd.add(param);
        return this;
    }

    public FFmpegBuilder addParam(String param1, String param2) {
        cmd.add(param1);
        cmd.add(param2);
        return this;
    }

    public String[] build() {
        if (isRepeat) {
            cmd.add("-y");
        }
        return cmd.toArray(new String[0]);
    }
}
