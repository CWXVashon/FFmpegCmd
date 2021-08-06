package com.example.ffmpegcmd.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.ffmpegcmd.bean.AudioBean;
import com.example.ffmpegcmd.bean.MediaBean;
import com.example.ffmpegcmd.bean.VideoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Vashon on 2021/8/5.
 * 用于解析 ffprobe 返回的 json 字符串
 */
public class JsonUtils {

    private static final String TYPE_VIDEO = "video";
    private static final String TYPE_AUDIO = "audio";

    @NonNull
    public static MediaBean parseMediaFormat(String json) {
        MediaBean media = new MediaBean();
        if (TextUtils.isEmpty(json))
            return media;
        try {
            JSONObject mediaJson = new JSONObject(json);
            JSONObject format = mediaJson.getJSONObject("format");
            media.streamNum = format.optInt("nb_streams");
            media.formatName = format.optString("format_name");
            String bitRateStr = format.optString("bit_rate");
            if (!TextUtils.isEmpty(bitRateStr)) {
                media.bitRate = Integer.parseInt(bitRateStr);
            }
            String sizeStr = format.optString("size");
            if (!TextUtils.isEmpty(sizeStr)) {
                media.size = Long.parseLong(sizeStr);
            }
            String durationStr = format.optString("duration");
            if (!TextUtils.isEmpty(durationStr)) {
                float duration = Float.parseFloat(durationStr);
                media.duration = (long) duration;
            }
            JSONArray mediaStreams = mediaJson.getJSONArray("streams");
            for (int i = 0; i < mediaStreams.length(); i++) {
                JSONObject stream = mediaStreams.optJSONObject(i);
                String codecType = stream.optString("codec_type");
                if (TextUtils.equals(TYPE_VIDEO, codecType)) {
                    VideoBean video = new VideoBean();
                    video.videoCodec = stream.optString("codec_tag_string");
                    video.width = stream.optInt("width");
                    video.height = stream.optInt("height");
                    video.displayAspectRatio = stream.optString("display_aspect_ratio");
                    video.pixelFormat = stream.optString("pix_fmt");
                    video.profile = stream.optString("profile");
                    video.level = stream.optInt("level");
                    String frameRateStr = stream.optString("r_frame_rate");
                    if (!TextUtils.isEmpty(frameRateStr)) {
                        String[] frameRateArray = frameRateStr.split("/");
                        double frameRate = Math.ceil(Double.parseDouble(frameRateArray[0]) / Double.parseDouble(frameRateArray[1]));
                        video.frameRate = (int) frameRate;
                    }
                    media.videoBean = video;
                } else if (TextUtils.equals(TYPE_AUDIO, codecType)) {
                    AudioBean audio = new AudioBean();
                    audio.audioCodec = stream.optString("codec_tag_string");
                    String sampleRateStr = stream.optString("sample_rate");
                    if (!TextUtils.isEmpty(sampleRateStr)) {
                        audio.sampleRate = Integer.parseInt(sampleRateStr);
                    }
                    audio.channels = stream.optInt("channels");
                    audio.channelLayout = stream.optString("channel_layout");
                    JSONObject audioTag = format.getJSONObject("tags");
                    parseTag(audioTag, audio);
                    media.audioBean = audio;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }

    private static void parseTag(JSONObject audioTag, AudioBean audio) {
        audio.title = audioTag.optString("title");
        audio.artist = audioTag.optString("artist");
        audio.album = audioTag.optString("album");
        audio.albumArtist = audioTag.optString("album_artist");
        audio.composer = audioTag.optString("composer");
        audio.genre = audioTag.optString("genre");
        String lyrics = audioTag.optString("lyrics-eng");
        if (lyrics.contains("\r\n")) {
            String[] array = lyrics.split("\r\n");
            audio.lyrics = Arrays.asList(array);
        }
    }
}
