package com.example.registration.ui.list_of_providers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListOfProvidersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListOfProvidersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}