package io.skyvoli.goodbooks.ui.fragments.bookdetail;

import androidx.lifecycle.ViewModel;

import io.skyvoli.goodbooks.storage.database.dto.Book;

public class BookDetailViewModel extends ViewModel {

    private Book book;

    public BookDetailViewModel() {
        //necessary
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}