package com.example.przemek.gymdiary.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.Models.ExerciseHistory;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.Set;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.SummaryExerciseViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SummaryExercisesAdapter extends RecyclerView.Adapter<SummaryExerciseViewHolder> {

    private ArrayList<ExerciseHistory> exerciseWithSeries;
    private RecyclerView mRecyclerView;
    private Context context;

    public SummaryExercisesAdapter(ArrayList<ExerciseHistory> exercises, RecyclerView recyclerView, Context context) {
        this.exerciseWithSeries = exercises;
        this.mRecyclerView = recyclerView;
        this.context = context;
    }

    @NonNull
    @Override
    public SummaryExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_summary_exercise, parent, false);
        return new SummaryExerciseViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryExerciseViewHolder holder, int position) {

        ExerciseHistory data = exerciseWithSeries.get(position);


        ArrayList<Set> listOfSets = data.getListOfSets();
        int i = 0;
        for (Set set : listOfSets
                ) {
            i++;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.item_live_training_summary_serie, null);
            TextView tvRepeats = rowView.findViewById(R.id.training_summary_serie_tv_repeats_text);
            TextView tvWeight = rowView.findViewById(R.id.training_summary_serie_tv_weight_text);
            TextView tvSerieNumber = rowView.findViewById(R.id.training_summary_serie_number);


            DecimalFormat df = new DecimalFormat("0.#");

            tvSerieNumber.setText("Seria: " + String.valueOf(i));
            tvRepeats.setText(String.valueOf(set.getRepeats()));
            tvWeight.setText(String.valueOf(df.format(set.getWeight()) + "kg"));

            holder.addSerieToLinearLayout(rowView);
        }

        holder.setExerciseName(data.getExerciseName());

    }

    @Override
    public int getItemCount() {
        return exerciseWithSeries.size();
    }
}
