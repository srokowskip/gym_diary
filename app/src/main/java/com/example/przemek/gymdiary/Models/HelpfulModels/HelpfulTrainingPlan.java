package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.TrainingPlan;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class HelpfulTrainingPlan extends TrainingPlan {

    private String id;

    public HelpfulTrainingPlan() {
        super();
    }

    public HelpfulTrainingPlan(TrainingPlan plan, String planId) {
        super(plan);
        this.id = planId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public ArrayList<HelpfulExercise> getExerciseList() {
        return super.getExerciseList();
    }

    @Override
    public void setExerciseList(ArrayList<HelpfulExercise> exerciseList) {
        super.setExerciseList(exerciseList);
    }
}
