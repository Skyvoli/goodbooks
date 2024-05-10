package io.skyvoli.goodbooks.global;

import android.content.Context;

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

    public void updateBook(Book book, String previousTitle, Context context) {
        updateBookWithCover(book, context);

        if (db.seriesDao().getCountOfSeries(previousTitle) <= 1) {
            List<SeriesEntity> found = seriesExists(book.getTitle());
            if (!found.isEmpty()) {
                //Change reference and delete old
                //TODO by id
                book.setSeriesId(found.get(0).getSeriesId());
                db.bookDao().update(book.getEntity());
                deleteSeries(previousTitle);
            } else {
                //"Renaming"
                SeriesEntity series = db.seriesDao().getSeriesDtoByTitle(previousTitle).get(0);
                series.setTitle(book.getTitle());
                db.seriesDao().update(series);
            }
        } else {
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

        deleteSeries(book.getTitle());
    }

    private void deleteSeries(String title) {
        if (db.seriesDao().getCountOfSeries(title) == 0) {
            //TODO better + in viewModel as well
            db.seriesDao().delete(db.seriesDao().getSeriesDtoByTitle(title).get(0));
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
                .stream().map(bookEntity -> new Book(bookEntity.getTitle(), bookEntity.getSubtitle(),
                        bookEntity.getPart(), bookEntity.getIsbn(),
                        bookEntity.getAuthor(),
                        fileStorage.getImage(bookEntity.getIsbn()),
                        bookEntity.isResolved())).collect(Collectors.toList());
        globalViewModel.setBooks(books);
        return books;
    }

    public List<Series> loadSeriesFromDb(Context context) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        List<Series> series = db.seriesDao().getSeries();
        series.forEach(series1 -> {
            BookEntity first = db.bookDao().getBooksFromSeries(series1.getSeriesId()).get(0);
            series1.setCover(fileStorage.getImage(first.getIsbn()));
        });

        globalViewModel.setSeries(series);
        return series;
    }

    public List<Book> getBooksFromSeries(Context context, long seriesId) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        return db.bookDao().getBooksFromSeries(seriesId)
                .stream().map(bookEntity -> new Book(bookEntity.getTitle(), bookEntity.getSubtitle(),
                        bookEntity.getPart(), bookEntity.getIsbn(),
                        bookEntity.getAuthor(),
                        fileStorage.getImage(bookEntity.getIsbn()),
                        bookEntity.isResolved())).collect(Collectors.toList());
    }

    public Book getBookByIsbn(Context context, String isbn) {
        FileStorage fileStorage = new FileStorage(context.getFilesDir());
        BookEntity bookEntity = db.bookDao().getBookByIsbn(isbn);
        return new Book(bookEntity.getTitle(), bookEntity.getSubtitle(),
                bookEntity.getPart(), bookEntity.getIsbn(),
                bookEntity.getAuthor(),
                fileStorage.getImage(bookEntity.getIsbn()),
                bookEntity.isResolved());
    }

    public ObservableList<Series> getSeries() {
        return globalViewModel.getSeries();
    }

    public void sort() {
        globalViewModel.sort();
    }
}