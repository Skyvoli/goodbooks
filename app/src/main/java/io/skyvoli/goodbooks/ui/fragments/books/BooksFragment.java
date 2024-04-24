package io.skyvoli.goodbooks.ui.fragments.books;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

        recyclerView = binding.books;
        placeholder = binding.placeholder;
        progressBar = binding.progressBar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        books = new ArrayList<>(globalViewModel.getBooks());

        final SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        //Change colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);

            Context context = requireContext();

            recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));

            recyclerView.setVisibility(View.INVISIBLE);

            List<String> unresolvedIsbn = books.stream()
                    .filter((book -> !book.isResolved())).
                    map(Book::getIsbn)
                    .collect(Collectors.toList());

            File dir = requireContext().getFilesDir();
            AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "books").build();

            new Thread(() -> {
                unresolvedIsbn.forEach(s -> {
                    Book resolved = new BookResolver().resolveBook(s);
                    Optional<Drawable> cover = resolved.getCover();
                    cover.ifPresent(drawable -> new FileStorage(dir).saveImage(resolved.getIsbn(), drawable));

                    globalViewModel.updateBook(resolved);
                    db.bookDao().update(resolved);
                });

                List<Book> reload = db.bookDao().getAll();
                FileStorage fileStorage = new FileStorage(dir);
                reload.forEach((book -> book.setCover(fileStorage.getImage(book.getIsbn()))));
                globalViewModel.setList(reload);

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        recyclerView.setAdapter(new BookAdapter(reload));
                        setPlaceholder(reload, placeholder);
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

        BookAdapter adapter = new BookAdapter(books);
        recyclerView.setAdapter(adapter);
        setPlaceholder(books, placeholder);
        progressBar.setVisibility(View.GONE);
    }

    private void setPlaceholder(List<Book> books, TextView placeholder) {
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