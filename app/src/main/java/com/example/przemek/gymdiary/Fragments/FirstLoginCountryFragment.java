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

public class FirstLoginCountryFragment extends Fragment implements BlockingStep {

    @BindView(R.id.firstlogin_til_city)
    TextInputLayout til_city;
    @BindView(R.id.firstlogin_til_country)
    TextInputLayout til_country;
    @BindView(R.id.firstlogin_tiet_city)
    TextInputEditText tiet_city;
    @BindView(R.id.firstlogin_tiet_country)
    TextInputEditText tiet_country;
    User userModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_first_login_city, container, false);
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
                userModel.setCountry(tiet_country.getText().toString());
                userModel.setCity(tiet_city.getText().toString());
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

        if (TextUtils.isEmpty(tiet_country.getText().toString())) {
            return new VerificationError("emptyCountry");
        }
        if (TextUtils.isEmpty(tiet_city.getText().toString())) {
            return new VerificationError("emptyCity");
        } else {
            til_city.setError("");
            til_country.setError("");
            return null;
        }
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

        switch (error.getErrorMessage()) {

            case "emptyCountry": {
                til_country.setError(getString(R.string.put_your_country));
                break;
            }
            case "emptyCity": {
                til_city.setError(getString(R.string.put_your_city));
                break;
            }

        }


    }
}
