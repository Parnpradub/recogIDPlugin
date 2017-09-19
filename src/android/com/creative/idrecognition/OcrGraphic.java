
package com.creative.idrecognition;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.creative.idrecognition.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private int mId;

    private static final int TEXT_COLOR = Color.BLUE;

    private static Paint sRectPaint;
 //   private static Paint sTextPaint;
    private final TextBlock mText;
    public final boolean recogFlag;

    OcrGraphic(GraphicOverlay overlay,TextBlock text) {
        super(overlay);

        if(detectId(text)){
            mText = text;
            recogFlag = true;
        }
        else {
            mText = null;
            recogFlag = false;
        }
        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        postInvalidate();
    }

    public Boolean detectId(TextBlock txt) {
        String str = getLines(txt);
        String pre = str.replaceAll("[^-]","");
        String numOnly = str.replaceAll("[^0-9]", "");
        if (numOnly.length() == 11 && pre.length() == 2) {
            return true;
        } else {
            return false;
        }
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public TextBlock getTextBlock() {
        return mText;
    }

    public boolean contains(float x, float y) {
        TextBlock text = mText;
        if (text == null) {
            return false;
        }
        RectF rect = new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {

        if (mText == null) {
            return;
        }
    }
    public String getLines(TextBlock block){
        List<Line> lines = (List<Line>) block.getComponents();
        String str = "";
        for(Line elements : lines){
            str = elements.getValue().toString();
        }
        return str;
    }
    public Line getLineBlock(TextBlock parent){
        List<Line> lines = (List<Line>) parent.getComponents();
        Line child = null;
        for(Line elements : lines){
            child = elements;
        }
        return child;
    }
}
