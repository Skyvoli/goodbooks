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

    private final AppDatabase db;


    public GlobalController(FragmentActivity activity) {
        globalViewModel = new ViewModelProvider(activity).get(GlobalViewModel.class);
        db = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
    }

    public void setListsWithDataFromDatabase(Context context) {
        List<Book> books = db.bookDao().getAll();
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        books.forEach((book -> book.setCover(fileStorage.getImage(book.getIsbn()))));
        globalViewModel.setBooks(books);

        globalViewModel.setSeries(createSeries(books));
    }

    private List<Series> createSeries(List<Book> books) {
        List<Series> seriesList = db.seriesDao().getSeriesDto();

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
        db.bookDao().insert(book);
        book.getCover().ifPresent(cover -> new FileStorage(context.getFilesDir()).saveImage(book.getIsbn(), cover));
        createNewSeries(book);
    }

    private void createNewSeries(Book book) {
        if (seriesExists(book.getTitle())) {
            return;
        }

        db.seriesDao().insert(
                new Series(book.getTitle(), book.getNullableCover(), book.getAuthor(), 1));
        globalViewModel.setSeries(db.seriesDao().getSeriesDto());
    }

    public void updateBook(Book book, String previousTitle, Context context) {
        updateBookWithCover(book, context);
        db.bookDao().update(book);

        if (db.seriesDao().getCountOfSeries(previousTitle) == 0) {
            if (seriesExists(book.getTitle())) {
                //No renaming needed
                deleteSeries(previousTitle);
            } else {
                //"Renaming"
                Series series = db.seriesDao().getSeriesDtoByTitle(previousTitle).get(0);
                series.setTitle(book.getTitle());
                series.setAuthor(book.getAuthor());
                db.seriesDao().update(series);
            }
            return;
        }
        createNewSeries(book);
    }

    private boolean seriesExists(String title) {
        return db.seriesDao().getSeriesDto()
                .stream().anyMatch(series -> series.getTitle().equalsIgnoreCase(title));
    }

    public void updateBookWithCover(Book book, Context context) {
        book.getCover().ifPresent(drawable -> new FileStorage(context.getFilesDir()).saveImage(book.getIsbn(), drawable));
        globalViewModel.updateBook(book);
    }

    public void removeBook(Book book, Context context) {
        globalViewModel.removeBook(book);
        db.bookDao().delete(book);
        new FileStorage(context.getFilesDir()).deleteImage(book.getIsbn());

        deleteSeries(book.getTitle());
    }

    private void deleteSeries(String title) {
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