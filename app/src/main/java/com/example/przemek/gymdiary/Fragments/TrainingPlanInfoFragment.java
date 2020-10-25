package com.example.przemek.gymdiary.Fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.przemek.gymdiary.Activities.TrainingPlanActivity;
import com.example.przemek.gymdiary.Interfaces.Validateable;
import com.example.przemek.gymdiary.Models.TrainingPlan;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.TrainingPlanViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingPlanInfoFragment extends Fragment implements Validateable {
    @BindView(R.id.create_plan_tiet_title)
    TextInputEditText tiet_title;
    @BindView(R.id.create_plan_description)
    EditText et_description;
    TrainingPlanActivity parentActivity;
    TrainingPlan mModel;
    boolean editing = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (TrainingPlanActivity) getActivity();
        editing = parentActivity.isEditing();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_create_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mModel = ViewModelProviders.of(getActivity()).get(TrainingPlanViewModel.class).getPlan();
        ButterKnife.bind(this, view);
        if (editing) {
            tiet_title.setText(mModel.getTitle());
            et_description.setText(mModel.getDescription());

        }
        tiet_title.addTextChangedListener(titleWatcher);
        et_description.addTextChangedListener(descriptionWatcher);
    }

    private final TextWatcher titleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mModel.setTitle(editable.toString());
        }
    };
    private final TextWatcher descriptionWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mModel.setDescription(editable.toString());
        }
    };

    @Override
    public void onDestroy() {
        mModel = null;
        super.onDestroy();
    }

    //TODO funkcja walidacyjna
    private boolean titleTextValidation() {

        if (TextUtils.isEmpty(tiet_title.getText())) {
            tiet_title.setError("Nazwa planu nie może być pusta");
            return false;
        } else if (tiet_title.getText().length() < 3) {
            tiet_title.setError("Nazwa planu musi mieć co najmniej 3 znaki");
            return false;
        }
        return true;
    }


    @Override
    public boolean isValid() {
        if (!titleTextValidation())
            return false;

        return true;
    }
}
