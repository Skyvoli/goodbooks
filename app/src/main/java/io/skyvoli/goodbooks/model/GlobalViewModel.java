package io.skyvoli.goodbooks.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.skyvoli.goodbooks.storage.database.dto.Book;

public class GlobalViewModel extends ViewModel {

    private final MutableLiveData<List<Book>> books;

    public GlobalViewModel() {
        books = new MutableLiveData<>();
        books.setValue(new ArrayList<>());
    }

    public LiveData<List<Book>> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        Objects.requireNonNull(books.getValue()).add(book);
    }


    public void clearBooks() {
        this.books.setValue(new ArrayList<>());
    }

    public void setBooksAsynchronous(List<Book> books) {
        this.books.postValue(books);
    }

    public boolean hasBook(String isbn) {
        return Objects.requireNonNull(books.getValue()).contains(new Book(isbn));
    }
}