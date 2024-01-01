package io.skyvoli.goodbooks.helper;

import android.view.View;

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
        intentIntegrator.setPrompt("Scan a barcode");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.initiateScan();
    }
}
