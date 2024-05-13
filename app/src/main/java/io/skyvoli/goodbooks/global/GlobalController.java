package io.skyvoli.goodbooks.global;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.databinding.ObservableList;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.skyvoli.goodbooks.storage.FileStorage;
import io.skyvoli.goodbooks.storage.database.AppDatabase;
import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.storage.database.dto.Series;
import io.skyvoli.goodbooks.storage.database.entities.BookEntity;
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
        List<SeriesEntity> found = seriesExists(book.getTitle());
        if (!found.isEmpty()) {
            //Series already exists
            if (found.size() == 1) {
                return found.get(0).getSeriesId();
            }
            //TODO Test
            List<BookEntity> hits = found.stream()
                    .map(seriesEntity -> db.bookDao().getBooksFromSeries(seriesEntity.getSeriesId()).get(0))
                    .filter(bookEntity -> bookEntity.getAuthor().equalsIgnoreCase(book.getAuthor()))
                    .collect(Collectors.toList());

            if (hits.isEmpty()) {
                return found.get(0).getSeriesId();
            }

            return hits.get(0).getSeriesId();
        }

        //New series
        Series newSeries = new Series(0, book.getTitle(), book.getNullableCover(), 1);
        long seriesId = db.seriesDao().insert(newSeries.getEntity());
        newSeries.setSeriesId(seriesId);
        globalViewModel.addSeries(newSeries);
        return seriesId;
    }

    public void updateBook(Book book, Context context) {
        long oldId = book.getSeriesId();
        updateBookWithCover(book, context);

        if (db.seriesDao().getCountOfSeries(oldId) <= 1) {
            List<SeriesEntity> found = seriesExists(book.getTitle());
            if (!found.isEmpty()) {
                //Change reference and delete old
                book.setSeriesId(found.get(0).getSeriesId());
                db.bookDao().update(book.getEntity());
                deleteSeries(oldId);
            } else {
                //"Renaming"
                SeriesEntity series = db.seriesDao().getSeriesById(oldId);
                series.setTitle(book.getTitle());
                db.seriesDao().update(series);
            }
        } else {
            //TODO update old series (counted)
            long id = getOrCreateSeries(book);
            book.setSeriesId(id);
            db.bookDao().update(book.getEntity());
        }
    }

    private List<SeriesEntity> seriesExists(String title) {
        return db.seriesDao().getSeriesDtoByTitle(title);
    }

    public void updateBookWithCover(Book book, Context context) {
        book.getCover().ifPresent(drawable -> new FileStorage(context.getFilesDir()).saveImage(book.getIsbn(), drawable));
        globalViewModel.updateBook(book);
    }

    public void removeBook(Book book, Context context) {
        globalViewModel.removeBook(book);
        db.bookDao().delete(book.getEntity());
        new FileStorage(context.getFilesDir()).deleteImage(book.getIsbn());

        deleteSeries(book.getSeriesId());
    }

    private void deleteSeries(long seriesId) {
        if (db.seriesDao().getCountOfSeries(seriesId) == 0) {
            //TODO better + in viewModel as well
            db.seriesDao().delete(seriesId);
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


    public List<Book> loadBooksFromDb(Context context) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());

        List<Book> books = db.bookDao().getAll()
                .stream().map(bookEntity -> getEntityAsBook(bookEntity, fileStorage)).collect(Collectors.toList());
        globalViewModel.setBooks(books);
        return books;
    }

    public List<Series> loadSeriesFromDb(Context context) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        List<Series> series = db.seriesDao().getSeries();
        series.forEach(series1 -> {
            BookEntity first = db.bookDao().getBooksFromSeries(series1.getSeriesId()).get(0);
            series1.setCover(loadImage(fileStorage, first.getIsbn()));
        });

        globalViewModel.setSeries(series);
        return series;
    }

    public List<Book> getBooksFromSeries(Context context, long seriesId) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        return db.bookDao().getBooksFromSeries(seriesId)
                .stream().map(bookEntity -> getEntityAsBook(bookEntity, fileStorage)).collect(Collectors.toList());
    }

    public Book getBookByIsbn(Context context, String isbn) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        BookEntity bookEntity = db.bookDao().getBookByIsbn(isbn);
        return getEntityAsBook(bookEntity, fileStorage);
    }

    public ObservableList<Series> getSeries() {
        return globalViewModel.getSeries();
    }

    public void sort() {
        globalViewModel.sort();
    }

    private Book getEntityAsBook(BookEntity bookEntity, FileStorage fileStorage) {
        return new Book(bookEntity.getTitle(), bookEntity.getSubtitle(),
                bookEntity.getPart(), bookEntity.getIsbn(),
                bookEntity.getAuthor(),
                loadImage(fileStorage, bookEntity.getIsbn()),
                bookEntity.isResolved(),
                bookEntity.getSeriesId());
    }

    private Drawable loadImage(FileStorage fileStorage, String isbn) {
        Optional<Drawable> cached = globalViewModel.getDrawable(isbn);
        return cached.orElseGet(() -> {
            Drawable cover = fileStorage.getImage(isbn);
            globalViewModel.addDrawable(isbn, cover);
            return cover;
        });
    }
}