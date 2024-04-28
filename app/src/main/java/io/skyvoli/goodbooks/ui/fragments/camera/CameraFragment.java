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

import java.util.Objects;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentCameraBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.helper.TitleBuilder;
import io.skyvoli.goodbooks.helper.listener.ScanListener;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.BookResolver;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private CameraViewModel cameraViewModel;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;
    private TextView title;
    private TextView isbnText;
    private TextView author;
    private ImageView cover;
    private GlobalController globalController;
    private TextView information;
    private Button addBookBtn;
    private Book scannedBook;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                this::onScanResult);

        globalController = new GlobalController(requireActivity());

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.bookPreview.floatingActionButton.setVisibility(View.GONE);

        constraintLayout = binding.bookPreview.bookData;
        title = binding.bookPreview.title;
        isbnText = binding.bookPreview.isbn;
        author = binding.bookPreview.author;
        cover = binding.bookPreview.cover;
        information = binding.information;
        progressBar = binding.progressBar;

        constraintLayout.setVisibility(View.INVISIBLE);

        final Button scanBtn = binding.scanBtn;
        addBookBtn = binding.addBookBtn;
        scanBtn.setOnClickListener(new ScanListener(barcodeLauncher));
        addBookBtn.setOnClickListener(this::addBook);
        addBookBtn.setEnabled(false);

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

        Optional<Book> dupe = globalController.getBook(isbn);

        if (dupe.isPresent()) {
            scannedBook = dupe.get();
            refreshBook(requireContext(), true);
            constraintLayout.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.INVISIBLE);

        new Thread(() -> {
            Context context = requireContext();
            scannedBook = new BookResolver().resolveBook(isbn, 10);

            if (!isAdded()) {
                return;
            }
            requireActivity().runOnUiThread(() -> {
                addBookBtn.setEnabled(true);
                refreshBook(context, false);
                constraintLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            });
        }).start();

    }

    private void setCover(Drawable drawable) {
        ((ConstraintLayout.LayoutParams) cover.getLayoutParams()).dimensionRatio
                = String.valueOf(getRatio(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
        cover.setImageDrawable(drawable);
    }

    private float getRatio(int width, int height) {
        if (height != 0) {
            return (float) width / height;
        } else {
            return 2.3f; // Handle divide by zero case
        }
    }

    private void addBook(View v) {
        addBookBtn.setEnabled(false);
        if (scannedBook == null) {
            Toast.makeText(requireContext(), "Book is null", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> globalController.addBook(scannedBook, requireContext())).start();

        information.setText(R.string.book_already_in_list);

        new InformationDialog("Hinzugefügt", "Das Buch wurde zur Liste hinzugefügt")
                .show(getParentFragmentManager(), "added");
    }

    private void refreshBook(Context context, boolean isAlreadyInList) {
        title.setText(TitleBuilder.buildWholeTitle(scannedBook.getTitle(), scannedBook.getSubtitle(), scannedBook.getPart()));
        isbnText.setText(scannedBook.getIsbn());
        author.setText(scannedBook.getAuthor());
        Optional<Drawable> drawable = scannedBook.getCover();
        if (drawable.isPresent()) {
            setCover(drawable.get());
        } else {
            //Default
            setCover(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.ruby)));
        }

        if (isAlreadyInList) {
            information.setText(R.string.book_already_in_list);
        } else {
            information.setText(R.string.new_book);
        }
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