package io.skyvoli.goodbooks.ui.fragments.bookdetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jsoup.internal.StringUtil;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.skyvoli.goodbooks.R;
import io.skyvoli.goodbooks.databinding.FragmentBookDetailBinding;
import io.skyvoli.goodbooks.dialog.InformationDialog;
import io.skyvoli.goodbooks.dialog.NoticeDialogListener;
import io.skyvoli.goodbooks.dialog.OnlyPositiveListener;
import io.skyvoli.goodbooks.dialog.PermissionDialog;
import io.skyvoli.goodbooks.global.GlobalController;
import io.skyvoli.goodbooks.helper.TitleBuilder;
import io.skyvoli.goodbooks.storage.FileStorage;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.web.BookResolver;

public class BookDetailFragment extends Fragment {

    private static final String PICKER_TAG = "PhotoPicker";
    private FragmentBookDetailBinding binding;
    private Book originalBook;
    private Book copiedBook;
    private Button submit;
    private ImageView cover;
    private GlobalController globalController;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri == null) {
                return;
            }

            requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ImageDecoder.Source source = ImageDecoder.createSource(requireContext().getContentResolver(), uri);
            Drawable newCover;
            try {
                newCover = ImageDecoder.decodeDrawable(source);
            } catch (IOException e) {
                new InformationDialog("Fehler", "Das Foto konnte nicht geladen werden")
                        .show(getParentFragmentManager(), PICKER_TAG);
                return;
            }
            setCover(newCover);
            copiedBook.setCover(newCover);
            changed();
        });
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO Color palette
        binding = FragmentBookDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        globalController = new GlobalController(requireActivity());
        requireActivity().addMenuProvider(getMenuProvider(), getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        final TextView title = binding.title;
        cover = binding.includeCard.cover;
        final TextView isbn = binding.includeCard.isbn;
        final TextView author = binding.includeCard.author;
        final TextInputLayout titleLayout = binding.titleLayout;
        final TextInputLayout partLayout = binding.partLayout;
        final TextInputLayout authorLayout = binding.authorLayout;
        final TextInputEditText editTitle = binding.editTitle;
        final TextInputEditText editSubtitle = binding.editSubtitle;
        final EditText editPart = binding.editPart;
        final TextInputEditText editAuthor = binding.editAuthor;
        final FloatingActionButton galleryButton = binding.includeCard.floatingActionButton;
        submit = binding.submitChanges;
        originalBook = loadBook();
        copiedBook = originalBook.createClone();

        //Set content & observables
        title.setText(TitleBuilder.buildWholeTitle(originalBook.getTitle(), originalBook.getSubtitle(), originalBook.getPart()));

        Drawable drawable = originalBook.getCover()
                .orElseGet(() -> ContextCompat.getDrawable(requireContext(), R.drawable.ruby));
        setCover(Objects.requireNonNull(drawable));

        isbn.setText(originalBook.getIsbn());
        author.setText(originalBook.getAuthor());

        //Set editable
        editTitle.setText(originalBook.getTitle());

        Optional<String> subtitle = Optional.ofNullable(originalBook.getSubtitle());
        subtitle.ifPresent(editSubtitle::setText);

        Optional<Integer> part = Optional.ofNullable(originalBook.getPart());
        part.ifPresent(integer -> editPart.setText(String.valueOf(integer)));

        editAuthor.setText(originalBook.getAuthor());

        //Set listener
        editTitle.setOnFocusChangeListener(getTitleListener(editTitle, titleLayout));
        editSubtitle.setOnFocusChangeListener(getSubtitleListener(editSubtitle));
        editPart.setOnFocusChangeListener(getPartListener(editPart, partLayout));
        editAuthor.setOnFocusChangeListener(getAuthorListener(editAuthor, authorLayout));

        submit.setOnClickListener(v -> {
            Book newBook = copiedBook.createClone();
            newBook.setResolved(true);
            new Thread(() -> globalController.updateBook(newBook, requireContext())).start();
            globalController.sort();
            originalBook = newBook;
            //Refresh
            title.setText(TitleBuilder.buildWholeTitle(originalBook.getTitle(), originalBook.getSubtitle(), originalBook.getPart()));
            author.setText(originalBook.getAuthor());
            editTitle.setText(originalBook.getTitle());
            editAuthor.setText(originalBook.getAuthor());
            submit.setEnabled(false);

            new InformationDialog("Gespeichert", "Die Daten wurden übernommen.")
                    .show(getParentFragmentManager(), "saved");
        });

        galleryButton.setOnClickListener(this::launchPicker);

        return root;
    }

    private void setCover(Drawable drawable) {
        ((ConstraintLayout.LayoutParams) cover.getLayoutParams()).dimensionRatio
                = String.valueOf(getRatio(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
        cover.setImageDrawable(drawable);
    }

    private float getRatio(int width, int height) {
        if (height != 0) {
            return (float) width / height;
        } else {
            return 2.3f; // Handle divide by zero case
        }
    }

    private MenuProvider getMenuProvider() {
        return new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.book_detail_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int selectedItemId = menuItem.getItemId();
                if (selectedItemId == R.id.delete_item) {
                    new PermissionDialog("Buch löschen",
                            "Möchten Sie '" + TitleBuilder.buildWholeTitle(originalBook.getTitle(),
                                    originalBook.getSubtitle(),
                                    originalBook.getPart()) + "'  wirklich löschen?",
                            true,
                            deleteBookListener()).show(getParentFragmentManager(), "deleteAction");
                    return true;
                } else if (selectedItemId == R.id.reset_image) {
                    new PermissionDialog("Bild zurücksetzen",
                            "Möchten Sie das Bild zurücksetzen?",
                            true,
                            resetImageListener()).show(getParentFragmentManager(), "deleteAction");
                }
                return false;
            }
        };
    }


    private Book loadBook() {
        if (getArguments() == null) {
            throw new IllegalStateException("Missing argument isbn");
        }
        String isbn = Optional.ofNullable(getArguments().getString("isbn"))
                .orElseThrow(() -> new IllegalStateException("Missing argument isbn"));

        return globalController.getBook(isbn);
    }

    private View.OnFocusChangeListener getTitleListener(TextInputEditText editTitle, TextInputLayout titleLayout) {
        //TODO if other books with same title ask to load author and subttitle
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

    private View.OnFocusChangeListener getSubtitleListener(TextInputEditText editSubtitle) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newSubtitle = editSubtitle.getText();
                if (newSubtitle != null) {
                    String formatted = formatString(newSubtitle.toString());
                    if (formatted.length() == 0) {
                        copiedBook.setSubtitle(null);
                    } else {
                        copiedBook.setSubtitle(formatted);
                    }
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
                    copiedBook.setPart(null);
                    changed();
                }
            }
        };
    }

    private View.OnFocusChangeListener getAuthorListener(TextInputEditText editAuthor, TextInputLayout authorLayout) {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                Editable newAuthor = editAuthor.getText();
                if (newAuthor != null) {
                    String formatted = formatAuthor(newAuthor.toString());

                    if (formatted.length() == 0) {
                        authorLayout.setError("Bitte geben Sie einen Autor ein.");
                        return;
                    }

                    authorLayout.setError(null);
                    copiedBook.setAuthor(formatted);
                    changed();
                }
            }
        };
    }

    private String formatAuthor(String unformatted) {
        //\n shouldn't be completely removed
        return unformatted.trim()
                .replaceAll(" {2,}", " ")
                .replaceAll("\n{2,}", "\n")
                .replace("\n ", "\n");
    }

    private String formatString(String unformatted) {
        return StringUtil.normaliseWhitespace(unformatted).trim();
    }

    private void changed() {
        submit.setEnabled(!originalBook.equals(copiedBook));
    }

    private NoticeDialogListener deleteBookListener() {
        return new OnlyPositiveListener() {
            @Override
            public void onDialogPositiveClick() {
                new Thread(() ->
                        globalController.removeBook(originalBook, requireContext())).start();
                getParentFragmentManager().popBackStack();
            }
        };
    }

    private NoticeDialogListener resetImageListener() {
        return new OnlyPositiveListener() {
            @Override
            public void onDialogPositiveClick() {
                Context context = requireContext();
                new Thread(() -> {
                    Optional<Drawable> baseCover = new BookResolver().loadImage(originalBook.getIsbn(), 15);
                    if (baseCover.isPresent()) {
                        Drawable resetImage = baseCover.get();
                        new FileStorage(context.getFilesDir()).saveImage(originalBook.getIsbn(), resetImage);
                        originalBook.setCover(resetImage);
                        globalController.updateBookWithCover(originalBook, context);
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> setCover(resetImage));
                        }
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(context, "Bild konnte nicht geladen werden", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        };
    }

    private void launchPicker(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}