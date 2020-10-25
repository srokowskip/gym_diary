package com.example.przemek.gymdiary.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulTrainingPlanHistory;
import com.example.przemek.gymdiary.Models.TrainingPlanHistory;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.TrainingPlanHistoryViewHolder;

import java.util.ArrayList;

public class TrainingPlanHistoryAdapter extends RecyclerView.Adapter<TrainingPlanHistoryViewHolder> {

    private ArrayList<HelpfulTrainingPlanHistory> data;
    private Context context;

    public TrainingPlanHistoryAdapter(ArrayList<HelpfulTrainingPlanHistory> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public TrainingPlanHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_plan_history, parent, false);

        return new TrainingPlanHistoryViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingPlanHistoryViewHolder holder, int position) {

        HelpfulTrainingPlanHistory history = data.get(position);

        holder.setDate(Helper.DateFormat(history.getDate(), "dd.MM.yyyy hh:mm"));
        holder.setHistoryId(history.getId());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
