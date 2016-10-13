package com.lxj.sample.letsplay;

import android.util.Log;

import com.lxj.sample.letsplay.bean.MusicInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fez on 2016/10/7.
 */

public class JsonParse {
    private static final String TAG = "JsonParse";
    public static String musicService = "music";

    public static List<MusicInfo> getMusicList(String json){
        List<MusicInfo> musicList = new ArrayList<MusicInfo>();
        String serviceTypt = "";
        try {
            JSONObject mainJson = new JSONObject(json);
            serviceTypt = mainJson.getString("service");
            Log.d(TAG, "getMusicList: 返回请求的类型为: " + serviceTypt);
            if (serviceTypt.equals(musicService)){
                JSONObject dataJson = mainJson.getJSONObject("data");
                JSONArray musicResult = dataJson.getJSONArray("result");
                int length = 0;
                length = musicResult.length();
                for (int i = 0;i < length;i++){
                    MusicInfo mMusicInfo = new MusicInfo();
                    mMusicInfo.setSinger(musicResult.getJSONObject(i).getString("singer"));
                    mMusicInfo.setSourceName(musicResult.getJSONObject(i).getString("sourceName"));
                    mMusicInfo.setName(musicResult.getJSONObject(i).getString("name"));
                    mMusicInfo.setDownloadUrl(musicResult.getJSONObject(i).getString("downloadUrl"));
                    musicList.add(mMusicInfo);
                }
            } else{
                musicList = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return musicList;
    }

    public static String getResultText(String json){
        String text = "";
        try {
            JSONObject mainJson = new JSONObject(json);
            text = mainJson.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return text;
    }
}
