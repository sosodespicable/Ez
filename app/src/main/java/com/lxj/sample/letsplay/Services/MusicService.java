package com.lxj.sample.letsplay.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by fez on 2016/10/7.
 */

public class MusicService extends Service {

    private static final String TAG = "MusicService";
    private static final String PLAY_MUSIC = "com.soundai.play";
    private static final String STOP_MUSIC = "com.soundai.stop";

    private MediaPlayer mMediaPlayer;
    private String downloadUrl;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PLAY_MUSIC)){
                downloadUrl = intent.getStringExtra("downloadUrl");
                try {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(downloadUrl);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (intent.getAction().equals(STOP_MUSIC)){
                mMediaPlayer.stop();

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 初始化音乐服务");
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(PLAY_MUSIC);
        mIntentFilter.addAction(STOP_MUSIC);
        registerReceiver(mBroadcastReceiver,mIntentFilter);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.stop();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
