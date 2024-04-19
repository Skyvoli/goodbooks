package io.skyvoli.goodbooks.ui.fragments.books;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.databinding.FragmentBooksBinding;
import io.skyvoli.goodbooks.helper.observer.BookObserver;
import io.skyvoli.goodbooks.model.GlobalViewModel;
import io.skyvoli.goodbooks.storage.FileStorage;
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

        final Button button = binding.sync;

        recyclerView = binding.books;
        placeholder = binding.placeholder;
        progressBar = binding.progressBar;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        books = new ArrayList<>(globalViewModel.getBooks());

        button.setOnClickListener(v ->
        {
            List<String> unresolvedIsbn = books.stream()
                    .filter((book -> !book.isResolved())).
                    map(Book::getIsbn)
                    .collect(Collectors.toList());
            
            unresolvedIsbn.forEach(s -> new Thread(() -> {
                Book resolved = new BookResolver().resolveBook(s);
                Optional<Drawable> cover = resolved.getCover();
                cover.ifPresent(drawable -> new FileStorage(requireContext().getFilesDir()).saveImage(resolved.getIsbn(), drawable));
                globalViewModel.updateBook(resolved);
            }).start());
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