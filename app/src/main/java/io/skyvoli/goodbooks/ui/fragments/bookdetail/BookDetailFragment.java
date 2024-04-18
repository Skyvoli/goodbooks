package io.skyvoli.goodbooks.ui.fragments.bookdetail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentBookDetailBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.helper.BackgroundTask;
import io.skyvoli.goodbooks.model.GlobalViewModel;
import io.skyvoli.goodbooks.storage.database.AppDatabase;
import io.skyvoli.goodbooks.storage.database.dto.Book;

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;
    private final String logTag = this.getClass().getSimpleName();
    private MutableLiveData<Book> book;
    private Book originalBook;
    private Button submit;

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
        submit = binding.submitChanges;
        originalBook = loadBook(globalViewModel);
        book = new MutableLiveData<>(originalBook.createClone());

        //Set content & observables
        Optional<Drawable> drawable = originalBook.getCover();
        if (drawable.isPresent()) {
            cover.setImageDrawable(drawable.get());
        } else {
            cover.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ruby));
        }
        isbn.setText(originalBook.getIsbn());
        book.observe(getViewLifecycleOwner(), bookValue -> {
            title.setText(buildWholeTitle(Objects.requireNonNull(book.getValue()).getTitle(), book.getValue().getPart()));
            author.setText(bookValue.getAuthor());
        });

        //Set editable
        editTitle.setText(originalBook.getTitle());
        Integer part = originalBook.getPart();
        if (part != null) {
            editPart.setText(String.valueOf(part));
        }
        editAuthor.setText(originalBook.getAuthor());

        //Set listener
        editTitle.setOnFocusChangeListener(getTitleListener(title, editTitle, Objects.requireNonNull(book.getValue()).getPart()));
        editPart.setOnFocusChangeListener(getPartListener(title, editPart, partLayout));
        editAuthor.setOnFocusChangeListener(getAuthorListener(author, editAuthor));

        submit.setOnClickListener(v -> {
            Book newBook = book.getValue().createClone();
            globalViewModel.updateBook(newBook);
            originalBook = newBook;
            submit.setEnabled(false);
            new BackgroundTask(() -> {
                AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "books").build();
                db.bookDao().update(newBook);
            }).start();

            InformationDialog dialog = new InformationDialog("Gespeichert", "Die Daten wurden übernommen.");
            dialog.show(getParentFragmentManager(), "saved");
        });

        return root;
    }


    private Book loadBook(GlobalViewModel globalViewModel) {
        if (getArguments() != null) {
            String isbn = Optional.ofNullable(getArguments().getString("isbn")).orElse("No Isbn");
            return globalViewModel.getBooks().stream()
                    .filter(el -> el.sameIsbn(isbn))
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
                    changed();
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
                        changed();
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
                    changed();
                }
            }
        };
    }

    private void changed() {
        submit.setEnabled(!originalBook.equals(book.getValue()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}