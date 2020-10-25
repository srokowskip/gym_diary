package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.User;

public class HelpfulUser extends User {

    private String id;
    public HelpfulUser() {

    }

    public HelpfulUser(String id, User user) {
        super(user.getName(), user.getSurname(), user.getCity(), user.getCountry(), user.getGender(), user.getBirthday(), user.getNick(), user.getProfilePhotoUrl(), user.getIsBanned());
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
