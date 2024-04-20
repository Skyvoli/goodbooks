package io.skyvoli.goodbooks.ui.fragments.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentCameraBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.helper.listener.ScanListener;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.BookResolver;

public class CameraFragment extends Fragment {

    private final String logTag = this.getClass().getSimpleName();
    private FragmentCameraBinding binding;
    private CameraViewModel cameraViewModel;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;
    private TextView title;
    private TextView author;
    private ImageView cover;
    private GlobalController globalController;
    private Book scannedBook;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                this::onScanResult);

        globalController = new GlobalController(requireActivity());

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textContent;

        constraintLayout = binding.bookData;
        title = binding.title;
        author = binding.author;
        cover = binding.cover;
        progressBar = binding.progressBar;

        constraintLayout.setVisibility(View.INVISIBLE);

        cameraViewModel.getIsbn().observe(getViewLifecycleOwner(), textView::setText);

        final Button scanBtn = binding.scanBtn;
        final Button addBookBtn = binding.addBookBtn;
        scanBtn.setOnClickListener(new ScanListener(barcodeLauncher));
        addBookBtn.setOnClickListener(this::addBook);

        return root;
    }


    private void onScanResult(ScanIntentResult result) {
        if (result.getContents() == null) {
            handleCanceledScan(result);
            return;
        }

        String isbn = result.getContents();
        cameraViewModel.setIsbn(isbn);

        if (!isbnIsBook(isbn)) {
            new InformationDialog("418", "Ich bin kein Buch.")
                    .show(getParentFragmentManager(), "418");
            return;
        }

        if (globalController.hasBook(isbn)) {
            new InformationDialog("Duplikat", "Dieses Buch ist bereits vorhanden")
                    .show(getParentFragmentManager(), "Duplikat");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            Context context = requireContext();
            scannedBook = new BookResolver().resolveBook(isbn, 10);

            if (!isAdded()) {
                return;
            }


            Optional<Drawable> drawable = scannedBook.getCover();
            requireActivity().runOnUiThread(() -> {
                title.setText(scannedBook.getTitle());
                author.setText(scannedBook.getAuthor());
                if (drawable.isPresent()) {
                    cover.setImageDrawable(drawable.get());
                } else {
                    //Default
                    cover.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ruby));
                }
                constraintLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            });
        }).start();

    }

    private void addBook(View v) {
        if (scannedBook == null) {
            Toast.makeText(requireContext(), "Book is null", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> globalController.addBook(scannedBook, requireContext())).start();
    }

    private void handleCanceledScan(ScanIntentResult result) {
        Intent originalIntent = result.getOriginalIntent();
        if (originalIntent == null) {
            Toast.makeText(getActivity(), "Scan abgebrochen", Toast.LENGTH_LONG).show();
        } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
            Toast.makeText(getActivity(), "Erlaubnis zur Nutzung der Kamera nicht gegeben", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isbnIsBook(String isbn) {
        String prefix = isbn.substring(0, 3);
        return isbn.toCharArray().length == 13 && (prefix.equals("978") || prefix.equals("979"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}