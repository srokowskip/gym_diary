package com.example.przemek.gymdiary.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przemek.gymdiary.Adapters.FirestoreAdapters.ExerciseFirestoreRecyclerAdapter;
import com.example.przemek.gymdiary.DbManagement.DefinedExercisesManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.RecyclerViewClickListener;
import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.R;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstantExercisesActivity extends AppCompatActivity {

    @BindView(R.id.instant_exercises_activity_list)
    RecyclerView exercisesList;
    @BindView(R.id.instant_exercises_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.defined_exercises_question)
    TextView tvQuestion;
    @BindView(R.id.defined_exercises_group)
    RadioGroup exerciseMusculeGroups;
    @BindView(R.id.defined_exercises_button)
    Button buttonGet;
    ExerciseFirestoreRecyclerAdapter mAdapter;
    FirebaseUser currentLoggedUser;

    private int musculeGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_exercises);
        ButterKnife.bind(this);
        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);
        init();


    }

    private void init() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        exercisesList.setLayoutManager(mLinearLayoutManager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        buttonGet.setOnClickListener(c -> {

            int pickedGroup = exerciseMusculeGroups.getCheckedRadioButtonId();

            if (pickedGroup == -1) {
                Toast.makeText(this, "Wybierz grupę", Toast.LENGTH_SHORT).show();
            } else {
                switch (pickedGroup) {
                    case R.id.muscule_group_chest:
                        musculeGroup = 0;
                        break;
                    case R.id.muscule_group_legs:
                        musculeGroup = 1;
                        break;
                    case R.id.muscule_group_arms:
                        musculeGroup = 2;
                        break;
                    case R.id.muscule_group_back:
                        musculeGroup = 3;
                        break;
                }
            }
            tvQuestion.setVisibility(View.GONE);
            exerciseMusculeGroups.setVisibility(View.GONE);
            buttonGet.setVisibility(View.GONE);
            getExercises(musculeGroup);
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(1, null);
        finish();
    }

    private void getExercises(int group) {
        exercisesList.setVisibility(View.VISIBLE);
        RecyclerViewClickListener listener = (View view, int position, String exerciseId, Exercise definedExercise) -> {
            Intent i = new Intent();
            i.putExtra("Exercise", definedExercise);
            setResult(1, i);
            finish();
        };
        DefinedExercisesManagement definedExercisesManagement = new DefinedExercisesManagement();

        mAdapter = new ExerciseFirestoreRecyclerAdapter(definedExercisesManagement.getDefinedExercises(group), this, listener);
        mAdapter.startListening();
        //TODO empty view : http://akbaribrahim.com/empty-view-for-androids-recyclerview/
        mAdapter.notifyDataSetChanged();
        exercisesList.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null)
            mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);
        super.onResume();
    }
}
