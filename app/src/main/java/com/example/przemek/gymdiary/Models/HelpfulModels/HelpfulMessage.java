package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.Message;

public class HelpfulMessage extends Message {

    String id;

    public HelpfulMessage() {
    }

    public HelpfulMessage(String id, Message message) {
        super(message.getMessage(), message.getWriter());
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
