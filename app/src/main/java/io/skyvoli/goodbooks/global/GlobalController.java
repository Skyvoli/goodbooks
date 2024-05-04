package io.skyvoli.goodbooks.global;

import android.content.Context;

import androidx.databinding.ObservableList;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import java.util.List;
import java.util.Optional;

import io.skyvoli.goodbooks.storage.FileStorage;
import io.skyvoli.goodbooks.storage.database.AppDatabase;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.storage.database.dto.Series;

public class GlobalController {

    private final GlobalViewModel globalViewModel;
    private static final String DATABASE_NAME = "books";


    public GlobalController(FragmentActivity activity) {
        globalViewModel = new ViewModelProvider(activity).get(GlobalViewModel.class);
    }

    public void setListsWithDataFromDatabase(Context context) {
        List<Book> books = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build()
                .bookDao().getAll();
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        books.forEach((book -> book.setCover(fileStorage.getImage(book.getIsbn()))));
        globalViewModel.setBooks(books);

        globalViewModel.setSeries(createSeries(context, books));
    }

    private List<Series> createSeries(Context context, List<Book> books) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build();
        List<Series> seriesList = db.seriesDao().getSeries();

        seriesList.forEach(series -> {
            series.setCover(books.stream()
                    .filter(book -> book.getTitle().equalsIgnoreCase(series.getTitle()))
                    .findFirst()
                    .orElse(new Book("")).getNullableCover());
            series.setCountedBooks(db.seriesDao().getCountOfSeries(series.getTitle()));
        });

        return seriesList;
    }

    public void addBook(Book book, Context context) {
        globalViewModel.addBook(book);
        Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build()
                .bookDao().insert(book);
        book.getCover().ifPresent(cover -> new FileStorage(context.getFilesDir()).saveImage(book.getIsbn(), cover));
        addSeries(book, context);
    }

    private void addSeries(Book book, Context context) {
        if (Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build().seriesDao().getSeriesDto()
                .stream().anyMatch(series -> series.getTitle().equalsIgnoreCase(book.getTitle()))) {
            //&& series.getAuthor().equalsIgnoreCase(book.getAuthor()))) {
            return;
        }

        Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build().seriesDao()
                .insert(new Series(0, book.getTitle(), book.getNullableCover(), book.getAuthor(), 1));
    }

    public void updateBook(Book book, String previousTitle, Context context) {
        updateBookWithCover(book, context);
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build();
        db.bookDao().update(book);
        if (db.seriesDao().getCountOfSeries(previousTitle) == 0) {
            if (Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build().seriesDao().getSeriesDto()
                    .stream().anyMatch(series -> series.getTitle().equalsIgnoreCase(book.getTitle()))) {
                deleteSeries(db, previousTitle);
                return;
            }
            Series series = db.seriesDao().getSeriesDtoByTitle(previousTitle).get(0);
            series.setTitle(book.getTitle());
            series.setAuthor(book.getAuthor());
            db.seriesDao().update(series);
            return;
        }
        addSeries(book, context);
    }

    public void updateBookWithCover(Book book, Context context) {
        book.getCover().ifPresent(drawable -> new FileStorage(context.getFilesDir()).saveImage(book.getIsbn(), drawable));
        globalViewModel.updateBook(book);
    }

    public void removeBook(Book book, Context context) {
        globalViewModel.removeBook(book);
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build();
        db.bookDao().delete(book);
        new FileStorage(context.getFilesDir()).deleteImage(book.getIsbn());

        deleteSeries(db, book.getTitle());
    }

    private void deleteSeries(AppDatabase db, String title) {
        if (db.seriesDao().getCountOfSeries(title) == 0) {
            Series series = db.seriesDao().getSeriesDtoByTitle(title).get(0);
            db.seriesDao().delete(series);
        }
    }

    public Optional<Book> getBook(String isbn) {
        return globalViewModel.getBooks().stream()
                .filter(el -> el.sameIsbn(isbn))
                .findAny();
    }

    public ObservableList<Book> getBooks() {
        return globalViewModel.getBooks();
    }

    public ObservableList<Series> getSeries() {
        return globalViewModel.getSeries();
    }

    public void sort() {
        globalViewModel.sort();
    }
}