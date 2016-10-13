package com.lxj.sample.letsplay.Idea;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.lxj.sample.letsplay.R;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class IdeaActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private TextView tv_aboutApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea);
        toolbar = (Toolbar) findViewById(R.id.toolbar_idea);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tv_aboutApp = (TextView) findViewById(R.id.tv_aboutApp);
        tv_aboutApp.setText("目前已知Bug：1.软件打开后有时候内容更新结束加载动画不关闭（还在找原因-.-）;" + "\n" + "相关说明：1.App的语音交互功能目前只有3个装机量，提供给评审的还剩下两个装机量，如果发现不能唤醒的情况，请联系我：lixiaojie@soundai.com；" +
            "音箱界面的表盘用于显示识别出当前说话者的方位，需要结合定制的4+1麦克风阵列可以实现，阵列目前无法提供给评审方，可以观看附件中的视频来观看该功能的效果;" + "\n" +
                "3.由于版权问题，播放的音乐有时候会出现很奇怪的东西（目前发现有时候会有广告），虽然现在已经有了成熟的音乐库但是出于各种原因无法提供给评审方，具体效果请观看附件中的视频；"
                + "\n" + "如果还发现了什么问题，请及时反馈给我lixiaojie@soundai.com");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
