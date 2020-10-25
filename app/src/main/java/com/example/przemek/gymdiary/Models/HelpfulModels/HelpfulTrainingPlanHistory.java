package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.TrainingPlanHistory;

public class HelpfulTrainingPlanHistory extends TrainingPlanHistory {

    String id;

    public HelpfulTrainingPlanHistory() {

    }

    public HelpfulTrainingPlanHistory(String id, TrainingPlanHistory trainingPlanHistory) {
        super(trainingPlanHistory);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}


