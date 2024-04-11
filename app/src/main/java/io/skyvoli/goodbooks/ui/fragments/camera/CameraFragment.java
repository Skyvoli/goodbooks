package io.skyvoli.goodbooks.ui.fragments.camera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import io.skyvoli.goodbooks.constants.Constants;
import io.skyvoli.goodbooks.databinding.FragmentCameraBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.dialog.NoticeDialogListener;
import io.skyvoli.goodbooks.dialog.PermissionDialog;
import io.skyvoli.goodbooks.helper.BookResolver;
import io.skyvoli.goodbooks.listener.ScanListener;
import io.skyvoli.goodbooks.model.GlobalViewModel;
import io.skyvoli.goodbooks.storage.Storage;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private CameraViewModel cameraViewModel;

    private final String logTag = this.getClass().getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                this::onScanResult);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textContent;
        cameraViewModel.getText1().observe(getViewLifecycleOwner(), textView::setText);

        final TextView textView2 = binding.textFormat;
        cameraViewModel.getText2().observe(getViewLifecycleOwner(), textView2::setText);

        final Button button = binding.scanBtn;
        button.setOnClickListener(new ScanListener(barcodeLauncher));

        return root;
    }


    private void onScanResult(ScanIntentResult result) {
        if (result.getContents() == null) {
            handleCanceledScan(result);
            return;
        }

        String isbn = result.getContents();
        Log.i(logTag, "Scanned " + isbn);
        cameraViewModel.setText1(isbn);
        cameraViewModel.setText2(result.getFormatName());

        if (!isbnIsBook(isbn)) {
            InformationDialog informationDialog = new InformationDialog("418", "Ich bin kein Buch.");
            informationDialog.show(getParentFragmentManager(), "418");
            return;
        }

        GlobalViewModel globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);

        if (globalViewModel.hasBook(isbn)) {
            InformationDialog informationDialog = new InformationDialog("Duplikat", "Dieses Buch ist bereits vorhanden");
            informationDialog.show(getParentFragmentManager(), "Duplikat");
            return;
        }

        PermissionDialog permissionDialog = new PermissionDialog("Buch erkannt",
                "Soll das Buch mit ISBN " + isbn + " hinzugefügt werden?",
                addBookListener(globalViewModel, isbn));
        permissionDialog.show(getParentFragmentManager(), "Buch erkannt");
    }

    private void handleCanceledScan(ScanIntentResult result) {
        Intent originalIntent = result.getOriginalIntent();
        if (originalIntent == null) {
            Log.d(logTag, "Cancelled scan");
            Toast.makeText(getActivity(), "Scan abgebrochen", Toast.LENGTH_LONG).show();
        } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
            Log.d(logTag, "Cancelled scan due to missing camera permission");
            Toast.makeText(getActivity(), "Erlaubnis zur Nutzung der Kamera nicht gegeben", Toast.LENGTH_LONG).show();
        }

    }

    private boolean isbnIsBook(String isbn) {
        String prefix = isbn.substring(0, 3);
        return isbn.toCharArray().length == 13 && (prefix.equals("978") || prefix.equals("979"));
    }

    private NoticeDialogListener addBookListener(GlobalViewModel globalViewModel, String isbn) {
        return new NoticeDialogListener() {
            @Override
            public void onDialogPositiveClick() {
                BookResolver bookResolver = new BookResolver();
                globalViewModel.addBook(bookResolver.resolveBook(isbn));
                Storage storage = new Storage(requireContext().getFilesDir());
                storage.saveBooks(Constants.FILENAME_BOOKS, globalViewModel.getBooks().getValue());
            }

            @Override
            public void onDialogNegativeClick() {
                Toast.makeText(getContext(), "Nicht hinzugefügt", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}