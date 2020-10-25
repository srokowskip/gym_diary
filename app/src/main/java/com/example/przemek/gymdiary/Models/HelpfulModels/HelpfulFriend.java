package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.Friend;

public class HelpfulFriend extends Friend {

    private String id;

    public HelpfulFriend() {
    }

    public HelpfulFriend(String id, Friend friend) {

        super(friend.getFriendNick());
        this.id = id;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
