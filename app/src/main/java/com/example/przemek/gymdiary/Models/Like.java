package com.example.przemek.gymdiary.Models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Like {

    private String userId;
    private Date timestamp;

    public Like() {
    }

    public Like(String userId) {
        this.userId = userId;
        this.timestamp = Timestamp.now().toDate();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
