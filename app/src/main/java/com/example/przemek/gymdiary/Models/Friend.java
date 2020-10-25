package com.example.przemek.gymdiary.Models;

public class Friend {

    public String friendNick;

    public Friend() {
    }

    public Friend(String friendNick) {
        this.friendNick = friendNick;
    }


    public String getFriendNick() {
        return friendNick;
    }

    public void setFriendNick(String friendNick) {
        this.friendNick = friendNick;
    }

}
