package com.example.przemek.gymdiary.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;

import com.example.przemek.gymdiary.Models.User;

public class UserViewModel extends ViewModel {
    //TODO uprościć
    private MutableLiveData<User> user = new MutableLiveData<>();

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public User getUser() {

        return this.user.getValue();
    }


}


