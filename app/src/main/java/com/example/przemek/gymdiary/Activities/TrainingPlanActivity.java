package com.example.przemek.gymdiary.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.przemek.gymdiary.Adapters.TrainingPlanViewPagerAdapter;
import com.example.przemek.gymdiary.DbManagement.TrainingPlansManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Fragments.TrainingPlanExercisesListFragment;
import com.example.przemek.gymdiary.Fragments.TrainingPlanInfoFragment;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.TrainingPlan;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.TrainingPlanViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingPlanActivity extends AppCompatActivity {
    @BindView(R.id.training_plan_toolbar)
    Toolbar toolbar;
    @BindView(R.id.training_plan_pager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    TrainingPlanViewModel mModel;
    TrainingPlanInfoFragment trainingPlanInfoFragment = new TrainingPlanInfoFragment();
    TrainingPlanExercisesListFragment trainingPlanExercisesListFragment = new TrainingPlanExercisesListFragment();
    TrainingPlanViewPagerAdapter adapter;
    String userId;
    FirebaseUser currentLoggedUser;
    String planId;
    boolean editing = false;

    TrainingPlansManagement trainingPlansManagement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_plan);
        ButterKnife.bind(this);

        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);

        userId = currentLoggedUser.getUid();

        trainingPlansManagement = new TrainingPlansManagement();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mModel = ViewModelProviders.of(this).get(TrainingPlanViewModel.class);
        init();
        Log.d("ac", "onCreate");
    }

    private void init() {
        String title = getIntent().getStringExtra("title");
        planId = getIntent().getStringExtra("id");
        String editingExtras = getIntent().getStringExtra("editing");
        if (title != null || planId != null || editingExtras != null) {
            getPlan(planId);
            editing = true;
            toolbar.setTitle(title);
            Toast.makeText(this, "Edycja", Toast.LENGTH_SHORT).show();
            //TODO naprawić
        } else {
            if (!isEditing()) {
                Toast.makeText(this, "Tworzenie", Toast.LENGTH_SHORT).show();
                TrainingPlan plan = new TrainingPlan();
                mModel.setPlan(plan);
                setAdapter();
            }
        }

    }

    private void getPlan(String id) {

        trainingPlansManagement.getTrainingPlan(id, trainingPlan -> {
            if (trainingPlan != null) {
                mModel.setPlan(trainingPlan);
                setAdapter();

            } else {
                Toast.makeText(this, "nie można pobrać planu", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_plan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save: {
                if (!editing)
                    savePlan();
                else
                    editPlan();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void editPlan() {

        trainingPlansManagement.updateDocument(this, planId, preparePlanToAction(), status -> {

            if (status == DbStatus.Success) {
                Toast.makeText(TrainingPlanActivity.this, "Pomyślnie zaktualizowano plan", Toast.LENGTH_SHORT).show();
                finish();
            } else
                Toast.makeText(TrainingPlanActivity.this, "Nie można zaktualizować planu", Toast.LENGTH_SHORT).show();

        });
    }

    //TODO FAB
    private void savePlan() {
        if (trainingPlanInfoFragment.isValid() && trainingPlanExercisesListFragment.isValid()) {
            trainingPlansManagement.addTrainingPlan(preparePlanToAction(), status -> {
                if (status != DbStatus.Success) {
                    Toast.makeText(this, "Nie udało się dodać planu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Plan został dodany", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    }
    }

    private TrainingPlan preparePlanToAction() {
        TrainingPlan trainingPlan = mModel.getPlan();
        trainingPlan.setUserId(userId);

        return trainingPlan;
    }


    @Override
    public void onBackPressed() {
        getParentActivityIntent();
        super.onBackPressed();
    }

    private void setAdapter() {
        adapter = new TrainingPlanViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(trainingPlanInfoFragment);
        adapter.addFragment(trainingPlanExercisesListFragment);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    public boolean isEditing() {
        return editing;
    }

    @Override
    protected void onResume() {
        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);
        super.onResume();
    }
}
