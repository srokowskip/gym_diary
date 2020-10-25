package com.example.przemek.gymdiary.Adapters.FirestoreAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.TrainingPlanHistory;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.TrainingPlanHistoryViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class TrainingPlanHistoryFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter<TrainingPlanHistory, TrainingPlanHistoryViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param
     */
    Context mContext;

    public TrainingPlanHistoryFirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions<TrainingPlanHistory> options, Context context) {
        super(options);
        mContext = context;
    }

    @NonNull
    @Override
    public TrainingPlanHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_plan_history, parent, false);

        return new TrainingPlanHistoryViewHolder(view, mContext);
    }


    @Override
    protected void onBindViewHolder(@NonNull TrainingPlanHistoryViewHolder holder, int position, @NonNull TrainingPlanHistory model) {

        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());

        TrainingPlanHistory trainingPlanHistory = snapshot.toObject(TrainingPlanHistory.class);
        holder.setHistoryId(snapshot.getId());
        holder.setDate(Helper.DateFormat(trainingPlanHistory.getDate(), "EEEE, dd.MM.yyyy"));

    }


}
