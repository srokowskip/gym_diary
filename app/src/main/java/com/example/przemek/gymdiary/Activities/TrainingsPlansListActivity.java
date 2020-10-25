package com.example.przemek.gymdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.przemek.gymdiary.Adapters.FirestoreAdapters.TrainingPlanFirestoreRecyclerAdapter;
import com.example.przemek.gymdiary.DbManagement.TrainingPlansManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.VerticalSpaceItemDecorator;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingsPlansListActivity extends AppCompatActivity {

    LinearLayoutManager mLinearLayoutManager;
    TrainingPlanFirestoreRecyclerAdapter mAdapter;
    @BindView(R.id.training_plans_list)
    RecyclerView trainingPlansList;
    @BindView(R.id.training_plans_fab)
    FloatingActionButton fab;
    @BindView(R.id.training_plans_list_toolbar)
    Toolbar toolbar;
    String userId;

    FirebaseUser currentLoggedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_plans_list);

        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);

        userId = currentLoggedUser.getUid();

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        trainingPlansList.setLayoutManager(mLinearLayoutManager);
        getTrainingPlans();
        fab.setOnClickListener(c -> takeToCreatePlan());

        trainingPlansList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });

    }

    private void getTrainingPlans() {
        TrainingPlansManagement trainingPlansManagement = new TrainingPlansManagement();
        trainingPlansList.addItemDecoration(new VerticalSpaceItemDecorator(48));
        mAdapter = new TrainingPlanFirestoreRecyclerAdapter(trainingPlansManagement.getUsersTrainingPlans(userId), this);
        mAdapter.notifyDataSetChanged();
        trainingPlansList.setAdapter(mAdapter);
    }

    private void takeToCreatePlan() {
        Intent i = new Intent(TrainingsPlansListActivity.this, TrainingPlanActivity.class);
        startActivity(i);
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        mAdapter.notifyDataSetChanged();
        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);
        super.onResume();

    }
}
