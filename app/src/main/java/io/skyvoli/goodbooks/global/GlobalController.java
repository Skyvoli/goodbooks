package io.skyvoli.goodbooks.global;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.databinding.ObservableList;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.storage.FileStorage;
import io.skyvoli.goodbooks.storage.database.AppDatabase;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.storage.database.dto.Series;
import io.skyvoli.goodbooks.storage.database.entities.SeriesEntity;

public class GlobalController {

    private final GlobalViewModel globalViewModel;
    private static final String DATABASE_NAME = "books";

    private final AppDatabase db;

    public GlobalController(FragmentActivity activity) {
        globalViewModel = new ViewModelProvider(activity).get(GlobalViewModel.class);
        db = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .build();
    }

    public void addBook(Book book, Context context) {
        book.setSeriesId(getOrCreateSeries(book));
        globalViewModel.addBook(book);
        db.bookDao().insert(book.getEntity());
        book.getCover().ifPresent(cover -> new FileStorage(context.getFilesDir()).saveImage(book.getIsbn(), cover));
    }

    private long getOrCreateSeries(Book book) {
        List<SeriesEntity> found = db.seriesDao().getSeriesDtoByTitle(book.getTitle());
        if (!found.isEmpty()) {
            //Only 1 series exists with that name
            if (found.size() == 1) {
                return found.get(0).getSeriesId();
            }

            List<Book> hits = found.stream()
                    .map(seriesEntity -> db.bookDao().getBooksFromSeries(seriesEntity.getSeriesId()).get(0))
                    .filter(bookEntity -> bookEntity.getAuthor().equalsIgnoreCase(book.getAuthor()))
                    .collect(Collectors.toList());

            if (hits.isEmpty()) {
                return found.get(0).getSeriesId();
            }

            return hits.get(0).getSeriesId();
        }

        //New series
        return createNewSeries(book);
    }

    private long createNewSeries(Book book) {
        Series newSeries = new Series(0, book.getTitle(), book.getNullableCover(), 1);
        long seriesId = db.seriesDao().insert(newSeries.getEntity());
        newSeries.setSeriesId(seriesId);
        globalViewModel.addSeries(newSeries);
        return seriesId;
    }

    public void updateBook(Book book, Context context) {
        long oldId = book.getSeriesId();

        if (db.seriesDao().getCountOfSeries(oldId) <= 1) {
            List<SeriesEntity> found = db.seriesDao().getSeriesDtoByTitle(book.getTitle());
            if (found.isEmpty()) {
                //Renaming series
                db.bookDao().update(book.getEntity());
                SeriesEntity series = db.seriesDao().getSeriesEntityById(oldId);
                series.setTitle(book.getTitle());
                db.seriesDao().update(series);
            } else {
                //Change reference and delete old
                book.setSeriesId(found.get(0).getSeriesId());
                db.bookDao().update(book.getEntity());
                removeSeries(oldId);
                //Update new series
                updateSeries(book.getSeriesId(), context);
            }
        } else {
            book.setSeriesId(getOrCreateSeries(book));
            db.bookDao().update(book.getEntity());
            //Update both series
            updateSeries(oldId, context);
            updateSeries(book.getSeriesId(), context);
        }

        updateCoverOfBook(book, context);
    }

    private void updateSeries(long seriesId, Context context) {
        Series series = db.seriesDao().getSeriesById(seriesId);
        Book first = db.bookDao().getBooksFromSeries(series.getSeriesId()).get(0);
        series.setCover(loadImage(new FileStorage(context.getFilesDir()), first.getIsbn()));
        globalViewModel.updateSeries(series);
    }

    public void updateCoverOfBook(Book book, Context context) {
        book.getCover().ifPresent(drawable -> new FileStorage(context.getFilesDir()).saveImage(book.getIsbn(), drawable));
        globalViewModel.updateBook(book);
    }

    public void removeBook(Book book, Context context) {
        globalViewModel.removeBook(book);
        db.bookDao().delete(book.getEntity());
        new FileStorage(context.getFilesDir()).deleteImage(book.getIsbn());

        removeSeries(book.getSeriesId());
    }

    private void removeSeries(long seriesId) {
        if (db.seriesDao().getCountOfSeries(seriesId) == 0) {
            globalViewModel.removeSeries(seriesId);
            db.seriesDao().delete(seriesId);
        }
    }

    public Optional<Book> getBook(Context context, String isbn) {
        Optional<Book> found = Optional.ofNullable(db.bookDao().getBookByIsbn(isbn));
        found.ifPresent(book -> book.setCover(loadImage(new FileStorage(context.getFilesDir()), isbn)));
        return found;
    }

    public ObservableList<Book> getBooks() {
        return globalViewModel.getBooks();
    }


    public List<Book> loadBooksFromDb(Context context) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());

        List<Book> books = new ArrayList<>();

        db.bookDao().getAll().forEach(book -> {
            book.setCover(loadImage(fileStorage, book.getIsbn()));
            books.add(book);
        });

        globalViewModel.setBooks(books);
        return books;
    }

    public List<Series> loadSeriesFromDb(Context context) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        List<Series> series = db.seriesDao().getSeries();
        series.forEach(series1 -> {
            Book first = db.bookDao().getBooksFromSeries(series1.getSeriesId()).get(0);
            series1.setCover(loadImage(fileStorage, first.getIsbn()));
        });

        globalViewModel.setSeries(series);
        return series;
    }

    public List<Book> getBooksFromSeries(Context context, long seriesId) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());

        List<Book> books = new ArrayList<>();

        db.bookDao().getBooksFromSeries(seriesId).forEach(book -> {
            book.setCover(loadImage(fileStorage, book.getIsbn()));
            books.add(book);
        });

        return books;
    }

    public Book getBookByIsbn(Context context, String isbn) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        Book book = db.bookDao().getBookByIsbn(isbn);
        book.setCover(loadImage(fileStorage, book.getIsbn()));
        return book;
    }

    public ObservableList<Series> getSeries() {
        return globalViewModel.getSeries();
    }

    public void sort() {
        globalViewModel.sortBooks();
    }

    public Drawable loadImage(FileStorage fileStorage, String isbn) {
        Optional<Drawable> cached = globalViewModel.getDrawable(isbn);
        return cached.orElseGet(() -> {
            Drawable cover = fileStorage.getImage(isbn);
            globalViewModel.addDrawable(isbn, cover);
            return cover;
        });
    }
}