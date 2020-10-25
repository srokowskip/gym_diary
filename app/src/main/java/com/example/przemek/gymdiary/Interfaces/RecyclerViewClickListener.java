package com.example.przemek.gymdiary.Interfaces;

import android.view.View;

import com.example.przemek.gymdiary.Models.Exercise;

public interface RecyclerViewClickListener {


    void onClick(View view, int position, String exerciseId, Exercise definedExercise);


}
