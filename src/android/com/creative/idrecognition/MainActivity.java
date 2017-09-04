
package com.creative.idrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;

public class MainActivity extends Activity implements View.OnClickListener {

    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;

    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("activity_main", "layout", getPackageName()));

        statusMessage = (TextView)findViewById(getResources().getIdentifier("status_message", "id", getPackageName()));
        textValue = (TextView)findViewById(getResources().getIdentifier("text_value", "id", getPackageName()));

        autoFocus = (CompoundButton) findViewById(getResources().getIdentifier("auto_focus", "id", getPackageName()));
        useFlash = (CompoundButton) findViewById(getResources().getIdentifier("use_flash", "id", getPackageName()));

        findViewById(getResources().getIdentifier("read_text", "id", getPackageName())).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == getResources().getIdentifier("read_text", "id", getPackageName())) {
            // launch Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_OCR_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    statusMessage.setText(getResources().getIdentifier("ocr_success", "string", getPackageName()));
                    textValue.setText(text);
                    Log.d(TAG, "Text read: " + text);
                } else {
                    statusMessage.setText(getResources().getIdentifier("ocr_failure", "string", getPackageName()));
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {s
                statusMessage.setText(String.format(getString(getResources().getIdentifier("ocr_error", "string", getPackageName())),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
