package io.skyvoli.goodbooks.ui.fragments.books;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import io.skyvoli.goodbooks.ui.bookcard.BookTouchHelperCallback;
import io.skyvoli.goodbooks.web.BookResolver;

public class BooksFragment extends Fragment {

    private FragmentBooksBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalViewModel globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);

        binding = FragmentBooksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button button = binding.sync;

        final RecyclerView recyclerView = binding.books;
        final TextView placeholder = binding.placeholder;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Book> books = new ArrayList<>(globalViewModel.getBooks());
        BookAdapter adapter = new BookAdapter(books);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new BookTouchHelperCallback(adapter));
        helper.attachToRecyclerView(recyclerView);

        button.setOnClickListener(v ->
        {
            List<String> unresolvedIsbn = books.stream()
                    .filter((book -> !book.isResolved())).
                    map(Book::getIsbn)
                    .collect(Collectors.toList());

            //TODO in background and reload
            unresolvedIsbn.forEach(s -> new Thread(() -> {
                Book resolved = new BookResolver().resolveBook(s);
                Optional<Drawable> cover = resolved.getCover();
                cover.ifPresent(drawable -> new FileStorage(requireContext().getFilesDir()).saveImage(resolved.getIsbn(), drawable));
                globalViewModel.updateBook(resolved);
            }).start());
        });
        setPlaceholder(books, placeholder);
        if (isAdded()) {
            globalViewModel.getBooks().addOnListChangedCallback(new BookObserver(binding, requireActivity()));
        }

        return root;
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