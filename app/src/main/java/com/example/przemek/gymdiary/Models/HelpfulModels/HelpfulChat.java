package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.Chat;

public class HelpfulChat extends Chat {

    String chatId;
    Chat chat;

    public HelpfulChat() {
        super();
    }

    public HelpfulChat(String id, Chat chat) {
        super(chat.getParticipantIds());
        this.chatId = id;
    }

    public Chat getChat() {
        return chat;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
