package com.example.przemek.gymdiary.Models;

import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class TrainingPlan implements Serializable {

    //Todo userId
    private String userId;
    private String title;
    private String description;
    private Date lastUsed;


    private ArrayList<HelpfulExercise> exerciseList = new ArrayList<>();

    public TrainingPlan() {
    }

    public TrainingPlan(TrainingPlan trainingPlan) {

        this.lastUsed = trainingPlan.getLastUsed();
        this.userId = trainingPlan.getUserId();
        this.title = trainingPlan.getTitle();
        this.description = trainingPlan.getDescription();
        this.exerciseList = trainingPlan.getExerciseList();

    }

    public TrainingPlan(Date lastUsed, String title, String description, String userId) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.lastUsed = lastUsed;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<HelpfulExercise> getExerciseList() {
        return exerciseList;
    }

    public void setExerciseList(ArrayList<HelpfulExercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

}
