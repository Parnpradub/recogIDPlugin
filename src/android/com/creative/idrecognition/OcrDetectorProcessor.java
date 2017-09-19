
package com.creative.idrecognition;

import android.app.Activity;
import android.util.SparseArray;

import com.creative.idrecognition.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;


public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
    OcrCaptureActivity mainActivity;


    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    OcrDetectorProcessor(Activity activity, GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
        mainActivity = (OcrCaptureActivity)activity;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();

        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);

            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
            mainActivity.detectionSuccess(graphic.recogFlag,item);
        }
    }

    @Override
    public void release() {
        mGraphicOverlay.clear();
    }


}
