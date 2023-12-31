package io.skyvoli.goodbooks.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GlobalViewModel extends ViewModel {

    private final MutableLiveData<Set<String>> books;

    public GlobalViewModel() {
        books = new MutableLiveData<>();

        books.setValue(new HashSet<String>());
    }

    public LiveData<Set<String>> getBooks() {
        return books;
    }

    public void addBook(String text) {
        Objects.requireNonNull(books.getValue()).add(text);
    }

    public boolean hasBook(String text) {
        return Objects.requireNonNull(books.getValue()).contains(text);
    }
}
