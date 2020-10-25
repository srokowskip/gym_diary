package com.example.przemek.gymdiary.Models.HelpfulModels;

import android.support.annotation.Nullable;

import com.example.przemek.gymdiary.Models.Exercise;

import java.util.List;

public class HelpfulExercise extends Exercise {

    private String id;
    @Nullable
    private List<Integer> listOfInitialRepeats;


    public HelpfulExercise(String id, Exercise exercise) {
        super(exercise.getName(), exercise.getUserId(), exercise.getMusculeGroup(), exercise.getDescription(), exercise.getPhotoUri());
        this.id = id;
    }

    public HelpfulExercise() {
        super();
    }


    @Nullable
    public List<Integer> getListOfRepeats() {
        return listOfInitialRepeats;
    }

    public void setListOfRepeats(List<Integer> listOfRepeats) {
        this.listOfInitialRepeats = listOfRepeats;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
