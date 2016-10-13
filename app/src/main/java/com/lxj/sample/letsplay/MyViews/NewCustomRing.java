package com.lxj.sample.letsplay.MyViews;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.lxj.sample.letsplay.R;

import java.util.Date;

/**
 * Created by fez on 2016/9/13.
 */
public class NewCustomRing extends View {

    //圆环的信用等级文本
    String[] sesameStr = new String[]{
            "0","45",
            "90","135",
            "180","225",
            "270","315",
            "360"
    };

    //默认宽高值
    private int defaultSize;

    //距离圆环的值
    private int arcDistentce;

    //view宽度
    private int width;

    //view高度
    private int height;

    //默认padding值
    private final static int defaultPadding = 20;

    //圆环起始角度
    private final static float mStartAngle = 180f;

    //圆环结束角度
    private final static float mEndAngle = 540f;

    //外层圆环画笔
    private Paint mMiddleArcPaint;

    //内层圆环画笔
    private Paint mInnerArcPaint;

    //信用等级文本画笔
    private Paint mTextPaint;

    //大刻度画笔
    private Paint mCalibrationPaint;

    //小刻度画笔
    private Paint mSmallCalibrationPaint;

    //小刻度画笔
    private Paint mCalibrationTextPaint;

    //进度圆环画笔
    private Paint mArcProgressPaint;

    //半径
    private int radius;

    //外层矩形
    private RectF mMiddleRect;

    //内层矩形
    private RectF mInnerRect;

    //进度矩形
    private RectF mMiddleProgressRect;

    //最小数字
    private float mMinNum = 0;

    //最大数字
    private float mMaxNum = 360;

    //总进度
    private float mTotalAngle = 210f;

    //当前进度
    private float mCurrentAngle = 0;

    //信用等级
    private String sesameLevel = "";

    //评估时间
    private String evaluationTime = "";

    //小圆点
    private Bitmap bitmap;

    //当前点的实际位置
    private float[] pos;

    //当前点的tan值
    private float[] tan;

    //矩阵
    private Matrix mMatrix;

    //小圆点画笔
    private Paint mBitmapPaint;

    public NewCustomRing(Context context) {
        this(context,null);
    }

