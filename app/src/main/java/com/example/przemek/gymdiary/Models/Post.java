package com.example.przemek.gymdiary.Models;

import android.support.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Serializable {

    private String message;
    private String userId;
    private String userName;
    private Date timestamp;
    @Nullable
    private String photoUrl;
    private ArrayList<String> likersIds;
    private int commentsCount;

    public Post() {
    }

    public Post(String message, String userId, String userName, @Nullable String photoUrl) {

        this.message = message;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = Timestamp.now().toDate();
        this.photoUrl = photoUrl;
        this.likersIds = new ArrayList<>();
        this.commentsCount = 0;
    }

    public Post(Post post) {
        this.message = post.getMessage();
        this.timestamp = post.timestamp;
        this.userId = post.getUserId();
        this.userName = post.getUserName();
        this.likersIds = post.likersIds;
        this.photoUrl = post.getPhotoUrl();
        this.commentsCount = post.getCommentsCount();
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<String> getLikersIds() {
        return likersIds;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhotoUrl(@Nullable String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setLikersIds(ArrayList<String> userIds) {
        this.likersIds = userIds;
    }
}
