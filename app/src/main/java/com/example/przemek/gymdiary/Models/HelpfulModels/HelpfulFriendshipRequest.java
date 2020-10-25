package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.FriendshipRequest;

public class HelpfulFriendshipRequest extends FriendshipRequest {

    private String id;

    public HelpfulFriendshipRequest() {
    }

    public HelpfulFriendshipRequest(String id, FriendshipRequest request) {

        super(request);
        this.id = id;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
