package com.example.przemek.gymdiary.Models.HelpfulModels;

import com.example.przemek.gymdiary.Models.ExerciseHistory;

public class HelpfulExerciseHistory {

    private String id;
    private ExerciseHistory history;

    public HelpfulExerciseHistory(String id, ExerciseHistory exerciseHistory) {
        this.id = id;
        this.history = exerciseHistory;
    }

    public HelpfulExerciseHistory() {
    }

    public ExerciseHistory getHistory() {
        return history;
    }

    public String getId() {
        return id;
    }

    public void setHistory(ExerciseHistory history) {
        this.history = history;
    }

    public void setId(String id) {
        this.id = id;
    }
}
