package com.neu.habify.ui.shareable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareableViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShareableViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the shareable fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
