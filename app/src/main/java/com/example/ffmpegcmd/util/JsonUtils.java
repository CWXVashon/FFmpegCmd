package com.example.ffmpegcmd.util;

import android.text.TextUtils;

import com.example.ffmpegcmd.bean.MediaBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vashon on 2021/8/5.
 *  用于解析 ffprobe 返回的 json 字符串
 */
public class JsonUtils {

    private static final String TYPE_VIDEO = "video";
    private static final String TYPE_AUDIO = "audio";

    public static MediaBean parseMediaFormat(String json) {
        if (TextUtils.isEmpty(json))
            return null;
        JSONObject mediaJson;
        MediaBean media = new MediaBean();
        try {
            mediaJson = new JSONObject(json);
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
                Float duration = Float.parseFloat(durationStr);
                media.duration = duration.longValue();
            }
            JSONArray mediaStreams = mediaJson.getJSONArray("streams");
            for (int i = 0; i < mediaStreams.length(); i++) {
                JSONObject stream = mediaStreams.optJSONObject(i);
                String codecType = stream.optString("codec_type");
                if (TextUtils.equals(TYPE_VIDEO, codecType)) {
                    // TODO: 2021/8/5 待续 。。。
                } else if (TextUtils.equals(TYPE_AUDIO, codecType)) {

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }
}
