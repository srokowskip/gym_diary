package com.example.przemek.gymdiary.Adapters.FirestoreAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.przemek.gymdiary.Interfaces.RecyclerViewClickListener;
import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.ExerciseViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ExerciseFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter<Exercise, ExerciseViewHolder> {
    private Context mContext;
    RecyclerViewClickListener mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ExerciseFirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Exercise> options, Context context, RecyclerViewClickListener listener) {
        super(options);
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position, @NonNull Exercise model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        HelpfulExercise definedExercise = documentSnapshot.toObject(HelpfulExercise.class);

        holder.setName(definedExercise.getName());
        holder.setId(documentSnapshot.getId());
        holder.setMusculeGroup(definedExercise.getMusculeGroup());
        holder.setDescription(definedExercise.getDescription());

        holder.setPhotoUri(definedExercise.getPhotoUri());

        Glide.with(mContext).load(definedExercise.getPhotoUri()).into(holder.getExercisePhoto());

    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_recycler_adapter, parent, false);
        return new ExerciseViewHolder(view, mListener);
    }


    public void updateList(List<HelpfulExercise> listOfExercises) {


    }

}
