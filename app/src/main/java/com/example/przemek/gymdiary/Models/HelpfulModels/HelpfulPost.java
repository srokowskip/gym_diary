package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.Post;

public class HelpfulPost extends Post {

    private String id;

    public HelpfulPost(String id, Post post) {
        super(post);
        this.id = id;
    }

    public HelpfulPost() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
