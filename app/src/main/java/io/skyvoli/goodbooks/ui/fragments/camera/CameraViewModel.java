package io.skyvoli.goodbooks.ui.fragments.camera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CameraViewModel extends ViewModel {

    private final MutableLiveData<String> isbn;
    private final MutableLiveData<Boolean> isNewBook;

    public CameraViewModel() {
        isbn = new MutableLiveData<>();
        isNewBook = new MutableLiveData<>(false);
    }

    public LiveData<String> getIsbn() {
        return isbn;
    }

    public void setIsbn(String text) {
        isbn.setValue(text);
    }

    public MutableLiveData<Boolean> getIsNewBook() {
        return isNewBook;
    }

    public void setIsNewBook(boolean value) {
        isNewBook.setValue(value);
    }
}