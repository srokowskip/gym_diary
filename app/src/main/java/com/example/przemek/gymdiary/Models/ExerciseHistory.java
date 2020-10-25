package com.example.przemek.gymdiary.Models;

import java.util.ArrayList;

public class ExerciseHistory {

    private ArrayList<Set> listOfSets;

    private String exerciseName;

    public ExerciseHistory() {
    }

    public ExerciseHistory(ArrayList<Set> listOfSets, String exerciseName) {
        this.listOfSets = listOfSets;
        this.exerciseName = exerciseName;
    }

    public ArrayList<Set> getListOfSets() {
        return listOfSets;
    }

    public void setListOfSets(ArrayList<Set> listOfSets) {
        this.listOfSets = listOfSets;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

}
