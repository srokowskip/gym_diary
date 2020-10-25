package com.example.przemek.gymdiary.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.RecyclerViewClickListener;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.ExerciseViewHolder;

import java.util.ArrayList;

public class ExerciseRecyclerAdapter extends RecyclerView.Adapter<ExerciseViewHolder> {

    private ArrayList<HelpfulExercise> mExercises;
    private RecyclerView mRecyclerView;
    private RecyclerViewClickListener mListener;
    private boolean showRemoving;
    private Context conetext;

    public ExerciseRecyclerAdapter(ArrayList<HelpfulExercise> data, RecyclerView recyclerView, RecyclerViewClickListener listener, boolean showRemoving, Context context) {

        this.mExercises = data;
        this.mRecyclerView = recyclerView;
        this.mListener = listener;
        this.showRemoving = showRemoving;
        this.conetext = context;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_recycler_adapter, parent, false);
        return new ExerciseViewHolder(view, mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {

        HelpfulExercise helpfulExercise = mExercises.get(position);
        holder.setName(helpfulExercise.getName());
        holder.setId(helpfulExercise.getId());
        holder.setMusculeGroup(helpfulExercise.getMusculeGroup());
        holder.setDescription(helpfulExercise.getDescription());
        holder.setPhotoUri(helpfulExercise.getPhotoUri());
        holder.setMusculeGroupText(getMusculeGroupText(helpfulExercise.getMusculeGroup()));

        Glide.with(conetext).setDefaultRequestOptions(Helper.getPlaceholder()).load(helpfulExercise.getPhotoUri()).into(holder.getExercisePhoto());

        if (helpfulExercise.getListOfRepeats() != null)
            holder.setInitialSeries(helpfulExercise.getListOfRepeats());

        if (showRemoving) {
            holder.setRemovableItem();
            holder.getRemoveButton().setOnClickListener(c -> {
                mExercises.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

            });
        }

    }

    private String getMusculeGroupText(int musculeGroup) {
        if (musculeGroup == 0)
            return "Klatka piersiowa";
        if (musculeGroup == 1)
            return "Mięśnie nóg";
        if (musculeGroup == 2)
            return "Ramiona";
        if (musculeGroup == 3)
            return "Plecy";

        else
            return "";
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    public void updateList(ArrayList<HelpfulExercise> updatedList) {

        mExercises = new ArrayList<>();
        mExercises.addAll(updatedList);
        notifyDataSetChanged();

    }

}
