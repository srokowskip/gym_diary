package com.example.przemek.gymdiary.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.przemek.gymdiary.Adapters.SummaryExercisesAdapter;
import com.example.przemek.gymdiary.DbManagement.TrainingPlansHistoryManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.Models.ExerciseHistory;
import com.example.przemek.gymdiary.Models.Set;
import com.example.przemek.gymdiary.Models.TrainingPlanHistory;
import com.example.przemek.gymdiary.R;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveTrainingSummaryActivity extends AppCompatActivity {

    @BindView(R.id.rv_training_summary_exercises_list)
    RecyclerView recyclerView;
//    @BindView(R.id.tv_training_summary_planName)
//    TextView trainingTitle;
    @BindView(R.id.btn_training_summary_ok)
    Button savePlanProgressButton;
    @BindView(R.id.training_summary_toolbar)
    Toolbar toolbar;


    ArrayList<Map<String, Object>> exerciseWithSeriesList = new ArrayList<>();
    String planName;
    String planId;

    private boolean afterTraining;

    private ArrayList<ExerciseHistory> listOfExercisesWithSeries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_training_summary);

        ButterKnife.bind(this);


        //exercise //listOfSets
        afterTraining = getIntent().getBooleanExtra("afterTraining", false);

        if (afterTraining) {
            planName = getIntent().getStringExtra("planName");
            planId = getIntent().getStringExtra("planId");

            exerciseWithSeriesList = (ArrayList<Map<String, Object>>) getIntent().getSerializableExtra("exercisesWithSeriesList");
            savePlanProgressButton.setVisibility(View.VISIBLE);
            savePlanProgressButton.setOnClickListener(c -> {
                performAddHistory();
            });
            convertData();

        } else {
            getSummaryData();
        }


    }

    private void getSummaryData() {

        String historyId = getIntent().getStringExtra("historyId");

        TrainingPlansHistoryManagement trainingPlansHistoryManagement = new TrainingPlansHistoryManagement();
        trainingPlansHistoryManagement.getPlanHistoryDataById(historyId, result -> {
            if (result != null) {
                listOfExercisesWithSeries = (ArrayList<ExerciseHistory>) result;
                initView();

            } else {
            }
        });

    }

    private void convertData() {

        for (Map<String, Object> exerciseHistoryMap : exerciseWithSeriesList
                ) {
            Exercise e = (Exercise) exerciseHistoryMap.get("exercise");
            ArrayList<Set> listOfSets = (ArrayList<Set>) exerciseHistoryMap.get("listOfSets");
            listOfExercisesWithSeries.add(new ExerciseHistory(listOfSets, e.getName()));
            initView();

        }
    }

    private void performAddHistory() {

        TrainingPlansHistoryManagement trainingPlansHistoryManagement = new TrainingPlansHistoryManagement();
        TrainingPlanHistory trainingPlanHistory = new TrainingPlanHistory(trainingPlansHistoryManagement.getCurrentUserId(), planId);

        trainingPlansHistoryManagement.addTrainingPlanHistoryAndRemoveSession(planId, exerciseWithSeriesList, trainingPlanHistory, status -> {
            if (status == DbStatus.Success) {
                Toast.makeText(this, "Jest Git", Toast.LENGTH_SHORT).show();
                finish();
            } else
                Toast.makeText(this, "Błąd", Toast.LENGTH_SHORT).show();
        });

    }

    private void initView() {



        SummaryExercisesAdapter adapter = new SummaryExercisesAdapter(listOfExercisesWithSeries, recyclerView, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
