package io.skyvoli.goodbooks.helper.listener;

import android.view.View;

import androidx.activity.result.ActivityResultLauncher;

import com.journeyapps.barcodescanner.ScanOptions;

public class ScanListener implements View.OnClickListener {

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher;

    public ScanListener(ActivityResultLauncher<ScanOptions> barcodeLauncher) {
        this.barcodeLauncher = barcodeLauncher;
    }

    @Override
    public void onClick(View v) {
        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setPrompt("ISBN einscannen");
        scanOptions.setOrientationLocked(false);
        scanOptions.setDesiredBarcodeFormats(ScanOptions.EAN_13);
        scanOptions.setBeepEnabled(true);
        barcodeLauncher.launch(scanOptions);
    }
}