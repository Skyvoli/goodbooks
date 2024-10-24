package io.skyvoli.goodbooks.ui.fragments.camera;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.skyvoli.goodbooks.storage.database.dto.Book;

public class CameraViewModel extends ViewModel {
    private final MutableLiveData<Book> book;
    private final MutableLiveData<Boolean> isNewBook;
    private final MutableLiveData<Boolean> showBook;
    private final MutableLiveData<List<Integer>> missing;

    public CameraViewModel() {
        book = new MutableLiveData<>();
        isNewBook = new MutableLiveData<>();
        showBook = new MutableLiveData<>(true);
        missing = new MutableLiveData<>(new ArrayList<>());
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

    public MutableLiveData<List<Integer>> getMissing() {
        return missing;
    }

    public void setMissingBooks(List<Integer> missing) {
        this.missing.setValue(missing);
    }
}