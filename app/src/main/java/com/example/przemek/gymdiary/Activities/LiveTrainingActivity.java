package com.example.przemek.gymdiary.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.przemek.gymdiary.Adapters.DynamicFragmentAdapter;
import com.example.przemek.gymdiary.DbManagement.LiveTrainingManagement;
import com.example.przemek.gymdiary.DbManagement.TrainingPlansManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Fragments.LiveTrainingExerciseFragment;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.Set;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.LiveTrainingViewModel;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveTrainingActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.live_training_toolbar)
    Toolbar toolbar;
    @BindView(R.id.live_training_container)
    FrameLayout activityLayoutContainer;

    LiveTrainingViewModel liveTrainingViewModel;
    DynamicFragmentAdapter mDynamicFragmentAdapter;
    ArrayList<String> exerciseNames = new ArrayList<>();

    ArrayList<String> exerciseIds = new ArrayList<>();


    public String sessionId;
    private boolean resumed;

    MaterialDialog progressDialog = null;

    LiveTrainingManagement liveTrainingManagement = new LiveTrainingManagement();

    ArrayMap<String, Double> userMaxLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_training);
        ButterKnife.bind(this);

        Intent i = getIntent();

        sessionId = i.getStringExtra("sessionId");
        resumed = i.getBooleanExtra("resumed", false);

        Toast.makeText(this, String.valueOf(resumed), Toast.LENGTH_SHORT).show();


        if (sessionId != null) {

            String progressTitle = resumed ? "Wczytywanie treningu" : "Tworzenie treningu";
            String message = resumed ? "Przywracam wszystkie dane, proszę czekać..." : "Ładuję dane, proszę czekać...";
            progressDialog = new MaterialDialog.Builder(LiveTrainingActivity.this)
                    .title(progressTitle)
                    .contentColor(getColor(R.color.white))
                    .backgroundColor(getColor(R.color.colorPrimaryDark))
                    .titleColor(getColor(R.color.white))
                    .content(message)
                    .progress(true, 0)
                    .canceledOnTouchOutside(false)
                    .show();

            fetchTrainingPlan();

        }
    }

    private void fetchTrainingPlan() {

        liveTrainingManagement.getLiveTraining(sessionId, session -> {
            if (session != null) {
                TrainingPlansManagement trainingPlansManagement = new TrainingPlansManagement();
                trainingPlansManagement.getTrainingPlan(session.getPlanId(), plan -> {
                    if (plan != null) {
                        liveTrainingViewModel = ViewModelProviders.of(this).get(LiveTrainingViewModel.class);
                        liveTrainingViewModel.setTrainingPlan(plan);
                        if (!resumed)
                            updateLastUsedProperty(liveTrainingViewModel.getPlanId());
                        init();
                    }
                });
            }
        });

    }

    private void updateLastUsedProperty(String docId) {
        TrainingPlansManagement trainingPlansManagement = new TrainingPlansManagement();
        trainingPlansManagement.updateLastUsedProperty(Timestamp.now().toDate(), docId, status -> {
            if (status == DbStatus.Success)
                Toast.makeText(this, "Ustawiono datę", Toast.LENGTH_SHORT).show();
            else {
                //TODO obsługa błędu
            }
        });
    }


    private void init() {


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle(liveTrainingViewModel.getPlanName());

        viewPager.setOffscreenPageLimit(10);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        ArrayList<HelpfulExercise> listOfExercises = liveTrainingViewModel.getListOfExercises();

        for (HelpfulExercise exercise : listOfExercises
                ) {
            String exerciseName = exercise.getName();
            String exerciseId = exercise.getId();
            exerciseNames.add(exerciseName);
            exerciseIds.add(exerciseId);
        }

        setDynamicFragmentToTabLayout();

        if (!resumed)
            initSessionWithExercises();
        else
            getSavedData();

    }

    private void initSessionWithExercises() {

        liveTrainingManagement.initSessionWithExercises(sessionId, exerciseIds, status -> {

            if (status == DbStatus.Success) {
            } else {
            }

        });
        activityLayoutContainer.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
    }

    private void getSavedData() {

        liveTrainingManagement.getResumedExercises(sessionId, s -> {
            if (s != null) {
                Map<String, Map<String, Object>> savedData = s;
                assignSavedDataToExercises(savedData);
            }
        });

    }

    private void assignSavedDataToExercises(Map<String, Map<String, Object>> data) {

        List<LiveTrainingExerciseFragment> listOfFragments = mDynamicFragmentAdapter.getRegisteredFragments();

        for (LiveTrainingExerciseFragment exerciseFragment : listOfFragments
                ) {
            String exerciseBelongsToFragment = exerciseFragment.getExerciseId();

            Map<String, Object> mapOfSets = data.get(exerciseBelongsToFragment);

            List<Set> listOfSets = new ArrayList<>();

            for (int i = 1; i <= mapOfSets.size(); i++) {
                HashMap<String, Object> setMap = (HashMap<String, Object>) mapOfSets.get(String.valueOf(i));

                Long repeats = (long) setMap.get("repeats");
                double weight = (double) setMap.get("weight");
                Set set = new Set(repeats.intValue(), weight);
                listOfSets.add(set);
            }

            if (!listOfSets.isEmpty()) {
                exerciseFragment.setSavedData(listOfSets);
            }
            progressDialog.dismiss();
            activityLayoutContainer.setVisibility(View.VISIBLE);


        }
    }

    private void setDynamicFragmentToTabLayout() {


        for (String exerciseName : exerciseNames
                ) {
            mTabLayout.addTab(mTabLayout.newTab().setText(exerciseName));
        }

        mDynamicFragmentAdapter = new DynamicFragmentAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        viewPager.setAdapter(mDynamicFragmentAdapter);
        viewPager.setCurrentItem(0);

    }

    private void checkLiveTraining() {

        boolean everythingIsValid = true;

        int fragmentsCount = mDynamicFragmentAdapter.getCount();

        for (int i = 0; i < fragmentsCount; i++) {
            LiveTrainingExerciseFragment exerciseFragment = (LiveTrainingExerciseFragment) mDynamicFragmentAdapter.getRegisteredFragment(i);
            if (!exerciseFragment.isValid()) {
                everythingIsValid = false;
            }
        }

        if (everythingIsValid)
            finishLiveTraining();

    }

    private void finishLiveTraining() {
        int fragmentsCount = mDynamicFragmentAdapter.getCount();

        ArrayList<ArrayMap<String, Object>> exerciseWithSeriesList = new ArrayList<>();
        for (int i = 0; i < fragmentsCount; i++) {

            LiveTrainingExerciseFragment exerciseFragment = (LiveTrainingExerciseFragment) mDynamicFragmentAdapter.getRegisteredFragment(i);
            exerciseWithSeriesList.add(exerciseFragment.getExerciseDataToSummary());
        }


        if (exerciseWithSeriesList.size() > 0) {
            Intent i = new Intent(LiveTrainingActivity.this, LiveTrainingSummaryActivity.class);
            Bundle data = new Bundle();
            data.putString("planId", liveTrainingViewModel.getPlanId());
            data.putString("planName", liveTrainingViewModel.getPlanName());
            data.putSerializable("exercisesWithSeriesList", exerciseWithSeriesList);
            data.putBoolean("afterTraining", true);
            i.putExtras(data);
            startActivity(i);
            finish();
        }


    }

    public void saveSession(int serie, Set set, String exerciseName, FirestoreCompleteCallbackStatus completeListener) {

        liveTrainingManagement.saveSession(serie, sessionId, set, exerciseName, status -> {
            if (status == DbStatus.Success) {
                Toast.makeText(this, "Zapisano pomyślnie", Toast.LENGTH_SHORT).show();
                completeListener.onCallback(status);
            } else {
                Toast.makeText(this, "Błąd w zapisywaniu, spróbuj ponownie", Toast.LENGTH_SHORT).show();
                completeListener.onCallback(status);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.check_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_check: {
                checkLiveTraining();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
