package com.example.przemek.gymdiary.Adapters.FirestoreAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulTrainingPlan;
import com.example.przemek.gymdiary.Models.TrainingPlan;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.TrainingPlanViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class TrainingPlanFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter<TrainingPlan, TrainingPlanViewHolder> {
    private Context mContext;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param
     */
    @NonNull
    @Override
    public TrainingPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_plan, parent, false);

        return new TrainingPlanViewHolder(view, mContext);
    }

    public TrainingPlanFirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.mContext = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull TrainingPlanViewHolder holder, int position, @NonNull TrainingPlan model) {

        DocumentSnapshot document = getSnapshots().getSnapshot(holder.getAdapterPosition());
        TrainingPlan plan = document.toObject(TrainingPlan.class);
        HelpfulTrainingPlan planWithId = new HelpfulTrainingPlan(plan, document.getId());

        if (planWithId.getLastUsed() == null)
            holder.setLastUsed("Nigdy");
        else
            holder.setLastUsed(Helper.DateFormat(planWithId.getLastUsed(), "dd.MM.yyyy"));

        holder.setTitle(planWithId.getTitle());
        holder.setTrainingPlan(planWithId);


    }

}
