package io.skyvoli.goodbooks.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlobalViewModel extends ViewModel {

    private final MutableLiveData<List<String>> books;

    public GlobalViewModel() {
        books = new MutableLiveData<>();

        books.setValue(new ArrayList<String>());
    }

    public LiveData<List<String>> getBooks() {
        return books;
    }

    public void addBook(String text) {
        Objects.requireNonNull(books.getValue()).add(text);
    }
}
