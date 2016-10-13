package com.lxj.sample.letsplay.Main;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.lxj.sample.letsplay.JsonParse;
import com.lxj.sample.letsplay.bean.MusicInfo;

import java.util.List;

/**
 * Created by fez on 2016/10/7.
 */

public abstract class UnderstandManager {
    private static final String TAG = "Understandmanager";
    private Context mContext;
    private SpeechUnderstander mSpeechUnderstander;
    private String result;
    private String text;

    public abstract void updateTextView();
    public abstract void cleanTextView();
    public abstract void makeToast();
    public abstract void playMusic();
    public abstract void loopWakeup();
    public abstract void stopMusic();

    private String singer;
    private String sourceName;
    private String name;
    private String downloadUrl;

    private MusicInfo mMusicInfo;
    private List<MusicInfo> musicInfoList;

    public UnderstandManager(Context context){
        this.mContext = context;
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(context, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.d(TAG, "onInit: 初始化code=" + i);
                if (i != ErrorCode.SUCCESS){
                    Log.d(TAG, "onInit: 初始化失败");
                }
            }
        });
    }

    private void setParam(){
        //设置语言
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
        //前端点
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS,"4000");
        //后端点
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS,"700");
        //标点符号
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,"1");
        //音频保存路径和格式
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        Log.d(TAG, "setParam: " + Environment.getExternalStorageDirectory());
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/sud.wav");
    }

    int ret = 0;
    public void startSpeechUnderstand(){
        cleanTextView();
        Log.d(TAG, "startSpeechUnderstand: 清除成功");
        setParam();
        if (mSpeechUnderstander.isUnderstanding()){
            mSpeechUnderstander.stopUnderstanding();
            Log.d(TAG, "startSpeechUnderstand: 检测到正在录音并停止录音");
        } else {
            ret = mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
            if (ret != 0){
                Log.d(TAG, "startSpeechUnderstand: 语音语义理解失败");
            } else {
                Log.d(TAG, "startSpeechUnderstand: 语音语义理解成功");
            }
        }
    }
    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            Log.d(TAG, "mSpeechUnderstanderListener: 音量变化中");
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d(TAG, "mSpeechUnderstanderListener: 开始说话");
            stopMusic();
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "mSpeechUnderstanderListener: 结束说话");
        }

        @Override
        public void onResult(UnderstanderResult understanderResult) {
            Log.d(TAG, "mSpeechUnderstanderListener: 返回结果");
            result = understanderResult.getResultString();
            Log.d(TAG, "onResult: 语音理解的返回结果为：" + result);
            if (mSpeechUnderstander.isUnderstanding()){
                Log.d(TAG, "onResult: 还在识别");
                mSpeechUnderstander.stopUnderstanding();
                Log.d(TAG, "onResult: 手动关闭识别");
            } else{
                Log.d(TAG, "onResult: 停止识别");
            }
            text = JsonParse.getResultText(result);
            if (text.indexOf("停止") != -1){
                Log.d(TAG, "onResult: 字符串中包含停止");
                stopMusic();
            } else {
                musicInfoList = JsonParse.getMusicList(result);
                updateTextView();
                if (musicInfoList != null && !musicInfoList.isEmpty()){
                    mMusicInfo = musicInfoList.get(0);
                    singer = mMusicInfo.getSinger();
                    sourceName = mMusicInfo.getSourceName();
                    name = mMusicInfo.getName();
                    downloadUrl = mMusicInfo.getDownloadUrl();
                    Log.d(TAG, "singer:" + singer + "\nsourceName:" + sourceName + "\nname:" + name + "\ndownloadUrl:" + downloadUrl);
                    playMusic();
                } else {
                    Log.d(TAG, "onResult: 哎呀，这首歌曲库还没有呢");
                }
            }
            loopWakeup();
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.d(TAG, "mSpeechUnderstanderListener: " + speechError.getErrorDescription() + "\n" + speechError.getErrorCode());
            result = speechError.getErrorDescription();
            updateTextView();
            makeToast();
            loopWakeup();
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public void  stopSpeechUnderstand(){
        mSpeechUnderstander.stopUnderstanding();
        Log.d(TAG, "stopSpeechUnderstand: 停止语义理解");
    }

    public void cancelSpeechUnderstand(){
        mSpeechUnderstander.cancel();
        Log.d(TAG, "cancelSpeechUnderstand: 取消语义理解");
    }

    public String getResult(){
        return result;
    }

    public String getDownloadUrl(){
        return downloadUrl;
    }

    public String getSinger(){
        return singer;
    }

    public String getName(){
        return name;
    }

    public String getText(){
        return text;
    }
}
