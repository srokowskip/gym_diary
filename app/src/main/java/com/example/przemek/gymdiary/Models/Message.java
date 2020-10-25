package com.example.przemek.gymdiary.Models;

import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.google.firebase.Timestamp;

import java.util.Date;

public class Message {

    private Date wroteDate;
    private String message;
    private HelpfulUser writer;

    public Message() {
        this.wroteDate = Timestamp.now().toDate();
    }

    public Message(String message, HelpfulUser user) {

        this.message = message;
        this.writer = user;
        this.wroteDate = Timestamp.now().toDate();
    }

    public Date getWroteDate() {
        return wroteDate;
    }

    public String getMessage() {
        return message;
    }

    public HelpfulUser getWriter() {
        return writer;
    }

    public void setWriter(HelpfulUser writer) {
        this.writer = writer;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setWroteDate(Date wroteDate) {
        this.wroteDate = wroteDate;
    }
}
