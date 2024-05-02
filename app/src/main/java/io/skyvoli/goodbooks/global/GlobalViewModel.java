package io.skyvoli.goodbooks.global;


import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModel;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.dto.Book;
import io.skyvoli.goodbooks.ui.fragments.series.Series;

public class GlobalViewModel extends ViewModel {

    private ObservableList<Book> books;
    private ObservableList<Series> series;

    protected GlobalViewModel() {
        books = new ObservableArrayList<>();
        series = new ObservableArrayList<>();
    }

    protected ObservableList<Book> getBooks() {
        return books;
    }

    protected ObservableList<Series> getSeries() {
        return series;
    }

    protected void addBook(Book book) {
        books.add(book);
        sort();
    }

    protected void updateBook(Book newBook) {
        Optional<Book> found = books.stream()
                .filter(el -> el.sameIsbn(newBook.getIsbn()))
                .findFirst();

        if (!found.isPresent()) {
            throw new IllegalStateException("Existing book not found");
        }

        books.set(books.indexOf(found.get()), newBook);
    }

    protected void setBooks(List<Book> books) {
        this.books = new ObservableArrayList<>();
        this.books.addAll(books);
    }

    protected void setSeries(List<Series> series) {
        this.series = new ObservableArrayList<>();
        this.series.addAll(series);
    }

    protected boolean hasBook(String isbn) {
        return books.stream().anyMatch(book -> book.sameIsbn(isbn));
    }

    protected void sort() {
        books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER)
                .thenComparing((b1, b2) -> b1.comparePart(b2.getPart())));
    }

    protected void removeBook(Book book) {
        books.remove(book);
    }
}