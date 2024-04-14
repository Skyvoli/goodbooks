package io.skyvoli.goodbooks.ui.fragments.bookdetail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;

import io.skyvoli.goodbooks.databinding.FragmentBookDetailBinding;
import io.skyvoli.goodbooks.storage.database.dto.Book;

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BookDetailViewModel bookDetailViewModel =
                new ViewModelProvider(this).get(BookDetailViewModel.class);

        binding = FragmentBookDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            Log.d("Index", String.valueOf(getArguments().getInt("index")));
            String isbn = Optional.ofNullable(getArguments().getString("isbn")).orElse("No Isbn");
            bookDetailViewModel.setBook(new Book(isbn));
        } else {
            bookDetailViewModel.setBook(new Book("No Isbn"));
        }

        final TextView textView = binding.textHome;
        textView.setText(bookDetailViewModel.getBook().getIsbn());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}