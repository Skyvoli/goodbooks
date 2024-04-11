package io.skyvoli.goodbooks.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GlobalViewModel extends ViewModel {

    private final MutableLiveData<Set<Book>> books;

    public GlobalViewModel() {
        books = new MutableLiveData<>();
        books.setValue(new HashSet<>());
    }

    public LiveData<Set<Book>> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        Objects.requireNonNull(books.getValue()).add(book);
    }

    public void setBooks(Set<Book> books) {
        this.books.setValue(books);
    }

    public boolean hasBook(String isbn) {
        return Objects.requireNonNull(books.getValue()).contains(new Book("", isbn, "", null, true));
    }
}