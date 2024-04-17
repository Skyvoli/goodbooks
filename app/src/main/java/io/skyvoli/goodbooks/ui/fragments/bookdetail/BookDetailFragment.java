package io.skyvoli.goodbooks.ui.fragments.bookdetail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

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

        binding = FragmentBookDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView title = binding.title;
        final ImageView cover = binding.cover;
        final TextView isbn = binding.isbn;
        final TextView author = binding.author;
        final TextInputEditText editTitle = binding.editTitle;
        final EditText editPart = binding.editPart;
        final TextInputEditText editAuthor = binding.editAuthor;

        Book book = loadBook(getArguments(), globalViewModel);
        //Set content
        title.setText(buildWholeTitle(book.getTitle(), book.getPart()));
        Optional<Drawable> drawable = book.getCover();
        if (drawable.isPresent()) {
            cover.setImageDrawable(drawable.get());
        } else {
            cover.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ruby));
        }
        isbn.setText(book.getIsbn());
        author.setText(book.getAuthor());
        editTitle.setText(book.getTitle());
        Integer part = book.getPart();
        if (part != null) {
            editPart.setText(String.valueOf(part));
        }
        editAuthor.setText(book.getAuthor());

        //Set listener
        editTitle.setOnFocusChangeListener(getTitleListener(title, editTitle, book.getPart()));
        editPart.setOnFocusChangeListener(getPartListener(title, editPart, book.getTitle()));
        editAuthor.setOnFocusChangeListener(getAuthorListner(author, editAuthor));

        return root;
    }


    private Book loadBook(Bundle arguments, GlobalViewModel globalViewModel) {
        if (arguments != null) {
            String isbn = Optional.ofNullable(getArguments().getString("isbn")).orElse("No Isbn");
            return globalViewModel.getBooks().stream()
                    .filter(el -> el.sameBook(isbn))
                    .findAny()
                    .orElse(new Book(isbn));
        } else {

            Log.e(logTag, "Missing argument isbn");
            return new Book("No Isbn");
        }
    }

    private String buildWholeTitle(String title, Integer part) {
        if (part != null) {
            return title + " " + part;
        } else {
            return title;
        }
    }

    private View.OnFocusChangeListener getTitleListener(TextView title, TextInputEditText
            editTitle, Integer part) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newTitle = editTitle.getText();
                if (newTitle != null && !newTitle.toString().contentEquals(title.getText())) {
                    title.setText(buildWholeTitle(newTitle.toString(), part));
                }
            }
        };
    }

    private View.OnFocusChangeListener getPartListener(TextView title, EditText
            editPart, String titleString) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newPart = editPart.getText();
                try {
                    if (newPart != null && !newPart.toString().contentEquals(title.getText())) {
                        title.setText(buildWholeTitle(titleString, Integer.valueOf(newPart.toString())));
                    }
                } catch (NumberFormatException e) {
                    editPart.setText(null);
                }
            }
        };
    }

    private View.OnFocusChangeListener getAuthorListner(TextView author, TextInputEditText editAuthor) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newAuthor = editAuthor.getText();
                if (newAuthor != null && !newAuthor.toString().contentEquals(author.getText())) {
                    author.setText(newAuthor.toString());
                }
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}