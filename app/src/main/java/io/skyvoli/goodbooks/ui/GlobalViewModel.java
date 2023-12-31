package io.skyvoli.goodbooks.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.skyvoli.goodbooks.dto.Book;

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

    public boolean hasBook(String text) {
        return Objects.requireNonNull(books.getValue()).contains(new Book("", text));
    }
}
