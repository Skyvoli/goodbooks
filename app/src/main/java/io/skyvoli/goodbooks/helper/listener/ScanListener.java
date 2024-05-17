package io.skyvoli.goodbooks.helper.listener;

import android.view.View;

import androidx.activity.result.ActivityResultLauncher;

import com.journeyapps.barcodescanner.ScanOptions;

public class ScanListener implements View.OnClickListener {

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher;
    private final ScanOptions scanOptions = new ScanOptions();

    public ScanListener(ActivityResultLauncher<ScanOptions> barcodeLauncher) {
        this.barcodeLauncher = barcodeLauncher;
        scanOptions.setPrompt("ISBN einscannen");
        scanOptions.setOrientationLocked(false);
        scanOptions.setDesiredBarcodeFormats(ScanOptions.EAN_13);
        scanOptions.setBeepEnabled(true);
    }

    @Override
    public void onClick(View v) {
        barcodeLauncher.launch(scanOptions);
    }
}