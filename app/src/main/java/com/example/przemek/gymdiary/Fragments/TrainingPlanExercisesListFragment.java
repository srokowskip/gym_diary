package com.example.przemek.gymdiary.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.przemek.gymdiary.Activities.MyExercisesActivity;
import com.example.przemek.gymdiary.Activities.TrainingPlanActivity;
import com.example.przemek.gymdiary.Adapters.ExerciseRecyclerAdapter;
import com.example.przemek.gymdiary.Interfaces.RecyclerViewClickListener;
import com.example.przemek.gymdiary.Interfaces.Validateable;
import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.TrainingPlan;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.TrainingPlanViewModel;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingPlanExercisesListFragment extends Fragment implements Validateable {

    @BindView(R.id.exercises_list)
    RecyclerView exerciseList;
    @BindView(R.id.training_plan_exercises_list_fab)
    FloatingActionButton fab;
    TrainingPlan mModel;
    LinearLayoutManager mLayoutManager;
    ExerciseRecyclerAdapter adapter;
    ArrayList<HelpfulExercise> listOfExercises = new ArrayList<>();
    final static int PICK_EXERCISE_REQUEST = 1;
    boolean editing = false;
    TrainingPlanActivity parentActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        parentActivity = (TrainingPlanActivity) getActivity();
        editing = parentActivity.isEditing();
        mModel = ViewModelProviders.of(getActivity()).get(TrainingPlanViewModel.class).getPlan();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        if (editing)
            listOfExercises.addAll(mModel.getExerciseList());

        RecyclerViewClickListener listenerOnChoosenExercises = (View view, int position, String exerciseId, Exercise definedExercise) -> {
        };
        mLayoutManager = new LinearLayoutManager(getActivity());
        exerciseList.setLayoutManager(mLayoutManager);
        adapter = new ExerciseRecyclerAdapter(listOfExercises, exerciseList, listenerOnChoosenExercises, true, getActivity());
        adapter.notifyDataSetChanged();
        mModel.setExerciseList(listOfExercises);
        exerciseList.setAdapter(adapter);
        fab.setOnClickListener(v -> getExercise());
        exerciseList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide(true);
                } else {
                    fab.show(true);
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getExercise() {
        Intent i = new Intent(getActivity(), MyExercisesActivity.class);
        ArrayList<String> listOfIds = new ArrayList<>();

        for (HelpfulExercise e : listOfExercises
                ) {
            listOfIds.add(e.getId());
        }
        i.putStringArrayListExtra("alreadyPresentExercisesIds", listOfIds);
        i.putExtra("pickRequest", "true");
        startActivityForResult(i, PICK_EXERCISE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_EXERCISE_REQUEST) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                ArrayList<HelpfulExercise> receivedExercises = (ArrayList<HelpfulExercise>) bundle.getSerializable("chosenExercise");

                if (!receivedExercises.isEmpty()) {
                    listOfExercises.addAll(receivedExercises);
                    mModel.setExerciseList(listOfExercises);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), "Nie dodano ćwiczeń", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private boolean checkExercisesCount() {

        if (mModel.getExerciseList().isEmpty()) {
            Toast.makeText(parentActivity, "Musisz dodać przynajmniej jedno ćwiczenie", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        mModel = null;
        super.onDestroy();
    }

    @Override
    public boolean isValid() {

        if (!checkExercisesCount())
            return false;

        return true;
    }
}
