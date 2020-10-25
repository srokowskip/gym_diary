package com.example.przemek.gymdiary.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;

public class PhotoViewModel extends ViewModel {

    private MutableLiveData<Uri> localPhotoUri = new MutableLiveData<Uri>();

    public void setLocalPhotoUri(Uri photoUri) {
        this.localPhotoUri.setValue(photoUri);
    }

    public Uri getLocalPhotoUri() {
        return this.localPhotoUri.getValue();
    }
}
