package com.example.przemek.gymdiary.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.przemek.gymdiary.Interfaces.RecyclerViewClickListener;
import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.exercise_card_title)
    TextView tv_name;
    @BindView(R.id.exercise_card_initial_series)
    TextView tv_initial_series;
    @BindView(R.id.exercise_card_remove)
    ImageButton removeButton;
    @BindView(R.id.exercisePhoto)
    ImageView exercisePhoto;
    @BindView(R.id.exercise_muscule)
    TextView musculeGroupText;

    private String id;
    private int musculeGroup;
    private RecyclerViewClickListener mListener;

    private String photoUri = null;

    private String description;

    public ExerciseViewHolder(View itemView, RecyclerViewClickListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mListener = listener;
        itemView.setOnClickListener(this);
    }

    public void setName(String name) {
        tv_name.setText(name);
    }

    public String getName() {
        return tv_name.getText().toString();
    }

    public void setInitialSeries(List<Integer> listOfInitialRepeats) {

        if (listOfInitialRepeats.size() > 0) {
            tv_initial_series.setVisibility(View.VISIBLE);
            StringBuilder string = new StringBuilder();
            for (int repeats : listOfInitialRepeats
                    ) {
                string.append(String.valueOf(repeats)).append("/");
            }
            string.deleteCharAt(string.length() - 1);
            tv_initial_series.setText(string.toString());
        }
    }

    public void setRemovableItem() {
        removeButton.setVisibility(View.VISIBLE);
    }

    public ImageButton getRemoveButton() {
        return this.removeButton;
    }

    public void setMusculeGroup(int musculeGroup) {
        this.musculeGroup = musculeGroup;
    }

    public int getMusculeGroup() {
        return musculeGroup;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageView getExercisePhoto() {
        return exercisePhoto;
    }

    public void setExercisePhoto(ImageView exercisePhoto) {
        this.exercisePhoto = exercisePhoto;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public void setMusculeGroupText(String text) {
        musculeGroupText.setText(text);
    }

    @Override
    public void onClick(View v) {
        Exercise definedExercise = new Exercise(tv_name.getText().toString(), musculeGroup, description, photoUri);
        mListener.onClick(v, getAdapterPosition(), id, definedExercise);
    }
}
