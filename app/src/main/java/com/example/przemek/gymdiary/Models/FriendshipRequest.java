package com.example.przemek.gymdiary.Models;

import com.example.przemek.gymdiary.Enums.FriendshipStatus;
import com.google.firebase.Timestamp;

import java.util.Date;

public class FriendshipRequest {

    private String initiatorId;
    private String initiatorNick;
    private String askedId;
    private String askedNick;
    private Date startDate;
    private FriendshipStatus status;

    public FriendshipRequest() {
    }

    public FriendshipRequest(String initiatorId, String initiatorUserName, String askedId, String askedNick) {
        this.askedId = askedId;
        this.askedNick = askedNick;
        this.initiatorId = initiatorId;
        this.initiatorNick = initiatorUserName;
        this.startDate = Timestamp.now().toDate();
        this.status = FriendshipStatus.PENDING;
    }

    public FriendshipRequest(FriendshipRequest friendshipRequest) {

        this.initiatorId = friendshipRequest.initiatorId;
        this.initiatorNick = friendshipRequest.initiatorNick;
        this.askedNick = friendshipRequest.askedNick;
        this.askedId = friendshipRequest.askedId;
        this.startDate = friendshipRequest.startDate;
        this.status = friendshipRequest.status;

    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getAskedId() {
        return askedId;
    }

    public void setAskedId(String askedId) {
        this.askedId = askedId;
    }

    public String getInitiatorNick() {
        return initiatorNick;
    }

    public void setInitiatorNick(String initiatorNick) {
        this.initiatorNick = initiatorNick;
    }

    public String getAskedNick() {
        return askedNick;
    }

    public void setAskedNick(String askedNick) {
        this.askedNick = askedNick;
    }
}
