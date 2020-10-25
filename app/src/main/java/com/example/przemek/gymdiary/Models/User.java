package com.example.przemek.gymdiary.Models;

import java.io.Serializable;

/**
 * Created by Tatuażyk on 27.01.2018.
 */

public class User implements Serializable {

    private String name;
    private String surname;
    private String city;
    private String country;
    private String birthday;
    private int gender;
    private String nick;
    private String profilePhotoUrl;
    private String searchName;
    private boolean isBanned;

    //Konstruktor


    public User() {
    }

    public User(String name, String surname, String city, String country, int gender, String birthday, String nick, String profilePhotoUrl, boolean isBanned) {

        this.name = name;
        this.surname = surname;
        this.city = city;
        this.country = country;
        this.gender = gender;
        this.birthday = birthday;
        this.nick = nick;
        this.profilePhotoUrl = profilePhotoUrl;
        this.searchName = nick.toUpperCase();
        this.isBanned = isBanned;
    }

// SETTERY

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public void setNick(String nick) {
        this.nick = nick;
        this.searchName = nick.toUpperCase();
    }

    //GETTERY

    public String getName() {

        return name;
    }

    public String getNick() {
        return nick;
    }

    public String getSurname() {
        return surname;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getGender() {
        return gender;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public boolean getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(boolean banned) {
        isBanned = banned;
    }
}
