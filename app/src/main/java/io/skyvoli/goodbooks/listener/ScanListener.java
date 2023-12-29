package io.skyvoli.goodbooks.listener;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;

public class ScanListener implements View.OnClickListener {

    private final Fragment fragment;

    public ScanListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {
        // we need to create the object
        // of IntentIntegrator class
        // which is the class of QR library
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(fragment);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.initiateScan();
    }
}
