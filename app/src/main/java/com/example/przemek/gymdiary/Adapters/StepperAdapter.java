package com.example.przemek.gymdiary.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.example.przemek.gymdiary.Fragments.FirstLoginBirthdayFragment;
import com.example.przemek.gymdiary.Fragments.FirstLoginCountryFragment;
import com.example.przemek.gymdiary.Fragments.FirstLoginGenderFragment;
import com.example.przemek.gymdiary.Fragments.FirstLoginNameFragment;
import com.example.przemek.gymdiary.Fragments.FirstLoginNickFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class StepperAdapter extends AbstractFragmentStepAdapter {

    private static final String CURRENT_STEP_POSITION_KEY = "messageResourceId";

    public StepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position) {


            case 0:
                final FirstLoginNameFragment step1 = new FirstLoginNameFragment();
                Bundle b1 = new Bundle();
                b1.putInt(CURRENT_STEP_POSITION_KEY, position);
                step1.setArguments(b1);
                return step1;
            case 1:
                final FirstLoginBirthdayFragment step2 = new FirstLoginBirthdayFragment();
                Bundle b2 = new Bundle();
                b2.putInt(CURRENT_STEP_POSITION_KEY, position);
                step2.setArguments(b2);
                return step2;
            case 2:
                final FirstLoginCountryFragment step3 = new FirstLoginCountryFragment();
                Bundle b3 = new Bundle();
                b3.putInt(CURRENT_STEP_POSITION_KEY, position);
                step3.setArguments(b3);
                return step3;
            case 3:
                final FirstLoginGenderFragment step4 = new FirstLoginGenderFragment();
                Bundle b4 = new Bundle();
                b4.putInt(CURRENT_STEP_POSITION_KEY, position);
                step4.setArguments(b4);
                return step4;
            case 4:
                final FirstLoginNickFragment step5 = new FirstLoginNickFragment();
                Bundle b5 = new Bundle();
                b5.putInt(CURRENT_STEP_POSITION_KEY, position);
                step5.setArguments(b5);
                return step5;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        switch (position) {
            case 0:
                return new StepViewModel.Builder(context)
                        .setTitle("Tabs 1") //can be a CharSequence instead
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle("Tabs 2") //can be a CharSequence instead
                        .create();
            case 2:
                return new StepViewModel.Builder(context)
                        .setTitle("Tabs 3") //can be a CharSequence instead
                        .create();
            case 3:
                return new StepViewModel.Builder(context)
                        .setTitle("Tabs 4") //can be a CharSequence instead
                        .create();
            case 4:
                return new StepViewModel.Builder(context)
                        .setTitle("Tabs 5") //can be a CharSequence instead
                        .create();
        }
        return null;
    }
}