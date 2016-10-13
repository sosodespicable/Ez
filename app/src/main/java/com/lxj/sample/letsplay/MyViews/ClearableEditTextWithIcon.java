package com.lxj.sample.letsplay.MyViews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.lxj.sample.letsplay.R;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
public class ClearableEditTextWithIcon extends EditText implements View.OnTouchListener,TextWatcher{

    Drawable deleteImage = getResources().getDrawable(R.drawable.nim_icon_edit_delete);
    Drawable icon;


    public ClearableEditTextWithIcon(Context context) {
        super(context);
        init();
    }

    public ClearableEditTextWithIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableEditTextWithIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        ClearableEditTextWithIcon.this.setOnTouchListener(this);
        ClearableEditTextWithIcon.this.addTextChangedListener(this);
        deleteImage.setBounds(0,0,deleteImage.getIntrinsicWidth(),deleteImage.getIntrinsicHeight());
        manageClearButton();
    }

    public void setIconResource(int id){
        icon = getResources().getDrawable(id);
        icon.setBounds(0,0,icon.getIntrinsicWidth(),icon.getIntrinsicHeight());
        manageClearButton();
    }

    public void setDeleteImage(int id) {
        deleteImage = getResources().getDrawable(id);
        deleteImage.setBounds(0,0,deleteImage.getIntrinsicWidth(),deleteImage.getIntrinsicHeight());
        manageClearButton();
    }

    void manageClearButton(){
        if (this.getText().toString().equals("")){
            removeClearButton();
        }
        else {
            addClearButton();
        }
    }

    void removeClearButton(){
        this.setCompoundDrawables(this.icon,this.getCompoundDrawables()[1],null,this.getCompoundDrawables()[3]);
    }

    void addClearButton(){
        this.setCompoundDrawables(this.icon,this.getCompoundDrawables()[1],deleteImage,this.getCompoundDrawables()[3]);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ClearableEditTextWithIcon et = ClearableEditTextWithIcon.this;

        if (et.getCompoundDrawables()[2] == null){
            return false;
        }
        if (event.getAction() != MotionEvent.ACTION_UP){
            return false;
        }
        if (event.getX() > et.getWidth() - et.getPaddingRight() - deleteImage.getIntrinsicWidth()){
            et.setText("");
            ClearableEditTextWithIcon.this.removeClearButton();
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        ClearableEditTextWithIcon.this.manageClearButton();
    }
}