    public NewCustomRing(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public NewCustomRing(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        defaultSize = dp2px(250);
        arcDistentce = dp2px(14);

        //外层圆环画笔
        mMiddleArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddleArcPaint.setStrokeWidth(8);
        mMiddleArcPaint.setColor(Color.WHITE);
        mMiddleArcPaint.setStyle(Paint.Style.STROKE);
        mMiddleArcPaint.setAlpha(80);

        //内层圆环画笔
        mInnerArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerArcPaint.setStrokeWidth(30);
        mInnerArcPaint.setColor(Color.WHITE);
        mInnerArcPaint.setAlpha(80);
        mInnerArcPaint.setStyle(Paint.Style.STROKE);

        //正中间字体画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        //圆环大刻度画笔
        mCalibrationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCalibrationPaint.setStrokeWidth(4);
        mCalibrationPaint.setColor(Color.WHITE);
        mCalibrationPaint.setAlpha(120);
        mCalibrationPaint.setStyle(Paint.Style.STROKE);

        //圆环小刻度画笔
        mSmallCalibrationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallCalibrationPaint.setColor(Color.WHITE);
        mSmallCalibrationPaint.setStyle(Paint.Style.STROKE);
        mSmallCalibrationPaint.setStrokeWidth(1);
        mSmallCalibrationPaint.setAlpha(130);

        //圆环刻度文本画笔
        mCalibrationTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCalibrationTextPaint.setTextSize(30);
        mCalibrationTextPaint.setColor(Color.WHITE);

        //外层进度画笔
        mArcProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcProgressPaint.setStrokeWidth(8);
        mArcProgressPaint.setColor(Color.WHITE);
        mArcProgressPaint.setStyle(Paint.Style.STROKE);
        mArcProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        //小圆点画笔
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);

        //初始化小圆点图片
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_circle);
        pos = new float[2];
        tan = new float[2];
        mMatrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveMeasure(widthMeasureSpec,defaultSize),resolveMeasure(heightMeasureSpec,defaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = width/2;

        mMiddleRect = new RectF(defaultPadding,defaultPadding,width - defaultPadding,
                height - defaultPadding);
        mInnerRect = new RectF(defaultPadding + arcDistentce,defaultPadding + arcDistentce,
                width - defaultPadding - arcDistentce,height - defaultPadding - arcDistentce);
        mMiddleProgressRect = new RectF(defaultPadding,defaultPadding,width - defaultPadding,
                height - defaultPadding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMiddleArc(canvas);
        drawInnerArc(canvas);
        drawSmallCalibration(canvas);
        drawCalibrationAndText(canvas);
        drawCenterText(canvas);
        drawRingProgresss(canvas);
    }

    /*
    *
    * 绘制外层圆环
    *
    * */
    private void drawMiddleArc(Canvas canvas){
        canvas.drawArc(mMiddleRect,mStartAngle,mEndAngle,false,mMiddleArcPaint);
    }

    /*
    * 绘制内层圆环
    * */
    private void drawInnerArc(Canvas canvas){
        canvas.drawArc(mInnerRect,mStartAngle,mEndAngle,false,mInnerArcPaint);
    }

    /*
    * 绘制内层小刻度
    * */
    private void drawSmallCalibration(Canvas canvas){

        canvas.save();
        canvas.rotate(-270,radius,radius);
        //计算刻度线的开始电和结束点
        int startDst = (int) (defaultPadding + arcDistentce - mInnerArcPaint.getStrokeWidth()/2 - 1);
        int endDst = (int) (startDst + mInnerArcPaint.getStrokeWidth());
        for (int i = 0;i <= 80;i++){
            //每旋转6度画一条线
            canvas.drawLine(radius,startDst,radius,endDst,mSmallCalibrationPaint);
            canvas.rotate(4.5f,radius,radius);
        }
        canvas.restore();
    }

    //绘制刻度
    private void drawCalibrationAndText(Canvas canvas){
        canvas.save();
        canvas.rotate(-90 ,radius,radius);
        int startDst = (int) (defaultPadding + arcDistentce - mInnerArcPaint.getStrokeWidth()/2 -1);
        int endDst = (int) (startDst + mInnerArcPaint.getStrokeWidth());
        int rotateAngle = 360/8;
        for (int i =1;i < 9;i++){
//            if (i % 2 != 0){
//                canvas.drawLine(radius,startDst,radius,endDst,mCalibrationPaint);
//            }
            canvas.drawLine(radius,startDst,radius,endDst,mCalibrationPaint);
            float textLen = mCalibrationTextPaint.measureText(sesameStr[i -1]);
            canvas.drawText(sesameStr[i-1],radius - textLen/2,endDst + 40,mCalibrationTextPaint);
            canvas.rotate(rotateAngle,radius,radius);
        }
        canvas.restore();
    }

    //绘制中间文本
    private void drawCenterText(Canvas canvas){
//        mTextPaint.setTextSize(30);
//        canvas.drawText("Soundai",radius,radius - 130,mTextPaint);
        //绘制信用分数
        mTextPaint.setTextSize(100);
        mTextPaint.setStyle(Paint.Style.STROKE);
        canvas.drawText(String.valueOf(mMinNum),radius,radius + 35,mTextPaint);
        //绘制信用级别
//        mTextPaint.setTextSize(30);
////        canvas.drawText(sesameLevel,radius,radius + 160,mTextPaint);
//        canvas.drawText(evaluationTime,radius,radius + 205,mTextPaint);
    }

    //绘制外层圆环进度和小圆点
    private void drawRingProgresss(Canvas canvas){
        Path path = new Path();
        path.addArc(mMiddleProgressRect,mStartAngle,mCurrentAngle);
        PathMeasure pathMeasure = new PathMeasure(path,false);
        pathMeasure.getPosTan(pathMeasure.getLength() * 1,pos,tan);
        mMatrix.reset();
        mMatrix.postTranslate(pos[0] - bitmap.getWidth()/2,pos[1] - bitmap.getHeight()/2);
//        canvas.drawPath(path,mArcProgressPaint);
        //起始角度不为0时绘制小圆点
        if (mCurrentAngle == 0){
            return;
        }
        canvas.drawBitmap(bitmap,mMatrix,mBitmapPaint);
        mBitmapPaint.setColor(Color.WHITE);
//        canvas.drawLine(pos[0],pos[1],pos[0] - arcDistentce,pos[1] + arcDistentce,mBitmapPaint);
        canvas.drawCircle(pos[0],pos[1],15,mBitmapPaint);
    }

    private int resolveMeasure(int measureSpec, int defaultSize){
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)){
            case MeasureSpec.EXACTLY:

                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize,defaultSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            default:
                result = defaultSize;
                break;
        }
        return result;
    }

    public void setSesameValues(float values){
        mMaxNum = values;
        mTotalAngle = values;
        evaluationTime = "唤醒时间:" + getCurrentTime();
//        if (values <= 350){
//            mMaxNum = values;
//            mTotalAngle = 0f;
////            sesameLevel = "信用较差";
//            evaluationTime = "唤醒时间:" + getCurrentTime();
//        }else if (values <= 550){
//            mMaxNum = values;
//            mTotalAngle = (values - 350) * 80 / 400f + 2;
////            sesameLevel = "信用较差";
//            evaluationTime = "唤醒时间:" + getCurrentTime();
//        }else if (values <= 700){
//            mMaxNum = values;
//            if (values > 550 && values <= 600){
////                sesameLevel = "信用中等";
//            } else if (values > 600 && values <= 650){
////                sesameLevel = "信用良好";
//            } else{
////                sesameLevel = "信用优秀";
//            }
//            mTotalAngle = (values - 550) * 120 /150f + 43;
//            evaluationTime = "唤醒时间:" + getCurrentTime();
//        }else  if (values <= 950){
//            mMaxNum = values;
//            mTotalAngle = (values - 700) * 40 / 250f + 170;
////            sesameLevel = "信用极好";
//            evaluationTime = "唤醒时间:" + getCurrentTime();
//        }else {
//            mTotalAngle = 240f;
//        }
        startAnim();
    }

    public void startAnim(){
        ValueAnimator mAngleAnim = ValueAnimator.ofFloat(mCurrentAngle,mTotalAngle);
        mAngleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mAngleAnim.setDuration(1000);
        mAngleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentAngle = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        mAngleAnim.start();

        ValueAnimator mNumAnim = ValueAnimator.ofFloat(mMinNum,mMaxNum);
        mNumAnim.setDuration(1000);
        mNumAnim.setInterpolator(new LinearInterpolator());
        mNumAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mMinNum = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        mNumAnim.start();
    }

    public String getCurrentTime()
    {
        @SuppressLint("SimpleDateFormat")
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("hh:mm:ss");
        return format.format(new Date());
    }

    private int dp2px(int values){
        float density = getResources().getDisplayMetrics().density;
        return (int) (values*density + 0.5f);
    }

}
