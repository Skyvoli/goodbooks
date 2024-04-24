package io.skyvoli.goodbooks.model;


import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModel;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import io.skyvoli.goodbooks.storage.database.dto.Book;

public class GlobalViewModel extends ViewModel {

    private ObservableList<Book> books;

    public GlobalViewModel() {
        books = new ObservableArrayList<>();
    }

    public ObservableList<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
        sort();
    }

    public void updateBook(Book newBook) {
        Optional<Book> found = books.stream()
                .filter(el -> el.sameIsbn(newBook.getIsbn()))
                .findFirst();

        if (!found.isPresent()) {
            throw new IllegalStateException("Existing book not found");
        }

        books.set(books.indexOf(found.get()), newBook);
        sort();
    }

    public void clearBooks() {
        this.books.clear();
    }

    public void setList(List<Book> books) {
        this.books = new ObservableArrayList<>();
        this.books.addAll(books);
    }

    public boolean hasBook(String isbn) {
        return books.stream().anyMatch(book -> book.sameIsbn(isbn));
    }

    private void sort() {
        books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER).thenComparing(Book::getPart));
    }

    public void removeBook(Book book) {
        books.remove(book);
    }
}