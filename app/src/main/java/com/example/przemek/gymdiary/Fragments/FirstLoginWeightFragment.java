package com.example.przemek.gymdiary.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.przemek.gymdiary.Models.User;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.UserViewModel;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirstLoginWeightFragment extends Fragment implements BlockingStep {
    @BindView(R.id.firstlogin_til_weight)
    TextInputLayout til_weight;
    @BindView(R.id.firstlogin_tiet_weight)
    TextInputEditText tiet_weight;
    User userModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_first_login_weight, container, false);
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
                userModel.setWeight(Double.parseDouble(tiet_weight.getText().toString()));
                callback.goToNextStep();
            }
        }, 0L);// delay open another fragment,
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {

        if (TextUtils.isEmpty(tiet_weight.getText())) {
            return new VerificationError("emptyWeight");
        } else if (!checkUserLowWeight()) {
            return new VerificationError("toLowWeight");
        } else if (!checkUserHighWeight()) {
            return new VerificationError("toHighWeight");
        }


        return null;
    }

    private boolean checkUserHighWeight() {
        int userGender = userModel.getGender();

        switch (userGender) {
            case 0: {
                return !(Double.parseDouble(tiet_weight.getText().toString()) > 150);
            }
            case 1: {
                return !(Double.parseDouble(tiet_weight.getText().toString()) > 100);
            }
        }
        return true;
    }

    private boolean checkUserLowWeight() {

        int userGender = userModel.getGender();

        switch (userGender) {
            case 0: {
                return !(Double.parseDouble(tiet_weight.getText().toString()) < 45);

            }
            case 1: {
                return !(Double.parseDouble(tiet_weight.getText().toString()) < 35);
            }
        }
        return true;
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onError(@NonNull VerificationError error) {

        switch (error.getErrorMessage()) {

            case "emptyWeight": {
                til_weight.setError(getString(R.string.field_cannot_be_empty));
                break;
            }
            case "toLowWeight": {
                til_weight.setError(getString(R.string.your_weight_is_too_low));
                break;
            }
            case "toHighWeight": {
                til_weight.setError(getString(R.string.your_weight_is_to_high));
                break;
            }

        }
    }
}
