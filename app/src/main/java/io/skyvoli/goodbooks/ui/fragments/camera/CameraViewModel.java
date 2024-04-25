package io.skyvoli.goodbooks.ui.fragments.camera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CameraViewModel extends ViewModel {

    private final MutableLiveData<String> isbn;

    public CameraViewModel() {
        isbn = new MutableLiveData<>();
        isbn.setValue("This is camera fragment");
    }

    public LiveData<String> getIsbn() {
        return isbn;
    }

    public void setIsbn(String text) {
        isbn.setValue(text);
    }
}