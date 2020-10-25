package com.example.przemek.gymdiary.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulTrainingPlan;

import java.util.ArrayList;
import java.util.List;

public class LiveTrainingViewModel extends ViewModel {

    private MutableLiveData<HelpfulTrainingPlan> trainingPlan = new MutableLiveData<>();

    public void setTrainingPlan(HelpfulTrainingPlan trainingPlan) {
        this.trainingPlan.setValue(trainingPlan);
    }

    public HelpfulTrainingPlan getTrainingPlan() {
        return trainingPlan.getValue();
    }

    public ArrayList<HelpfulExercise> getListOfExercises() {
        return this.trainingPlan.getValue().getExerciseList();
    }

    public HelpfulExercise getExercise(int pos) {
        return this.trainingPlan.getValue().getExerciseList().get(pos);
    }

    public String getPlanName() {
        return this.trainingPlan.getValue().getTitle();
    }

    public String getPlanId() {
        return this.trainingPlan.getValue().getId();
    }

    public String getUserId(){
        return this.trainingPlan.getValue().getUserId();
    }


}
