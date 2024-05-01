package io.skyvoli.goodbooks.ui.fragments.camera;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.skyvoli.goodbooks.storage.database.dto.Book;

public class CameraViewModel extends ViewModel {
    private final MutableLiveData<Book> book;
    private final MutableLiveData<Boolean> isNewBook;
    private final MutableLiveData<Boolean> showBook;

    public CameraViewModel() {
        book = new MutableLiveData<>();
        isNewBook = new MutableLiveData<>();
        showBook = new MutableLiveData<>(true);
    }

    public MutableLiveData<Boolean> getIsNewBook() {
        return isNewBook;
    }

    public void setIsNewBook(boolean value) {
        isNewBook.setValue(value);
    }

    public MutableLiveData<Book> getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book.setValue(book);
    }

    public MutableLiveData<Boolean> getShowBook() {
        return showBook;
    }

    public void setShowBook(boolean value) {
        showBook.setValue(value);
    }
}