package io.skyvoli.goodbooks.ui.fragments.camera;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.BookDetailCardBinding;
import io.skyvoli.goodbooks.databinding.FragmentCameraBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.dimensions.Dimension;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.helper.DataFormatter;
import io.skyvoli.goodbooks.helper.ISBNChecker;
import io.skyvoli.goodbooks.helper.SwipeColorSchemeConfigurator;
import io.skyvoli.goodbooks.helper.listener.ScanListener;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.ui.fragments.StartFragmentListener;
import io.skyvoli.goodbooks.web.BookResolver;

public class CameraFragment extends Fragment implements StartFragmentListener {

    private FragmentCameraBinding binding;
    private CameraViewModel cameraViewModel;
    private GlobalController globalController;
    private boolean shouldConfigureUi = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        globalController = new GlobalController(requireActivity());

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SwipeColorSchemeConfigurator.setSwipeColorScheme(binding.swipeRefreshLayout, requireContext());
        binding.swipeRefreshLayout.setOnRefreshListener(this::onSwipe);

        binding.bookPreview.floatingActionButton.setVisibility(View.GONE);
        binding.scanBtn.setOnClickListener(new ScanListener(
                registerForActivityResult(new ScanContract(), this::onScanResult)));

        return root;
    }

    private void onSwipe() {
        binding.swipeRefreshLayout.setRefreshing(true);
        Optional<Book> maybeBook = Optional.ofNullable(cameraViewModel.getBook().getValue());

        if (maybeBook.isPresent()) {
            handleIsbn(maybeBook.get().getIsbn());
        } else {
            new InformationDialog("Kein Buch", "Kein Buch vorhanden zum Neuladen").show(getParentFragmentManager(), "");
        }

        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void configureFragment() {
        if (shouldConfigureUi) {
            shouldConfigureUi = false;

            cameraViewModel.getBook().observe(getViewLifecycleOwner(), this::setBookView);
            cameraViewModel.getShowBook().observe(getViewLifecycleOwner(), this::showBook);
            binding.addBookBtn.setOnClickListener(this::addBook);
            cameraViewModel.getIsNewBook().observe(getViewLifecycleOwner(), this::setInformationText);
            cameraViewModel.getMissing().observe(getViewLifecycleOwner(), missing ->
                    binding.missingBooks.setText(DataFormatter.getMissingBooksString(requireContext(), missing)));
        }
    }

    private void onScanResult(ScanIntentResult result) {
        if (result.getContents() == null) {
            handleCanceledScan(result);
        } else {
            handleIsbn(result.getContents());
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

    private void handleIsbn(String isbn) {
        if (ISBNChecker.isbnIsNotBook(isbn)) {
            new InformationDialog("418", "Ich bin kein Buch.")
                    .show(getParentFragmentManager(), "418");
            return;
        }

        cameraViewModel.setShowBook(false);

        new Thread(() -> {
            Book scanedBook;
            boolean isNew;
            Optional<Book> found = globalController.getBook(requireContext(), isbn);

            if (found.isPresent()) {
                isNew = false;
                scanedBook = found.get();
            } else {
                isNew = true;
                scanedBook = new BookResolver(requireContext()).resolveBook(isbn, 10);
            }

            List<Integer> missing = globalController.getPotentialMissingBooks(scanedBook);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> setModel(scanedBook, missing, isNew));
            }
        }).start();
    }

    private void setModel(Book book, List<Integer> missing, boolean isNew) {
        cameraViewModel.setIsNewBook(isNew);
        cameraViewModel.setMissingBooks(missing);
        cameraViewModel.setBook(book);
        cameraViewModel.setShowBook(true);
    }

    private void setInformationText(Boolean isNew) {
        if (isNew == null) {
            binding.information.setText(R.string.no_scanned_placeholder);
            binding.addBookBtn.setEnabled(false);
            return;
        }

        binding.addBookBtn.setEnabled(isNew);
        if (isNew) {
            binding.information.setText(R.string.new_book);
        } else {
            binding.information.setText(R.string.book_already_in_list);
        }
    }

    private void showBook(Boolean showBook) {
        if (showBook == null) {
            throw new NullPointerException();
        }
        if (showBook) {
            binding.bookPreview.bookData.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.bookPreview.bookData.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void setBookView(Book book) {
        BookDetailCardBinding bookPreview = binding.bookPreview;
        bookPreview.title.setText(book.buildCompleteTitle());
        bookPreview.isbn.setText(book.getIsbn());
        bookPreview.author.setText(book.getAuthor());
        Optional<Drawable> drawable = book.getCover();
        if (drawable.isPresent()) {
            setCover(drawable.get());
        } else {
            setCover(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.ruby)));
        }
    }

    private void setCover(Drawable drawable) {
        ImageView cover = binding.bookPreview.cover;
        ((ConstraintLayout.LayoutParams) cover.getLayoutParams()).dimensionRatio
                = new Dimension(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()).getRatio();
        cover.setImageDrawable(drawable);
    }

    private void addBook(View v) {
        cameraViewModel.setIsNewBook(false);
        Book bookToAdd = cameraViewModel.getBook().getValue();

        if (bookToAdd == null) {
            throw new IllegalStateException("Method call is not allowed");
        }

        new Thread(() -> globalController.addBook(bookToAdd, requireContext())).start();

        new InformationDialog("Hinzugefügt", "Das Buch wurde zur Liste hinzugefügt")
                .show(getParentFragmentManager(), "added");
    }

    @Override
    public void onPause() {
        super.onPause();
        shouldConfigureUi = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}