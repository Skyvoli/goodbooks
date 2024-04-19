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
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentBookDetailBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.model.GlobalViewModel;
import io.skyvoli.goodbooks.storage.database.AppDatabase;
import io.skyvoli.goodbooks.storage.database.dto.Book;

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;
    private final String logTag = this.getClass().getSimpleName();
    private Book originalBook;
    private Book copiedBook;
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
        copiedBook = originalBook.createClone();

        //Set content & observables
        title.setText(buildWholeTitle(originalBook.getTitle(), originalBook.getPart()));
        Optional<Drawable> drawable = originalBook.getCover();
        if (drawable.isPresent()) {
            cover.setImageDrawable(drawable.get());
        } else {
            cover.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ruby));
        }
        isbn.setText(originalBook.getIsbn());
        author.setText(originalBook.getAuthor());

        //Set editable
        editTitle.setText(originalBook.getTitle());
        Integer part = originalBook.getPart();
        if (part != null) {
            editPart.setText(String.valueOf(part));
        }
        editAuthor.setText(originalBook.getAuthor());

        //Set listener
        editTitle.setOnFocusChangeListener(getTitleListener(editTitle));
        editPart.setOnFocusChangeListener(getPartListener(editPart, partLayout));
        editAuthor.setOnFocusChangeListener(getAuthorListener(editAuthor));

        submit.setOnClickListener(v -> {
            Book newBook = copiedBook.createClone();
            newBook.setResolved(true);
            globalViewModel.updateBook(newBook);
            originalBook = newBook;
            //Refresh
            title.setText(buildWholeTitle(originalBook.getTitle(), originalBook.getPart()));
            author.setText(originalBook.getAuthor());
            submit.setEnabled(false);
            new Thread(() -> {
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

    private View.OnFocusChangeListener getTitleListener(TextInputEditText editTitle) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newTitle = editTitle.getText();
                if (newTitle != null) {
                    copiedBook.setTitle(newTitle.toString());
                    changed();
                }
            }
        };
    }

    private View.OnFocusChangeListener getPartListener(EditText
                                                               editPart, TextInputLayout partLayout) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newPart = editPart.getText();
                partLayout.setError(null);
                try {
                    if (newPart != null) {
                        copiedBook.setPart(Integer.valueOf(newPart.toString()));
                        changed();
                    }
                } catch (NumberFormatException e) {
                    partLayout.setError("Bitte geben Sie eine Ganzzahl ein.");
                }
            }
        };
    }

    private View.OnFocusChangeListener getAuthorListener(TextInputEditText editAuthor) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newAuthor = editAuthor.getText();
                if (newAuthor != null) {
                    copiedBook.setAuthor(newAuthor.toString());
                    changed();
                }
            }
        };
    }

    private void changed() {
        submit.setEnabled(!originalBook.equals(copiedBook));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}