package com.example.przemek.gymdiary.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.przemek.gymdiary.Adapters.TrainingPlanHistoryAdapter;
import com.example.przemek.gymdiary.DbManagement.TrainingPlansHistoryManagement;
import com.example.przemek.gymdiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendTrainingHistoryActivity extends AppCompatActivity {
    private String userId;
    @BindView(R.id.recyclerView)
    RecyclerView historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_training_history);
        ButterKnife.bind(this);

        userId = getIntent().getStringExtra("userId");
        fetchTrainingsHistory();
    }

    private void fetchTrainingsHistory() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        historyList.setLayoutManager(mLinearLayoutManager);

        TrainingPlansHistoryManagement trainingPlansHistoryManagement = new TrainingPlansHistoryManagement();
        trainingPlansHistoryManagement.getUserHistory(userId, history -> {
            TrainingPlanHistoryAdapter adapter = new TrainingPlanHistoryAdapter(history, this);
            adapter.notifyDataSetChanged();
            historyList.setAdapter(adapter);
        });
    }
}
