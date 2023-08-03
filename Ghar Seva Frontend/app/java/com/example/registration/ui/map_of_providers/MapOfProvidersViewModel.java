package com.example.registration.ui.map_of_providers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapOfProvidersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MapOfProvidersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}