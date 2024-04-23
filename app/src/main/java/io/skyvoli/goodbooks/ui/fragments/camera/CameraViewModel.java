package io.skyvoli.goodbooks.ui.fragments.camera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CameraViewModel extends ViewModel {

    private final MutableLiveData<String> text1;

    public CameraViewModel() {
        text1 = new MutableLiveData<>();
        text1.setValue("This is camera fragment");
    }

    public LiveData<String> getText1() {
        return text1;
    }

    public void setText1(String text) {
        text1.setValue(text);
    }
}