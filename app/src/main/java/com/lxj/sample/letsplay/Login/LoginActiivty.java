package com.lxj.sample.letsplay.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lxj.sample.letsplay.MyViews.ClearableEditTextWithIcon;
import com.lxj.sample.letsplay.R;

/**
 * Created by Administrator on 2016/7/29 0029.
 */
public class LoginActiivty extends AppCompatActivity implements View.OnKeyListener {
    private Toolbar toolbar;

    private TextView rightTopButton;
    private TextView switchModeButton;
    private TextView doneButton;

    private ClearableEditTextWithIcon loginAccountEdit;
    private ClearableEditTextWithIcon loginPasswordEdit;

    private ClearableEditTextWithIcon registerAccountEdit;
    private ClearableEditTextWithIcon registerNickNameEdit;
    private ClearableEditTextWithIcon registerPasswordEdit;

    private View loginLayout;
    private View registerLayout;

    private boolean registerMode = false;
    private boolean registerPanelInited = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        doneButton = (TextView) findViewById(R.id.register_login_done);
        initLoginPanel();
        initReisterPanel();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    private void initLoginPanel(){
        loginAccountEdit = (ClearableEditTextWithIcon) findViewById(R.id.editview_login_account);
        loginPasswordEdit = (ClearableEditTextWithIcon) findViewById(R.id.editview_login_password);

        loginAccountEdit.setIconResource(R.drawable.user_account_icon);
        loginPasswordEdit.setIconResource(R.drawable.user_pwd_lock_icon);

        loginAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        loginPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});

//        loginAccountEdit.addTextChangedListener(textWatcher);
//        loginPasswordEdit.addTextChangedListener(textWatcher);

        loginPasswordEdit.setOnKeyListener(this);

    }

    private void initReisterPanel(){

        loginLayout = findViewById(R.id.login_layout);
        registerLayout =findViewById(R.id.register_layout);
        switchModeButton = (TextView) findViewById(R.id.register_login_tip);
        switchModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode();
            }
        });

    }

    private void switchMode(){
        registerMode = !registerMode;
        if (registerMode && !registerPanelInited){
            registerAccountEdit = (ClearableEditTextWithIcon) findViewById(R.id.editview_register_account);
            registerNickNameEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_register_nickname);
            registerPasswordEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_register_password);

            registerAccountEdit.setIconResource(R.drawable.user_account_icon);
            registerNickNameEdit.setIconResource(R.drawable.nick_name_icon);
            registerPasswordEdit.setIconResource(R.drawable.user_pwd_lock_icon);

            registerAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            registerNickNameEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            registerPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

//            registerAccountEdit.addTextChangedListener(textWatcher);
//            registerNickNameEdit.addTextChangedListener(textWatcher);
//            registerPasswordEdit.addTextChangedListener(textWatcher);

            registerPanelInited = true;
        }

        loginLayout.setVisibility(registerMode ? View.GONE:View.VISIBLE);
        registerLayout.setVisibility(registerMode ? View.VISIBLE:View.GONE);
        switchModeButton.setText(registerMode ? "已有帐号？直接登录":"注册");
        if (registerMode){
            doneButton.setEnabled(true);
        }else{
            boolean isEnable = loginAccountEdit.getText().length() > 0 && loginPasswordEdit.getText().length() > 0;
            doneButton.setEnabled(isEnable);
        }

    }
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!registerMode){
                //登陆模式
                boolean isEnable = loginAccountEdit.getText().length() > 0 && loginPasswordEdit.getText().length() > 0;
                updateDoneButton(isEnable);
            }

        }
    };

    private void updateDoneButton(boolean isEnable){
        doneButton.setText("完成");
        doneButton.setEnabled(isEnable);
    }

}
