package com.lxj.sample.letsplay.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.lxj.sample.letsplay.Idea.IdeaActivity;
import com.lxj.sample.letsplay.JsonParse;
import com.lxj.sample.letsplay.MyViews.NewCustomRing;
import com.lxj.sample.letsplay.R;
import com.lxj.sample.letsplay.Services.MusicService;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by fez on 2016/10/5.
 */

public class FragmentWakeUnderstand extends Fragment {

    private static final String TAG = "FragmentWakeUnderstand";
    private static final String PLAY_MUSIC = "com.soundai.play";
    private static final String STOP_MUSIC = "com.soundai.stop";
    private NewCustomRing mNewCustomRing;
    private TextView tv_wakeup_content;
    private TextView tv_musicInfo;
    private VoiceWakeuper mIvw;
    private int curThresh = 10;
    private String keep_alive = "0";
    private String ivwNetMode = "0";
    private String resultString;
    private UnderstandManager mUnderstandManager;
    private Context mContext;
    private Button btn_aboutApp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wakep,container,false);
        mContext = this.getActivity();
        mUnderstandManager = new UnderstandManager(mContext) {
            @Override
            public void updateTextView() {
                tv_wakeup_content.setText(getText());
            }

            @Override
            public void cleanTextView() {
                tv_wakeup_content.setText("");
            }

            @Override
            public void makeToast() {

            }

            @Override
            public void playMusic() {
                tv_musicInfo.setText(mUnderstandManager.getSinger() + " - " + mUnderstandManager.getName());
                String downloadUrl = mUnderstandManager.getDownloadUrl();
                Log.d(TAG, "playMusic: 音乐链接为:" + downloadUrl);
                Intent startIntent = new Intent();
                startIntent.putExtra("downloadUrl",downloadUrl);
                startIntent.setAction(PLAY_MUSIC);
                mContext.sendBroadcast(startIntent);
            }

            @Override
            public void loopWakeup() {
                startWakeup();
            }

            @Override
            public void stopMusic() {
                tv_musicInfo.setText("跟我说：" + "播放XXX的歌" + "或者我想听XXX");
                Intent stopIntent = new Intent();
                stopIntent.setAction(STOP_MUSIC);
                mContext.sendBroadcast(stopIntent);
            }
        };
        initView(view,mContext);
        mIvw = VoiceWakeuper.createWakeuper(mContext, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.d(TAG, "onInit: init_code=" + i);
            }
        });
        Intent serviceIntent = new Intent(mContext, MusicService.class);
        mContext.startService(serviceIntent);
        startWakeup();
        return view;
    }

    private void initView(View view, final Context context){
        tv_wakeup_content = (TextView) view.findViewById(R.id.tv_wakeup_content);
        tv_musicInfo = (TextView) view.findViewById(R.id.tv_musicInfo);
        mNewCustomRing = (NewCustomRing) view.findViewById(R.id.custom_angle);
        btn_aboutApp = (Button) view.findViewById(R.id.btn_aboutApp);
        btn_aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IdeaActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void startWakeup(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null){
            resultString = "";
            mIvw.setParameter(SpeechConstant.PARAMS,null);
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD,"0:" + curThresh);
            mIvw.setParameter(SpeechConstant.IVW_SST,"wakeup");
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE,keep_alive);
            mIvw.setParameter(SpeechConstant.IVW_NET_MODE,ivwNetMode);
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH,getResource());
            mIvw.startListening(mWakeuperListener);
        }
    }
    private WakeuperListener mWakeuperListener = new WakeuperListener() {
        @Override
        public void onBeginOfSpeech() {
            Log.d(TAG, "mWakeuperListener: 开始说话");
        }
        @Override
        public void onResult(WakeuperResult wakeuperResult) {
            Log.d(TAG, "mWakeuperListener: 返回结果");
            try {
                String text = wakeuperResult.getResultString();
                JSONObject mJSONObject;
                mJSONObject = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【Raw】" + text);
                buffer.append("\n");
                resultString = buffer.toString();
                Log.d(TAG, "mWakeuperListener: 解析结果为:" + resultString);
            } catch (JSONException e) {
                Log.d(TAG, "mWakeuperListener: 解析结果出错");
                e.printStackTrace();
            } finally {
                if (mIvw.isListening()){
                    Log.d(TAG, "mWakeuperListener: 会话未关闭");
                    mIvw.stopListening();
                    Log.d(TAG, "mWakeuperListener: 会话手动关闭");
                } else {
                    Log.d(TAG, "mWakeuperListener: 会话关闭");
                }
                mUnderstandManager.startSpeechUnderstand();
            }
        }
        @Override
        public void onError(SpeechError speechError) {
            Log.d(TAG, "mWakeuperListener:" + speechError.getErrorDescription() + "\n" + speechError.getErrorCode());
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            Log.d(TAG, "mWakeuperListener: onEvent!");
        }
        @Override
        public void onVolumeChanged(int i) {
            Log.d(TAG, "mWakeuperListener: 音量变化中");
        }
    };

    private String getResource(){
        return ResourceUtil.generateResourcePath(mContext,
                ResourceUtil.RESOURCE_TYPE.assets,"ivw/" + "57f45c67.jet");
    }
}
