package com.example.przemek.gymdiary.Models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;

public class TrainingPlanHistory implements Serializable {


    private Date date;


    private String userId;


    private String planId;


    public TrainingPlanHistory() {
    }

    public TrainingPlanHistory(String userId, String planId) {
        this.date = Timestamp.now().toDate();
        this.userId = userId;
        this.planId = planId;
    }

    public TrainingPlanHistory(TrainingPlanHistory trainingPlanHistory) {
        this.date = trainingPlanHistory.getDate();
        this.planId = trainingPlanHistory.getPlanId();
        this.userId = trainingPlanHistory.getUserId();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
