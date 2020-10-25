package com.example.przemek.gymdiary.Fragments;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;

import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.User;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.UserViewModel;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirstLoginBirthdayFragment extends Fragment implements BlockingStep {

    @BindView(R.id.firstlogin_til_birthday)
    TextInputLayout til_birthday;
    @BindView(R.id.firstlogin_tiet_birthday)
    TextInputEditText tiet_birthday;
    User userModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_first_login_birthday, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        userModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class).getUser();
        tiet_birthday.setOnClickListener(v -> showDatePicker());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {
        new Handler().postDelayed(() -> {
            if (verifyStep() == null) {
                userModel.setBirthday(tiet_birthday.getText().toString());
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

        if (TextUtils.isEmpty(tiet_birthday.getText().toString())) {
            return new VerificationError("emptyBirthday");
        } else if (Helper.getAge(tiet_birthday.getText().toString()) < 13) {
            return new VerificationError("tooYoung");
        } else {
            til_birthday.setError("");
            return null;
        }

    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

        switch (error.getErrorMessage()) {
            case "emptyBirthDay": {
                til_birthday.setError(getString(R.string.put_your_birthday));
                break;
            }
            case "tooYoung": {
                til_birthday.setError(getString(R.string.too_young));
                break;
            }
        }


    }

    //TODO wywalić stąd
    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Fragment Menager");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String zeroOrNot = monthOfYear >= 9 ? "." : ".0";

            String date = String.valueOf(String.valueOf(dayOfMonth) + zeroOrNot + String.valueOf(monthOfYear + 1)
                    + "." + String.valueOf(year));

            tiet_birthday.setText(date);
        }
    };


}
