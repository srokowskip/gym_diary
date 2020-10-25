package com.example.przemek.gymdiary.Models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class Chat {

    private Date started;
    private ArrayList<String> participantIds;


    public Chat() {
        this.started = Timestamp.now().toDate();
    }

    public Chat(ArrayList<String> ids) {
        this.participantIds = ids;
        this.started = Timestamp.now().toDate();
    }

    public void setParticipantIds(ArrayList<String> participantIds) {
        this.participantIds = participantIds;
    }

    public ArrayList<String> getParticipantIds() {
        return participantIds;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getStarted() {
        return started;
    }
}
