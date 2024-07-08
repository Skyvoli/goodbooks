package io.skyvoli.goodbooks.ui.fragments.books;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentBooksBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.helper.SwipeColorSchemeConfigurator;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.ui.fragments.StartFragmentListener;
import io.skyvoli.goodbooks.ui.recyclerviews.bookcard.BookAdapter;
import io.skyvoli.goodbooks.web.BookResolver;

public class BooksFragment extends Fragment implements StartFragmentListener {

    private FragmentBooksBinding binding;
    private ProgressBar progressBar;
    private TextView placeholder;
    private RecyclerView recyclerView;
    private List<Book> books;
    private GlobalController globalController;
    private boolean shouldConfigureUi = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        globalController = new GlobalController(requireActivity());

        binding = FragmentBooksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        requireActivity().addMenuProvider(getMenuProvider(), getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return root;
    }

    @Override
    public void configureFragment() {
        if (shouldConfigureUi) {
            shouldConfigureUi = false;

            recyclerView = binding.books;
            placeholder = binding.placeholder;
            progressBar = binding.progressBar;

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setVisibility(View.INVISIBLE);

            final SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;

            SwipeColorSchemeConfigurator.setSwipeColorScheme(swipeRefreshLayout, requireContext());
            swipeRefreshLayout.setOnRefreshListener(() -> onSwipe(swipeRefreshLayout, requireContext()));

            //TODO pagination?
            if (books == null) {
                new Thread(() -> {
                    books = new ArrayList<>(globalController.loadBooksFromDb(requireContext()));
                    if (isAdded()) {
                        requireActivity().runOnUiThread(this::loadingCompleted);
                    }
                }).start();
            } else {
                books = globalController.getBooks();
                loadingCompleted();
            }
        }
    }

    private void loadingCompleted() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(new BookAdapter(books));
        recyclerView.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in));
        recyclerView.setVisibility(View.VISIBLE);
        setPlaceholder();
    }

    private MenuProvider getMenuProvider() {
        return new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.books_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.reset_item) {
                    resolveBooks();
                    return true;
                }
                return false;
            }
        };
    }

    private void resolveBooks() {
        Activity activity = requireActivity();
        Context context = requireContext();

        List<String> unresolvedIsbn = books.stream()
                .filter((book -> !book.isResolved()))
                .map(Book::getIsbn)
                .collect(Collectors.toList());

        List<Book> unresolvedImages = books.stream()
                .filter((book -> !book.getCover().isPresent() && book.isResolved()))
                .collect(Collectors.toList());

        if (unresolvedImages.isEmpty() && unresolvedIsbn.isEmpty()) {
            new InformationDialog("Bücher vollständig", "Alle Bücher sind vollständig geladen").show(getParentFragmentManager(), "completed");
            return;
        }

        new Thread(() -> {
            BookResolver resolver = new BookResolver();

            if (!unresolvedIsbn.isEmpty()) {
                long seriesIdUnknown = books.stream()
                        .filter((book -> !book.isResolved()))
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new)
                        .getSeriesId();

                unresolvedIsbn.forEach(isbn -> {
                    Book resolved = resolver.resolveBook(isbn, 10);
                    resolved.setSeriesId(seriesIdUnknown);
                    globalController.updateBook(resolved, requireContext());
                });
            }

            unresolvedImages.forEach(book -> resolver.loadImage(book.getIsbn(), 15)
                    .ifPresent(drawable -> {
                        book.setCover(drawable);
                        globalController.updateCoverOfBook(book, context);
                    }));

            activity.runOnUiThread(() ->
                    Toast.makeText(context, "Reloaded", Toast.LENGTH_SHORT).show());
        }).start();

    }

    private void onSwipe(SwipeRefreshLayout swipeRefreshLayout, Context context) {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
            recyclerView.setVisibility(View.INVISIBLE);

            books = globalController.loadBooksFromDb(requireContext());

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    recyclerView.setAdapter(new BookAdapter(books));
                    setPlaceholder();
                    recyclerView.clearAnimation();
                    recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                    recyclerView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        }).start();
    }

    private void setPlaceholder() {
        if (!books.isEmpty()) {
            placeholder.setVisibility(View.GONE);
        } else {
            placeholder.setVisibility(View.VISIBLE);
        }
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