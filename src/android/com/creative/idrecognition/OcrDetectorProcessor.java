
package com.creative.idrecognition;

import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.RelativeLayout;

import com.creative.idrecognition.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;


public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
    OcrCaptureActivity mainActivity;


    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    OcrDetectorProcessor(AppCompatActivity activity, GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
        mainActivity = (OcrCaptureActivity)activity;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();

        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);

            if(detectId(item.getValue().toString())){
                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                mGraphicOverlay.add(graphic);
                mainActivity.detectionSuccess(true,item);
            }

        }
    }

    @Override
    public void release() {
        mGraphicOverlay.clear();
    }

    public Boolean detectId(String str) {
        String pre = str.replaceAll("[^-]","");
        String numOnly = str.replaceAll("[^0-9]", "");
        if (numOnly.length() == 11 && pre.length() == 2) {
            return true;
        } else {
            return false;
        }
    }

}
