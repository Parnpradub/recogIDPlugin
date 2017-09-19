package com.creative.idrecognition.ui.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.util.AttributeSet;

import com.creative.idrecognition.OcrCaptureActivity;

/**
 * Created by K on 9/6/2017.
 */




public class FocusBox extends View {

    private OcrCaptureActivity ocrCap;
    private Paint mPaint;
    private Paint nPaint;

    public FocusBox(Context context) {
        this(context, null, 0);
    }

    public FocusBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.BEVEL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);

        nPaint = new Paint();
        nPaint.setColor(Color.GREEN);
        nPaint.setStyle(Paint.Style.STROKE);
        nPaint.setStrokeJoin(Paint.Join.BEVEL);
        nPaint.setStrokeCap(Paint.Cap.ROUND);
        nPaint.setStrokeWidth(6);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();

        float leftX = width/4f;
        float upY = height/5*2f;

        float rightX = width/4*3f;
        float downY= height/5*3f;

        float offXout = (rightX - leftX)/5;
        float offYout = (upY - downY)/4;

        canvas.drawLine(leftX,upY,leftX + offXout,upY,mPaint);
        canvas.drawLine(leftX,upY,leftX,upY - offYout,mPaint);

        canvas.drawLine(leftX,downY,leftX + offXout,downY,mPaint);
        canvas.drawLine(leftX,downY,leftX,downY + offYout,mPaint);

        canvas.drawLine(rightX,upY,rightX - offXout,upY,mPaint);
        canvas.drawLine(rightX,upY,rightX,upY - offYout,mPaint);

        canvas.drawLine(rightX,downY,rightX - offXout,downY,mPaint);
        canvas.drawLine(rightX,downY,rightX,downY + offYout,mPaint);

        float inleftX = width/3f;
        float inupY = height/15*7f;

        float inrightX = width/3*2f;
        float indownY = height/15*8f;

        float inoffXout = (inrightX - inleftX)/6;
        float inoffYout = (inupY - indownY)/5;

        canvas.drawLine(inleftX,inupY,inleftX + inoffXout,inupY,nPaint);
        canvas.drawLine(inleftX,inupY,inleftX,inupY - inoffYout,nPaint);

        canvas.drawLine(inleftX,indownY,inleftX + inoffXout,indownY,nPaint);
        canvas.drawLine(inleftX,indownY,inleftX,indownY + inoffYout,nPaint);

        canvas.drawLine(inrightX,inupY,inrightX - inoffXout,inupY,nPaint);
        canvas.drawLine(inrightX,inupY,inrightX,inupY - inoffYout,nPaint);

        canvas.drawLine(inrightX,indownY,inrightX - inoffXout,indownY,nPaint);
        canvas.drawLine(inrightX,indownY,inrightX,indownY + inoffYout,nPaint);

        super.onDraw(canvas);
    }

}
