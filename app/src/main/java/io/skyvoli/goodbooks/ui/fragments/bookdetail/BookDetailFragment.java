package io.skyvoli.goodbooks.ui.fragments.bookdetail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jsoup.internal.StringUtil;

import java.io.File;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentBookDetailBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.dialog.NoticeDialogListener;
import io.skyvoli.goodbooks.dialog.PermissionDialog;
import io.skyvoli.goodbooks.model.GlobalViewModel;
import io.skyvoli.goodbooks.storage.FileStorage;
import io.skyvoli.goodbooks.storage.database.AppDatabase;
import io.skyvoli.goodbooks.storage.database.dto.Book;

public class BookDetailFragment extends Fragment {

    private FragmentBookDetailBinding binding;
    private GlobalViewModel globalViewModel;
    private final String logTag = this.getClass().getSimpleName();
    private Book originalBook;
    private Book copiedBook;
    private Button submit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        globalViewModel = new ViewModelProvider(requireActivity()).get(GlobalViewModel.class);

        binding = FragmentBookDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        requireActivity().addMenuProvider(getMenuProvider(), getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        final TextView title = binding.title;
        final ImageView cover = binding.cover;
        final TextView isbn = binding.isbn;
        final TextView author = binding.author;
        final TextInputLayout titleLayout = binding.titleLayout;
        final TextInputLayout partLayout = binding.partLayout;
        final TextInputLayout authorLayout = binding.authorLayout;
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
        editTitle.setOnFocusChangeListener(getTitleListener(editTitle, titleLayout));
        editPart.setOnFocusChangeListener(getPartListener(editPart, partLayout));
        editAuthor.setOnFocusChangeListener(getAuthorListener(editAuthor, authorLayout));

        submit.setOnClickListener(v -> {
            Book newBook = copiedBook.createClone();
            newBook.setResolved(true);
            globalViewModel.updateBook(newBook);
            originalBook = newBook;
            //Refresh
            title.setText(buildWholeTitle(originalBook.getTitle(), originalBook.getPart()));
            author.setText(originalBook.getAuthor());
            editTitle.setText(originalBook.getTitle());
            editAuthor.setText(originalBook.getAuthor());
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

    private MenuProvider getMenuProvider() {
        return new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.book_detail_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.android_item) {
                    PermissionDialog permissionDialog = new PermissionDialog("Buch löschen", "Möchten Sie '" + buildWholeTitle(originalBook.getTitle(), originalBook.getPart()) + "'  wirklich löschen?", true, new NoticeDialogListener() {
                        @Override
                        public void onDialogPositiveClick() {
                            File dir = requireContext().getFilesDir();
                            new Thread(() -> {
                                AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "books").build();
                                db.bookDao().delete(originalBook);
                                new FileStorage(dir).deleteImage(originalBook.getIsbn());
                            }).start();
                            globalViewModel.removeBook(originalBook);
                            getParentFragmentManager().popBackStack();
                        }

                        @Override
                        public void onDialogNegativeClick() {
                            //Do nothing
                        }
                    });
                    permissionDialog.show(getParentFragmentManager(), "deleteAction");
                    return true;
                }
                return false;
            }
        };
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

    private View.OnFocusChangeListener getTitleListener(TextInputEditText editTitle, TextInputLayout titleLayout) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newTitle = editTitle.getText();
                if (newTitle != null) {
                    String formatted = formatString(newTitle.toString());

                    if (formatted.length() == 0) {
                        titleLayout.setError("Bitte geben Sie einen Titel ein.");
                        return;
                    }

                    titleLayout.setError(null);
                    copiedBook.setTitle(formatted);
                    changed();
                }
            }
        };
    }

    private View.OnFocusChangeListener getPartListener(EditText editPart, TextInputLayout partLayout) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newPart = editPart.getText();
                try {
                    if (newPart != null) {
                        partLayout.setError(null);
                        copiedBook.setPart(Integer.valueOf(newPart.toString()));
                        changed();
                    }
                } catch (NumberFormatException e) {
                    if (newPart.toString().length() > 9) {
                        partLayout.setError("Bitte geben Sie eine kleinere Zahl ein.");
                        return;
                    }
                    partLayout.setError("Bitte geben Sie eine Ganzzahl ein.");
                }
            }
        };
    }

    private View.OnFocusChangeListener getAuthorListener(TextInputEditText editAuthor, TextInputLayout authorLayout) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newAuthor = editAuthor.getText();
                if (newAuthor != null) {
                    String formatted = formatString(newAuthor.toString());

                    if (formatted.length() == 0) {
                        authorLayout.setError("Bitte geben Sie einen Autor ein.");
                        return;
                    }

                    authorLayout.setError(null);
                    copiedBook.setAuthor(newAuthor.toString());
                    changed();
                }
            }
        };
    }

    private String formatString(String unformatted) {
        return StringUtil.normaliseWhitespace(unformatted).trim();
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