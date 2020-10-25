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

public class FirstLoginNameFragment extends Fragment implements BlockingStep {

    @BindView(R.id.firstlogin_til_name)
    TextInputLayout til_name;
    @BindView(R.id.firstlogin_tiet_name)
    TextInputEditText tiet_name;
    @BindView(R.id.firstlogin_til_surname)
    TextInputLayout til_surname;
    @BindView(R.id.firstlogin_tiet_surname)
    TextInputEditText tiet_surname;
    User userModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_first_login_name, container, false);
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
                userModel.setName(tiet_name.getText().toString());
                userModel.setSurname(tiet_surname.getText().toString());
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

        if (TextUtils.isEmpty(tiet_name.getText())) {
            return new VerificationError("emptyName");
        } else if (tiet_name.getText().toString().length() < 2) {
            return new VerificationError("shortName");
        } else {
            til_name.setError("");
        }

        if (TextUtils.isEmpty(tiet_surname.getText())) {
            return new VerificationError("emptySurname");
        } else if (tiet_surname.getText().toString().length() < 2) {
            return new VerificationError("shortSurname");
        } else {
            til_surname.setError("");
        }

        return null;
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onError(@NonNull VerificationError error) {

        switch (error.getErrorMessage()) {

            case "shortName": {
                til_name.setError(getString(R.string.too_short_name));
                break;
            }
            case "shortSurname": {
                til_surname.setError(getString(R.string.too_short_surname));
                break;
            }
            case "emptyName": {
                til_name.setError(getString(R.string.put_your_name));
                break;
            }
            case "emptySurname": {
                til_surname.setError(getString(R.string.put_your_surname));
                break;
            }

        }
    }
}
