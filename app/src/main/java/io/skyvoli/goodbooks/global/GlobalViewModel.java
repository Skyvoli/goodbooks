package io.skyvoli.goodbooks.global;


import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.storage.database.dto.Series;

public class GlobalViewModel extends ViewModel {

    private ObservableList<Book> books;
    private ObservableList<Series> series;
    private final Map<String, Drawable> drawables;
    private final String logTag = getClass().getSimpleName();

    protected GlobalViewModel() {
        books = new ObservableArrayList<>();
        series = new ObservableArrayList<>();
        drawables = new HashMap<>();
    }

    protected ObservableList<Book> getBooks() {
        return books;
    }

    protected ObservableList<Series> getSeries() {
        return series;
    }

    protected void addBook(Book book) {
        books.add(book);
        drawables.put(book.getIsbn(), book.getNullableCover());
        sort();
    }

    protected void updateBook(Book newBook) {
        Optional<Book> found = books.stream()
                .filter(el -> el.sameIsbn(newBook.getIsbn()))
                .findFirst();

        if (!found.isPresent()) {
            //Series probably not filled with data yet
            Log.w(logTag, "Book not found.");
            return;
        }

        books.set(books.indexOf(found.get()), newBook);
        drawables.put(newBook.getIsbn(), newBook.getNullableCover());

        sort();
    }

    public void updateSeries(Series seriesNew) {
        Optional<Series> found = series.stream()
                .filter(series1 -> series1.sameIdAs(seriesNew))
                .findFirst();

        if (!found.isPresent()) {
            //Series probably not filled with data yet
            Log.w(logTag, "Book not found.");
            return;
        }

        series.set(series.indexOf(found.get()), seriesNew);
    }

    protected void setBooks(List<Book> books) {
        this.books = new ObservableArrayList<>();
        this.books.addAll(books);
        books.forEach(book -> drawables.put(book.getIsbn(), book.getNullableCover()));
    }

    protected void setSeries(List<Series> series) {
        this.series = new ObservableArrayList<>();
        this.series.addAll(series);
    }

    protected void addSeries(Series series) {
        this.series.add(series);
        sortSeries();
    }

    protected void sort() {
        books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER)
                .thenComparing((b1, b2) -> b1.comparePart(b2.getPart())));
    }

    protected Optional<Drawable> getDrawable(String isbn) {
        return Optional.ofNullable(drawables.get(isbn));
    }

    private void sortSeries() {
        series.sort(Comparator.comparing(Series::getTitle));
    }

    protected void removeBook(Book book) {
        books.remove(book);
    }

    public void removeSeries(long seriesId) {
        series.removeIf(series1 -> series1.getSeriesId() == seriesId);
    }

    public void addDrawable(String isbn, Drawable cover) {
        drawables.put(isbn, cover);
    }
}