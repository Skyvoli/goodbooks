package io.skyvoli.goodbooks.ui.fragments.books;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
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
    private GlobalViewModel globalViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);

        binding = FragmentBooksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Context context = requireContext();

        requireActivity().addMenuProvider(getMenuProvider(), getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        recyclerView = binding.books;
        placeholder = binding.placeholder;
        progressBar = binding.progressBar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        books = new ArrayList<>(globalViewModel.getBooks());

        final SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;

        setSwipeColorScheme(swipeRefreshLayout, context);
        swipeRefreshLayout.setOnRefreshListener(() -> onSwipe(swipeRefreshLayout, context));

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

    private MenuProvider getMenuProvider() {
        return new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.books_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.reset_item) {

                    Activity activity = requireActivity();
                    Context context = requireContext();

                    List<String> unresolvedIsbn = books.stream()
                            .filter((book -> !book.isResolved())).
                            map(Book::getIsbn)
                            .collect(Collectors.toList());

                    List<Book> unresolvedImages = books.stream()
                            .filter((book -> !book.getCover().isPresent() && book.isResolved()))
                            .collect(Collectors.toList());


                    AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "books").build();

                    new Thread(() -> {
                        BookResolver resolver = new BookResolver();
                        FileStorage storage = new FileStorage(context.getFilesDir());

                        unresolvedIsbn.forEach(isbn -> {
                            Book resolved = resolver.resolveBook(isbn, 10);

                            resolved.getCover().ifPresent(drawable ->
                                    storage.saveImage(resolved.getIsbn(), drawable));

                            globalViewModel.updateBook(resolved);
                            db.bookDao().update(resolved);
                        });

                        unresolvedImages.forEach(book -> resolver.loadImage(book.getIsbn(), 15)
                                .ifPresent(drawable -> {
                                    storage.saveImage(book.getIsbn(), drawable);
                                    book.setCover(drawable);
                                    globalViewModel.updateBook(book);
                                }));

                        activity.runOnUiThread(() ->
                                Toast.makeText(context, "Reloaded", Toast.LENGTH_SHORT).show());
                    }).start();


                    return true;
                }
                return false;
            }
        };
    }

    private void setSwipeColorScheme(SwipeRefreshLayout swipeRefreshLayout, Context context) {
        //Change colors
        TypedValue primary = new TypedValue();
        TypedValue secondary = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, primary, true);
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, secondary, true);

        swipeRefreshLayout.setColorSchemeResources(primary.resourceId, secondary.resourceId);
    }

    private void onSwipe(SwipeRefreshLayout swipeRefreshLayout, Context context) {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
            recyclerView.setVisibility(View.INVISIBLE);

            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "books").build();
            books = db.bookDao().getAll();
            FileStorage fileStorage = new FileStorage(context.getFilesDir());
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