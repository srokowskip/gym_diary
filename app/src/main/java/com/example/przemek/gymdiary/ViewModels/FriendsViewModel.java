package com.example.przemek.gymdiary.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulFriend;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulFriendshipRequest;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;

import java.util.ArrayList;

public class FriendsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<HelpfulUser>> listOfUserFriends = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HelpfulUser>> listOfUserToAdd = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HelpfulFriendshipRequest>> listOfSentRequests = new MutableLiveData<>();
    private MutableLiveData<ArrayList<HelpfulFriendshipRequest>> listOfRequests = new MutableLiveData<>();



    public void setListOfUserFriends(ArrayList<HelpfulUser> listOfUserFriends) {
        this.listOfUserFriends.setValue(listOfUserFriends);
    }

    public void setListOfUserToAdd(ArrayList<HelpfulUser> listOfUserToAdd) {
        this.listOfUserToAdd.setValue(listOfUserToAdd);
    }

    public void setListOfRequests(ArrayList<HelpfulFriendshipRequest> listOfRequests) {
        this.listOfRequests.setValue(listOfRequests);
    }

    public void setListOfSentRequests(ArrayList<HelpfulFriendshipRequest> listOfSentRequests) {
        this.listOfSentRequests.setValue(listOfSentRequests);
    }

    public ArrayList<HelpfulFriendshipRequest> getListOfRequests() {
        return listOfRequests.getValue();
    }

    public ArrayList<HelpfulFriendshipRequest> getListOfSentRequests() {
        return listOfSentRequests.getValue();
    }

    public ArrayList<HelpfulUser> getListOfUserFriends() {
        return listOfUserFriends.getValue();
    }

    public ArrayList<HelpfulUser> getListOfUsersToAdd() {
        return listOfUserToAdd.getValue();
    }

}
