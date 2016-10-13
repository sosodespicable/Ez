package com.lxj.sample.letsplay;

import android.app.Application;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by fez on 2016/10/7.
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    static {
        System.loadLibrary("msc");
        Log.d(TAG, "static initializer: 初始化成功");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=57f45c67");
    }
}
