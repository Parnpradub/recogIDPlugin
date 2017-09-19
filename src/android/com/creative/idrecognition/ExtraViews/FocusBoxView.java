package com.creative.idrecognition.ExtraViews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



/**
 * Created by Fadi on 5/11/2014.
 */
public class FocusBoxView extends View {

    private static final int MIN_FOCUS_BOX_WIDTH = 50;
    private static final int MIN_FOCUS_BOX_HEIGHT = 20;

    private final Paint paint;
    private final int maskColor;
    private final int frameColor;
    private Paint nPaint;

    public FocusBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();

        maskColor = resources.getColor(getResources().getIdentifier("focus_box_mask", "color", context.getPackageName()));
        frameColor = resources.getColor(getResources().getIdentifier("focus_box_frame", "color", context.getPackageName()));

        nPaint = new Paint();
        nPaint.setColor(Color.YELLOW);
        nPaint.setStyle(Paint.Style.STROKE);
        nPaint.setStrokeJoin(Paint.Join.BEVEL);
        nPaint.setStrokeCap(Paint.Cap.ROUND);
        nPaint.setStrokeWidth(8);

    }

    private Rect box;

    private static Point ScrRes;

    private  Rect getBoxRect() {

        if (box == null) {

            ScrRes = FocusBoxUtils.getScreenResolution(getContext());

            int width = ScrRes.x * 7/ 9;
            int height = ScrRes.y / 8;

            width = width == 0
                    ? MIN_FOCUS_BOX_WIDTH
                    : width < MIN_FOCUS_BOX_WIDTH ? MIN_FOCUS_BOX_WIDTH : width;

            height = height == 0
                    ? MIN_FOCUS_BOX_HEIGHT
                    : height < MIN_FOCUS_BOX_HEIGHT ? MIN_FOCUS_BOX_HEIGHT : height;

            int left = (ScrRes.x - width) / 2;
            int top = (ScrRes.y - height) / 2;

            box = new Rect(left, top, left + width, top + height);
        }

        return box;
    }

    public Rect getBox() {
        return box;
    }

    @Override
    public void onDraw(Canvas canvas) {

        Rect frame = getBoxRect();

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float inleftX = frame.left-30;
        float inupY = frame.top-30;

        float inrightX = frame.right+30;
        float indownY = frame.bottom+30;

        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        paint.setAlpha(0);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(frameColor);
        canvas.drawRect(frame.left, frame.top+40, frame.right + 1, frame.top+40 + 2, paint);
        canvas.drawRect(frame.left, frame.top+40 + 2, frame.left + 2, frame.bottom-40 - 1, paint);
        canvas.drawRect(frame.right - 1, frame.top+40, frame.right + 1, frame.bottom-40 - 1, paint);
        canvas.drawRect(frame.left, frame.bottom-40 - 1, frame.right + 1, frame.bottom-40 + 1, paint);


        float inoffXout = 50;
        float inoffYout = 50;

        canvas.drawLine(inleftX,inupY,inleftX + inoffXout,inupY,nPaint);
        canvas.drawLine(inleftX,inupY,inleftX,inupY + inoffYout,nPaint);

        canvas.drawLine(inleftX,indownY,inleftX + inoffXout,indownY,nPaint);
        canvas.drawLine(inleftX,indownY,inleftX,indownY - inoffYout,nPaint);

        canvas.drawLine(inrightX,inupY,inrightX - inoffXout,inupY,nPaint);
        canvas.drawLine(inrightX,inupY,inrightX,inupY + inoffYout,nPaint);

        canvas.drawLine(inrightX,indownY,inrightX - inoffXout,indownY,nPaint);
        canvas.drawLine(inrightX,indownY,inrightX,indownY - inoffYout,nPaint);

    }
}
