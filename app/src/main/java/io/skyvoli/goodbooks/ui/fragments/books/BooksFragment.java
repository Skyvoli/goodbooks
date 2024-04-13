package io.skyvoli.goodbooks.ui.fragments.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import io.skyvoli.goodbooks.databinding.FragmentBooksBinding;
import io.skyvoli.goodbooks.model.GlobalViewModel;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.ui.BookViewHolder;
import io.skyvoli.goodbooks.ui.adapter.BookAdapter;

public class BooksFragment extends Fragment {

    private FragmentBooksBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalViewModel globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);

        binding = FragmentBooksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final RecyclerView recyclerView = binding.books;
        final TextView placeholder = binding.placeholder;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Book> books = Objects.requireNonNull(globalViewModel.getBooks().getValue());

        RecyclerView.Adapter<BookViewHolder> adapter =
                new BookAdapter(books);
        recyclerView.setAdapter(adapter);

        if (!books.isEmpty()) {
            placeholder.setVisibility(View.GONE);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}