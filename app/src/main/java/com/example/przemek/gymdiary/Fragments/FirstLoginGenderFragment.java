package com.example.przemek.gymdiary.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.przemek.gymdiary.Models.User;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.UserViewModel;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirstLoginGenderFragment extends Fragment implements BlockingStep {

    @BindView(R.id.firstlogin_radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.firstlogin_tv_gender_error)
    TextView tv_error;
    User userModel;

    private final int man_button_int_id = 2131230894;
    private final int woman_button_int_id = 2131230896;
    private final int other_button_int_id = 2131230895;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_first_login_gender, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        userModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class).getUser();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
        new Handler().postDelayed(() -> {
            if (verifyStep() == null) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == man_button_int_id) {
                    userModel.setGender(0);
                } else if (selectedId == woman_button_int_id) {
                    userModel.setGender(1);
                } else if (selectedId == other_button_int_id) {
                    userModel.setGender(2);
                }
                callback.goToNextStep();
            }
        }, 0L);// delay open another fragment,
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {


    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {

        if (radioGroup.getCheckedRadioButtonId() == -1) {
            return new VerificationError("noGenderSelected");
        }
        tv_error.setVisibility(View.INVISIBLE);
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

        switch (error.getErrorMessage()) {
            case "noGenderSelected": {
                tv_error.setVisibility(View.VISIBLE);
            }
        }

    }
}
