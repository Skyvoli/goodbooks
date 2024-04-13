package io.skyvoli.goodbooks.model;


import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.Book;

public class GlobalViewModel extends ViewModel {

    private final ObservableList<Book> books;

    public GlobalViewModel() {
        books = new ObservableArrayList<>();
    }

    public ObservableList<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
    }


    public void clearBooks() {
        this.books.clear();
    }

    public void setBooksAsynchronous(List<Book> books) {
        this.books.addAll(books);
    }

    public boolean hasBook(String isbn) {
        return books.contains(new Book(isbn));
    }
}