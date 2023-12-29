package io.skyvoli.goodbooks.ui.camera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CameraViewModel extends ViewModel {

    private final MutableLiveData<String> text1;
    private final MutableLiveData<String> text2;

    public CameraViewModel() {
        text1 = new MutableLiveData<>();
        text2 = new MutableLiveData<>();
        text1.setValue("This is camera fragment");
        text2.setValue("Text2");
    }

    public LiveData<String> getText1() {
        return text1;
    }

    public LiveData<String> getText2() {
        return text2;
    }

    public void setText1(String text) {
        text1.setValue(text);
    }

    public void setText2(String text) {
        text2.setValue(text);
    }
}