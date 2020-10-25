package com.example.przemek.gymdiary.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.przemek.gymdiary.Models.TrainingPlan;

public class TrainingPlanViewModel extends ViewModel {

    private MutableLiveData<TrainingPlan> plan = new MutableLiveData<>();

    public void setPlan(TrainingPlan plan) {
        this.plan.setValue(plan);
    }

    public TrainingPlan getPlan() {
        return this.plan.getValue();
    }

}
