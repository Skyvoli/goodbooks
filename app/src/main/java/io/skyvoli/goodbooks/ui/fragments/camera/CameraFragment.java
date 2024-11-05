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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.BookDetailCardBinding;
import io.skyvoli.goodbooks.databinding.FragmentCameraBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.dimensions.Dimension;
import io.skyvoli.goodbooks.global.GlobalController;
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
    private static final int BORDER = 6;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        globalController = new GlobalController(requireActivity());

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        SwipeColorSchemeConfigurator.setSwipeColorScheme(binding.swipeRefreshLayout, requireContext());
        swipeRefreshLayout.setOnRefreshListener(this::onSwipe);

        binding.bookPreview.floatingActionButton.setVisibility(View.GONE);
        binding.scanBtn.setOnClickListener(new ScanListener(
                registerForActivityResult(new ScanContract(),
                        this::onScanResult)));

        return root;
    }

    private void onSwipe() {
        binding.swipeRefreshLayout.setRefreshing(true);
        Optional<Book> maybeBook = Optional.ofNullable(cameraViewModel.getBook().getValue());

        if (maybeBook.isPresent()) {
            String isbn = maybeBook.get().getIsbn();
            handleIsbn(isbn);
        } else {
            //Ignore
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
            cameraViewModel.getMissing().observe(getViewLifecycleOwner(), this::setMissingBooksText);
        }
    }

    private void onScanResult(ScanIntentResult result) {
        if (result.getContents() == null) {
            handleCanceledScan(result);
            return;
        }

        handleIsbn(result.getContents());
    }

    private void handleIsbn(String isbn) {
        if (!isbnIsBook(isbn)) {
            new InformationDialog("418", "Ich bin kein Buch.")
                    .show(getParentFragmentManager(), "418");
            return;
        }

        new Thread(() -> {
            Book scanedBook;
            boolean isNew;
            Optional<Book> found = globalController.getBook(requireContext(), isbn);

            if (found.isPresent()) {
                isNew = false;
                scanedBook = found.get();
            } else {
                isNew = true;
                requireActivity().runOnUiThread(() -> cameraViewModel.setShowBook(false));
                scanedBook = new BookResolver(requireContext()).resolveBook(isbn, 10);
            }

            List<Integer> missing = globalController.getPotentialMissingBooks(scanedBook);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    cameraViewModel.setIsNewBook(isNew);
                    cameraViewModel.setMissingBooks(missing);
                    cameraViewModel.setBook(scanedBook);
                    cameraViewModel.setShowBook(true);
                });
            }
        }).start();
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

    private void setMissingBooksText(List<Integer> missing) {
        if (missing.isEmpty()) {
            binding.missingBooks.setText("Es fehlen keine Bücher");
            return;
        } else if (missing.size() == 1) {
            binding.missingBooks.setText(new StringBuilder("Buch ").append(missing.get(0)).append(" fehlt"));
            return;
        }

        StringBuilder builder = new StringBuilder("Bücher ");
        Iterator<Integer> iterator = missing.iterator();

        if (missing.size() > BORDER) {
            builder.append(missing.get(0));
            for (int index = 1; index < BORDER; index++) {
                builder.append(", ").append(missing.get(index));
            }
            builder.setLength(builder.length() - 1);
            builder.append("& ").append(missing.size() - (BORDER - 1)).append(" weitere Bücher");
        } else {
            builder.append(iterator.next());
            while (iterator.hasNext()) {
                builder.append(", ").append(iterator.next());
            }
        }

        builder.append(" fehlen");
        binding.missingBooks.setText(builder);
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
            setCover(drawable.get(), bookPreview);
        } else {
            setCover(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.ruby)), bookPreview);
        }
    }

    private void setCover(Drawable drawable, BookDetailCardBinding bookPreview) {
        ImageView cover = bookPreview.cover;
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