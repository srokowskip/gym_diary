package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.PostComment;

public class HelpfulPostComment extends PostComment {

    String id;

    public HelpfulPostComment() {
    }

    public HelpfulPostComment(String id, PostComment postComment) {
        super(postComment);
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
