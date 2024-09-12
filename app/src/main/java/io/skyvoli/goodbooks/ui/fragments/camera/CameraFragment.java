package io.skyvoli.goodbooks.ui.fragments.camera;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;

import java.util.Objects;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.BookDetailCardBinding;
import io.skyvoli.goodbooks.databinding.FragmentCameraBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.dimensions.Dimension;
import io.skyvoli.goodbooks.global.GlobalController;
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
        //TODO Swipe for refresh
        cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        globalController = new GlobalController(requireActivity());

        requireActivity().addMenuProvider(getMenuProvider(), getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.bookPreview.floatingActionButton.setVisibility(View.GONE);
        binding.scanBtn.setOnClickListener(new ScanListener(
                registerForActivityResult(new ScanContract(),
                        this::onScanResult)));

        return root;
    }

    @Override
    public void configureFragment() {
        if (shouldConfigureUi) {
            shouldConfigureUi = false;

            cameraViewModel.getBook().observe(getViewLifecycleOwner(), book -> setBookView(book, binding.bookPreview));
            cameraViewModel.getShowBook().observe(getViewLifecycleOwner(), showBook -> showBook(showBook, binding.bookPreview.bookData, binding.progressBar));

            binding.addBookBtn.setOnClickListener(this::addBook);
            cameraViewModel.getIsNewBook().observe(getViewLifecycleOwner(), isNew -> setInformationText(isNew, binding.information));
        }
    }

    private MenuProvider getMenuProvider() {
        return new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.books_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int selectedItemId = menuItem.getItemId();
                if (selectedItemId == R.id.reset_item) {
                    new InformationDialog("Reload", "wip")
                            .show(getParentFragmentManager(), "reload");
                    return true;
                }
                return false;
            }
        };
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
            Optional<Book> found = globalController.getBook(requireContext(), isbn);

            if (found.isPresent()) {
                requireActivity().runOnUiThread(() -> {
                    cameraViewModel.setBook(found.get());
                    cameraViewModel.setIsNewBook(false);
                });
                return;
            }

            requireActivity().runOnUiThread(() -> cameraViewModel.setShowBook(false));

            Book scanedBook = new BookResolver(requireContext()).resolveBook(isbn, 10);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    cameraViewModel.setIsNewBook(true);
                    cameraViewModel.setBook(scanedBook);
                    cameraViewModel.setShowBook(true);
                });
            }
        }).start();
    }

    private void setInformationText(Boolean isNew, TextView information) {
        if (isNew == null) {
            information.setText(R.string.no_scanned_placeholder);
            binding.addBookBtn.setEnabled(false);
            return;
        }

        binding.addBookBtn.setEnabled(isNew);
        if (isNew) {
            information.setText(R.string.new_book);
        } else {
            information.setText(R.string.book_already_in_list);
        }
    }

    private void showBook(Boolean showBook, ConstraintLayout constraintLayout, ProgressBar progressBar) {
        if (showBook == null) {
            throw new NullPointerException();
        }
        if (showBook) {
            constraintLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            constraintLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void setBookView(Book book, BookDetailCardBinding bookPreview) {
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