package com.example.przemek.gymdiary.Models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class PostComment {

    private String userId;
    private String userNick;
    private Date wroteDate;
    private String message;

    public PostComment() {
    }

    public PostComment(String userId, String userNick, String message) {
        this.userId = userId;
        this.userNick = userNick;
        this.wroteDate = Timestamp.now().toDate();
        this.message = message;
    }

    public PostComment(PostComment comment) {
        this.message = comment.message;
        this.userId = comment.userId;
        this.wroteDate = Timestamp.now().toDate();
        this.userNick = comment.userNick;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public Date getWroteDate() {
        return wroteDate;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setWroteDate(Date wroteDate) {
        this.wroteDate = wroteDate;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

}
