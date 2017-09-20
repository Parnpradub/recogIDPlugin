/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.creative.idrecognition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.creative.idrecognition.ExtraViews.FocusBoxView;
import com.creative.idrecognition.ui.camera.FocusBox;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.creative.idrecognition.ui.camera.CameraSource;
import com.creative.idrecognition.ui.camera.CameraSourcePreview;
import com.creative.idrecognition.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.List;

import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;


import static java.security.AccessController.getContext;


public final class OcrCaptureActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "OcrCaptureActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    public static final String TextBlockObject = "recognized_id_string";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
   // private FocusBox mFocusBox;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private Button flashbutton;

    private boolean useFlash = false;

    private static final long VIBRATE_DURATION = 200L;
    private boolean mVibrate;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(getResources().getIdentifier("ocr_capture", "layout", getPackageName()));

        mPreview = (CameraSourcePreview) findViewById(getResources().getIdentifier("preview", "id", getPackageName()));
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(getResources().getIdentifier("graphicOverlay", "id", getPackageName()));


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        flashbutton = (Button)findViewById(getResources().getIdentifier("flash_button", "id", getPackageName()));
        flashbutton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if(view.getId() == getResources().getIdentifier("flash_button", "id", getPackageName())){
            if(!useFlash){
                mCameraSource.setFlashMode(FLASH_MODE_TORCH);
                useFlash = true;
                flashbutton.setBackgroundResource(getResources().getIdentifier("flash_off", "drawable", getPackageName()));
            }
            else {
                mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                useFlash = false;
                flashbutton.setBackgroundResource(getResources().getIdentifier("flash_on", "drawable", getPackageName()));
            }

        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, getResources().getIdentifier("permission_camera_rationale", "string", getPackageName()),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getIdentifier("ok", "string", getPackageName()), listener)
                .show();
    }


    @SuppressLint("InlinedApi")
    private void createCameraSource() {
        Context context = getApplicationContext();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(OcrCaptureActivity.this, mGraphicOverlay));

        if (!textRecognizer.isOperational()) {

            Log.w(TAG, "Detector dependencies are not yet available.");

            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, getResources().getIdentifier("low_storage_error", "string", getPackageName()), Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(getResources().getIdentifier("low_storage_error", "string", getPackageName())));
            }
        }

        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE)
                .build();
        mCameraSource.myActivity = this;

        mCameraSource.focusbox = (FocusBoxView)findViewById(getResources().getIdentifier("focus_box", "id", getPackageName()));
    }


    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(getResources().getIdentifier("no_camera_permission", "string", getPackageName()))
                .setPositiveButton(getResources().getIdentifier("ok", "string", getPackageName()), listener)
                .show();
    }

    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    public void detectionSuccess(boolean flag,TextBlock item) {

        if(flag){

            Intent data = new Intent();
            String result = getL(item).replaceAll("[^\\d-]", "");
            data.putExtra(TextBlockObject, result);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    private String getL(TextBlock block){
        List<Line> lines = (List<Line>) block.getComponents();
        String str = "";
        for(Line elements : lines){
            str = elements.getValue().toString();
        }
        return str;
    }

}
