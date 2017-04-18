package com.photoview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mabo on 2016/10/18.
 */

public class EasyView extends View {
    private Paint mPaintNormal,mPaintStroke;
    private float mRadius=10f;//半径
    private int mCurrentSelected=0,mMaxCircle=4,mWidth,mHeight;
    private float mGapWidth;

    private Timer mTimer=new Timer();
    private TimerTask mTimerTask;
    public EasyView(Context context) {
        super(context);
    }

    public EasyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaintNormal=new Paint();
        mPaintNormal.setStyle(Paint.Style.FILL);
        mPaintNormal.setColor(Color.rgb(112,158,25));
        mPaintNormal.setAntiAlias(true);

        mPaintStroke=new Paint();
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(Color.rgb(115,160,30));
        mPaintStroke.setAntiAlias(true);
        mPaintStroke.setStrokeWidth(2f);

        mTimerTask=new TimerTask() {
            @Override
            public void run() {
                changeState();
            }
        };
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth=MeasureSpec.getSize(widthMeasureSpec);
        mHeight=MeasureSpec.getSize(heightMeasureSpec);
        mGapWidth=(mWidth-(mRadius*mMaxCircle))/(mMaxCircle+1);

        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.save();
        for(int i=0;i<mMaxCircle;i++){
            canvas.translate(mRadius+mGapWidth,0);
            if(i==mCurrentSelected){
                canvas.drawCircle(0,mHeight/2,mRadius,mPaintStroke);
            }else {
                canvas.drawCircle(0,mHeight/2,mRadius,mPaintNormal);
            }
        }

    }

    public synchronized void changeState(){
        if(mCurrentSelected==3){
            mCurrentSelected=0;
        }else {
            mCurrentSelected++;
        }
        postInvalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTimer.schedule(mTimerTask,0,500);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimerTask.cancel();
        mTimer.cancel();
    }
}
