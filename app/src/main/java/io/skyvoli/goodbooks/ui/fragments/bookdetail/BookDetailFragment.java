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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentBookDetailBinding;
import io.skyvoli.goodbooks.model.GlobalViewModel;
import io.skyvoli.goodbooks.storage.database.dto.Book;

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;
    private final String logTag = this.getClass().getSimpleName();
    MutableLiveData<Book> book;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalViewModel globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);

        binding = FragmentBookDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView title = binding.title;
        final ImageView cover = binding.cover;
        final TextView isbn = binding.isbn;
        final TextView author = binding.author;
        final TextInputLayout partLayout = binding.partLayout;
        final TextInputEditText editTitle = binding.editTitle;
        final EditText editPart = binding.editPart;
        final TextInputEditText editAuthor = binding.editAuthor;
        Book start = loadBook(globalViewModel);
        book = new MutableLiveData<>(start);

        //Set content
        Optional<Drawable> drawable = start.getCover();
        if (drawable.isPresent()) {
            cover.setImageDrawable(drawable.get());
        } else {
            cover.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ruby));
        }
        isbn.setText(start.getIsbn());
        book.observe(getViewLifecycleOwner(), bookValue -> {
            title.setText(buildWholeTitle(Objects.requireNonNull(book.getValue()).getTitle(), book.getValue().getPart()));
            author.setText(bookValue.getAuthor());
        });


        editTitle.setText(start.getTitle());
        Integer part = start.getPart();
        if (part != null) {
            editPart.setText(String.valueOf(part));
        }
        editAuthor.setText(start.getAuthor());

        //Set listener
        editTitle.setOnFocusChangeListener(getTitleListener(title, editTitle, Objects.requireNonNull(book.getValue()).getPart()));
        editPart.setOnFocusChangeListener(getPartListener(title, editPart, partLayout));
        editAuthor.setOnFocusChangeListener(getAuthorListener(author, editAuthor));

        return root;
    }


    private Book loadBook(GlobalViewModel globalViewModel) {
        if (getArguments() != null) {
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
                    Book newBook = book.getValue();
                    Objects.requireNonNull(newBook).setTitle(newTitle.toString());
                    book.setValue(newBook);
                }
            }
        };
    }

    private View.OnFocusChangeListener getPartListener(TextView title, EditText
            editPart, TextInputLayout partLayout) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newPart = editPart.getText();
                partLayout.setError(null);
                try {
                    if (newPart != null && !newPart.toString().contentEquals(title.getText())) {
                        Book newBook = book.getValue();
                        Objects.requireNonNull(newBook).setPart(Integer.valueOf(newPart.toString()));
                        book.setValue(newBook);
                    }
                } catch (NumberFormatException e) {
                    partLayout.setError("Bitte geben Sie eine Ganzzahl ein.");
                }
            }
        };
    }

    private View.OnFocusChangeListener getAuthorListener(TextView author, TextInputEditText editAuthor) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newAuthor = editAuthor.getText();
                if (newAuthor != null && !newAuthor.toString().contentEquals(author.getText())) {
                    Book newBook = book.getValue();
                    Objects.requireNonNull(newBook).setAuthor(newAuthor.toString());
                    book.setValue(newBook);
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