package com.example.przemek.gymdiary.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.przemek.gymdiary.Adapters.FirestoreAdapters.TrainingPlanHistoryFirestoreRecyclerAdapter;
import com.example.przemek.gymdiary.DbManagement.TrainingPlansHistoryManagement;
import com.example.przemek.gymdiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingPlanHistoryListActivity extends AppCompatActivity {

    @BindView(R.id.training_plan_history_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_training_plan_history)
    RecyclerView recyclerView;

    LinearLayoutManager mLinearLayoutManager;
    TrainingPlanHistoryFirestoreRecyclerAdapter mAdapter;

    private String planId;
    private String planTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_plan_history);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        planId = getIntent().getStringExtra("planId");
        planTitle = getIntent().getStringExtra("planTitle");

        if (!planId.isEmpty() && !planTitle.isEmpty()) {

            toolbar.setTitle("Historia: " + planTitle);
            fetchHistory();
        }

    }

    private void fetchHistory() {

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        TrainingPlansHistoryManagement trainingPlansHistoryManagement = new TrainingPlansHistoryManagement();
        mAdapter = new TrainingPlanHistoryFirestoreRecyclerAdapter(trainingPlansHistoryManagement.getPlanHistoryListByPlanId(planId), this);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
