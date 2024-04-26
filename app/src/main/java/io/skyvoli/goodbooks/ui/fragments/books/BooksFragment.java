package io.skyvoli.goodbooks.ui.fragments.books;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentBooksBinding;
import io.skyvoli.goodbooks.helper.observer.BookObserver;
import io.skyvoli.goodbooks.model.GlobalViewModel;
import io.skyvoli.goodbooks.storage.FileStorage;
import io.skyvoli.goodbooks.storage.database.AppDatabase;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.ui.bookcard.BookAdapter;
import io.skyvoli.goodbooks.web.BookResolver;

public class BooksFragment extends Fragment {

    private FragmentBooksBinding binding;
    private ProgressBar progressBar;
    private TextView placeholder;
    private RecyclerView recyclerView;
    private List<Book> books;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalViewModel globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);

        binding = FragmentBooksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Context context = requireContext();

        recyclerView = binding.books;
        placeholder = binding.placeholder;
        progressBar = binding.progressBar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        books = new ArrayList<>(globalViewModel.getBooks());

        final SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        //Change colors
        TypedValue primary = new TypedValue();
        TypedValue secondary = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, primary, true);
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, secondary, true);

        swipeRefreshLayout.setColorSchemeResources(primary.resourceId,
                secondary.resourceId);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);

            recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));

            recyclerView.setVisibility(View.INVISIBLE);

            List<String> unresolvedIsbn = books.stream()
                    .filter((book -> !book.isResolved())).
                    map(Book::getIsbn)
                    .collect(Collectors.toList());

            List<Book> unresolvedImages = books.stream()
                    .filter((book -> !book.getCover().isPresent() && book.isResolved()))
                    .collect(Collectors.toList());

            File dir = context.getFilesDir();
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "books").build();

            new Thread(() -> {
                BookResolver resolver = new BookResolver();
                unresolvedIsbn.forEach(isbn -> {
                    Book resolved = resolver.resolveBook(isbn, 10);
                    Optional<Drawable> cover = resolved.getCover();
                    cover.ifPresent(drawable -> new FileStorage(dir).saveImage(resolved.getIsbn(), drawable));

                    globalViewModel.updateBook(resolved);
                    db.bookDao().update(resolved);
                });

                unresolvedImages.forEach(book -> {
                    Optional<Drawable> cover = resolver.loadImage(book.getIsbn(), 15);
                    cover.ifPresent(drawable -> {
                        new FileStorage(dir).saveImage(book.getIsbn(), drawable);
                        book.setCover(drawable);
                        globalViewModel.updateBook(book);
                    });
                });

                books = db.bookDao().getAll();
                FileStorage fileStorage = new FileStorage(dir);
                books.forEach((book -> book.setCover(fileStorage.getImage(book.getIsbn()))));
                globalViewModel.setList(books);

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


        });

        if (isAdded()) {
            globalViewModel.getBooks().addOnListChangedCallback(new BookObserver(binding, requireActivity()));
        }


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView.setAdapter(new BookAdapter(books));
        setPlaceholder();
        progressBar.setVisibility(View.GONE);
    }

    private void setPlaceholder() {
        if (!books.isEmpty()) {
            placeholder.setVisibility(View.GONE);
        } else {
            placeholder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}