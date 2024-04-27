package io.skyvoli.goodbooks.global;

import android.content.Context;

import androidx.databinding.ObservableList;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import java.util.List;

import io.skyvoli.goodbooks.storage.FileStorage;
import io.skyvoli.goodbooks.storage.database.AppDatabase;
import io.skyvoli.goodbooks.storage.database.dto.Book;

public class GlobalController {

    private final GlobalViewModel globalViewModel;
    private static final String DATABASE_NAME = "books";


    public GlobalController(FragmentActivity activity) {
        globalViewModel = new ViewModelProvider(activity).get(GlobalViewModel.class);
    }

    public void setListWithDataFromDatabase(Context context) {
        List<Book> books = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build()
                .bookDao().getAll();
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        books.forEach((book -> book.setCover(fileStorage.getImage(book.getIsbn()))));
        globalViewModel.setList(books);
    }

    public void addBook(Book book, Context context) {
        globalViewModel.addBook(book);
        Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build()
                .bookDao().insert(book);
        book.getCover().ifPresent(cover -> new FileStorage(context.getFilesDir()).saveImage(book.getIsbn(), cover));
    }

    public void updateBook(Book book, Context context) {
        updateBookWithCover(book, context);
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build();
        db.bookDao().update(book);
    }

    public void updateBookWithCover(Book book, Context context) {
        book.getCover().ifPresent(drawable -> new FileStorage(context.getFilesDir()).saveImage(book.getIsbn(), drawable));
        globalViewModel.updateBook(book);
    }

    public void removeBook(Book book, Context context) {
        globalViewModel.removeBook(book);
        Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build()
                .bookDao().delete(book);
        new FileStorage(context.getFilesDir()).deleteImage(book.getIsbn());
    }

    public Book getBook(String isbn) {
        return globalViewModel.getBooks().stream()
                .filter(el -> el.sameIsbn(isbn))
                .findAny()
                .orElse(new Book("Not found"));
    }

    public ObservableList<Book> getBooks() {
        return globalViewModel.getBooks();
    }

    public boolean hasBook(String isbn) {
        return globalViewModel.hasBook(isbn);
    }

    public void sort() {
        globalViewModel.sort();
    }
}