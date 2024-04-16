package io.skyvoli.goodbooks.ui.fragments.bookdetail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentBookDetailBinding;
import io.skyvoli.goodbooks.model.GlobalViewModel;
import io.skyvoli.goodbooks.storage.database.dto.Book;

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;
    private final String logTag = this.getClass().getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalViewModel globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);
        BookDetailViewModel bookDetailViewModel = new ViewModelProvider(this).get(BookDetailViewModel.class);

        binding = FragmentBookDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            Log.d("Index", String.valueOf(getArguments().getInt("index")));
            String isbn = Optional.ofNullable(getArguments().getString("isbn")).orElse("No Isbn");
            Book book = globalViewModel.getBooks().stream()
                    .filter(el -> el.sameBook(isbn))
                    .findAny()
                    .orElse(new Book(isbn));
            bookDetailViewModel.setBook(book);
        } else {
            bookDetailViewModel.setBook(new Book("No Isbn"));
            Log.e(logTag, "Missing argument isbn");
        }

        final TextView title = binding.title;
        final ImageView cover = binding.cover;
        final TextView isbn = binding.isbn;
        final TextView author = binding.author;

        Book book = bookDetailViewModel.getBook();
        String wholeTitle = book.getTitle() + " " + book.getPart();
        title.setText(wholeTitle);
        isbn.setText(book.getIsbn());
        author.setText(book.getAuthor());

        Optional<Drawable> drawable = book.getCover();
        if (drawable.isPresent()) {
            cover.setImageDrawable(drawable.get());
        } else {
            cover.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ruby));
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}