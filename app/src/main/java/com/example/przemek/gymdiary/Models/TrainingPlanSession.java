package com.example.przemek.gymdiary.Models;


import com.google.firebase.Timestamp;

import java.util.Date;

public class TrainingPlanSession {


    private String userId;
    private String planId;
    private Date createdDate;

    public TrainingPlanSession(){}

    public TrainingPlanSession(String userId, String planId) {

        this.userId = userId;
        this.planId = planId;
        this.createdDate = Timestamp.now().toDate();

    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

}

