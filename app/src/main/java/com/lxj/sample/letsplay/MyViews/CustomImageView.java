package com.lxj.sample.letsplay.MyViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.lxj.sample.letsplay.R;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class CustomImageView extends ImageView{

    private int type;
    private static final int TYPE_CIRCLR = 0;
    private static final int TYPE_ROUND = 1;

    //图片
    private Bitmap mSrc;
    //圆角的大小
    private int mRadius;
    //view的宽和高
    private int mWidth;
    private int mHeight;



    public CustomImageView(Context context) {
        this(context,null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    //初始化自定义参数

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView,defStyleAttr,0);

        int n = a.getIndexCount();
        for(int i = 0;i < n;i++){
            int attr = a.getIndex(i);
            switch (attr){

                case (R.styleable.CustomImageView_src):
                    mSrc = BitmapFactory.decodeResource(getResources(),a.getResourceId(attr,0));
                    break;
                case (R.styleable.CustomImageView_type):
                    type = a.getInt(attr,0);
                    break;
                case (R.styleable.CustomImageView_borderRadius):
                    mRadius = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10f,getResources().getDisplayMetrics()));
                    break;

            }
        }
        a.recycle();
    }

    //计算控件的高度和宽度


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            mWidth =specSize;
        }else {
            //由图片决定的宽
            int desireByImage = getPaddingLeft() + getPaddingRight() + mSrc.getWidth();
            if(specMode == MeasureSpec.AT_MOST){
                mWidth = Math.min(desireByImage,specSize);
            }else
                mWidth = desireByImage;
        }

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            mHeight = specSize;
        }else {
            int desire = getPaddingTop() + getPaddingBottom() + mSrc.getHeight();
            if (specMode == MeasureSpec.AT_MOST){
                mHeight = Math.min(desire,specSize);
            }else
                mHeight = desire;
        }
        setMeasuredDimension(mWidth,mHeight);

    }

    //绘制

    @Override
    protected void onDraw(Canvas canvas) {
        switch (type){
            case TYPE_CIRCLR:
                int min = Math.min(mWidth,mHeight);
                mSrc = Bitmap.createScaledBitmap(mSrc,min,min,false);
                canvas.drawBitmap(createCircleImage(mSrc,min),0,0,null);
                break;
            case TYPE_ROUND:
                createRoundConerImage(mSrc);
                break;
        }
    }

    //根据原图和变长绘制圆形图片
    private Bitmap createCircleImage(Bitmap source,int min){

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min,min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(min/2,min/2,min/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source,0,0,paint);
        return target;
    }
    //根据原图添加圆角
    private Bitmap createRoundConerImage(Bitmap source){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        RectF rect = new RectF(0,0,source.getWidth(),source.getHeight());
        canvas.drawRoundRect(rect,mRadius,mRadius,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source,0,0,paint);
        return target;
    }
}
