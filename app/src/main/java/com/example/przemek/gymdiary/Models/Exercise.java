package com.example.przemek.gymdiary.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.example.przemek.gymdiary.Enums.MusculeGroup;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Tatuażyk on 19.02.2018.
 */

public class Exercise implements Serializable {

    private String name;
    @Nullable
    private String userId;
    private int musculeGroup;
    private String description;
    @Nullable
    private String photoUri;

    //TODO zdjęcia


    public Exercise() {

    }

    public Exercise(String name, @Nullable String userId, int musculeGroup, String description, @Nullable String photoUri) {
        this.name = name;
        this.userId = userId;
        this.musculeGroup = musculeGroup;
        this.description = description;
        this.photoUri = photoUri;
    }

    public Exercise(String name, int musculeGroup, String description) {
        this.name = name;
        this.musculeGroup = musculeGroup;
        this.description = description;
    }

    public Exercise(String name, int musculeGroup, String description, @Nullable String photoUri) {
        this.name = name;
        this.musculeGroup = musculeGroup;
        this.description = description;
        this.photoUri = photoUri;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getUserId() {
        return userId;
    }

    public void setUserId(@Nullable String userId) {
        this.userId = userId;
    }

    public int getMusculeGroup() {
        return musculeGroup;
    }

    public void setMusculeGroup(int musculeGroup) {
        this.musculeGroup = musculeGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Nullable
    public String getPhotoUri() {
        return photoUri;
    }
}
