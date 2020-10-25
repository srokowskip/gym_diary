package com.example.przemek.gymdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.przemek.gymdiary.Adapters.ExerciseRecyclerAdapter;
import com.example.przemek.gymdiary.DbManagement.ExercisesManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Dialogs.ExercisePropertiesDialogFragment;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.RecyclerViewClickListener;
import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyExercisesActivity extends AppCompatActivity implements ExercisePropertiesDialogFragment.OnSetExercisePreferencesListener, SearchView.OnQueryTextListener {

    @BindView(R.id.parent2)
    ConstraintLayout parent;
    @BindView(R.id.my_exercises_list)
    RecyclerView myExerciseRecycler;
    @BindView(R.id.choosen_exercises_list)
    RecyclerView choosenExercisesRecycler;
    ExerciseRecyclerAdapter myExercisesAdapter;
    ExerciseRecyclerAdapter recyclerAdapter;
    ArrayList<HelpfulExercise> choosenExercisesList = new ArrayList<>();
    ArrayList<String> alreadyChosenExercises = new ArrayList<>();
    @BindView(R.id.choosen_exercises_card)
    CardView choosenExercisesCard;
    @BindView(R.id.exercise_list_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_create_exercise)
    FloatingActionButton fab_create_exercise;
    @BindView(R.id.fab_choose_exercise)
    FloatingActionButton fab_choose_exercise;
    @BindView(R.id.exercises_choosen_exercises_expand)
    ImageView expand_btn;
    @BindView(R.id.menu)
    FloatingActionMenu fam;
    ArrayList<HelpfulExercise> userExercises = new ArrayList<>();

    String userId = null;

    FirebaseUser currentLoggedUser;

    final int PICK_DEFINED_EXERCISE_REQUEST = 1;
    final int CREATE_EXERCISE_REQUEST = 2;
    boolean pickRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        ButterKnife.bind(this);

        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);

        userId = currentLoggedUser.getUid();

        Bundle b = getIntent().getExtras();

        if (b != null) {
            if (getIntent().getExtras().getString("pickRequest").equals("true")) {
                pickRequest = true;
                alreadyChosenExercises = getIntent().getStringArrayListExtra("alreadyPresentExercisesIds");
                toolbar.setTitle("Wybierz ćwiczenia");
                Toast.makeText(this, "PickRequest", Toast.LENGTH_SHORT).show();
            }
        }
        init();
    }

    private void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myExerciseRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fam.hideMenuButton(true);
                } else {
                    fam.showMenuButton(true);
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        fab_create_exercise.setOnClickListener(c -> {
            Intent i = new Intent(MyExercisesActivity.this, ExerciseActivity.class);
            i.putExtra("userId", userId);
            startActivityForResult(i, CREATE_EXERCISE_REQUEST);
        });
        fab_choose_exercise.setOnClickListener(c -> {
            Intent i = new Intent(MyExercisesActivity.this, InstantExercisesActivity.class);
            startActivityForResult(i, PICK_DEFINED_EXERCISE_REQUEST);
        });
        if (pickRequest)
            setChoosenExercisesAdapter();

        getMyExercises();

    }

    private void setChoosenExercisesAdapter() {

        RecyclerViewClickListener listenerOnChoosenExercises = (View view, int position, String exerciseId, Exercise definedExercise) -> {
        };
        recyclerAdapter = new ExerciseRecyclerAdapter(choosenExercisesList, choosenExercisesRecycler, listenerOnChoosenExercises, true, this);
        choosenExercisesRecycler.setHasFixedSize(true);
        choosenExercisesRecycler.setLayoutManager(new LinearLayoutManager(this));
        choosenExercisesRecycler.setItemAnimator(new DefaultItemAnimator());
        choosenExercisesRecycler.setAdapter(recyclerAdapter);

        expand_btn.setOnClickListener(l -> {
            if (choosenExercisesRecycler.getVisibility() == View.GONE) {
                choosenExercisesRecycler.setVisibility(View.VISIBLE);
                expand_btn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            } else {
                choosenExercisesRecycler.setVisibility(View.GONE);
                expand_btn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            }
        });
        recyclerAdapter.notifyDataSetChanged();

    }

    private void getMyExercises() {

        if (userExercises.size() > 0) {
            userExercises.clear();
            myExercisesAdapter.notifyDataSetChanged();
        }

        RecyclerViewClickListener listenerOnMyExercises = (View view, int position, String exerciseId, Exercise definedExercise) -> {
            handleClickOnExercise(exerciseId, definedExercise);
        };
        myExercisesAdapter = new ExerciseRecyclerAdapter(userExercises, myExerciseRecycler, listenerOnMyExercises, false, this);
        myExerciseRecycler.setHasFixedSize(true);
        myExerciseRecycler.setLayoutManager(new LinearLayoutManager(this));
        myExerciseRecycler.setItemAnimator(new DefaultItemAnimator());
        myExerciseRecycler.setAdapter(myExercisesAdapter);

        ExercisesManagement exercisesManagement = new ExercisesManagement();

        exercisesManagement.getUsersExercises(userId, response -> {
            userExercises.addAll(response);
            myExercisesAdapter.notifyDataSetChanged();
        });

    }

    private void handleClickOnExercise(String id, Exercise exercise) {
        if (pickRequest) {
            if (checkIfExerciseAlreadyPresentOnList(id)) {
                Toast.makeText(this, "Ćwiczenie jest już na liście !", Toast.LENGTH_SHORT).show();
            } else {

                if (choosenExercisesRecycler.getVisibility() == View.VISIBLE) {
                    choosenExercisesRecycler.setVisibility(View.GONE);
                    expand_btn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }

                ExercisePropertiesDialogFragment dialog = new ExercisePropertiesDialogFragment();
                HelpfulExercise exerciseWithId = new HelpfulExercise(id, exercise);
                Bundle arg = new Bundle();
                arg.putSerializable("exercise", exerciseWithId);
                dialog.setArguments(arg);
                dialog.show(getSupportFragmentManager(), "ExercisePropertiesDialog");
            }
        } else {
            Intent i = new Intent(MyExercisesActivity.this, ExerciseActivity.class);
            i.putExtra("exerciseId", id);
            i.putExtra("exercise", exercise);
            startActivityForResult(i, 101);
        }
    }

    private boolean checkIfExerciseAlreadyPresentOnList(String id) {

        for (HelpfulExercise e : choosenExercisesList
                ) {
            if (e.getId().equals(id))
                return true;
        }

        for (String exerciseId : alreadyChosenExercises) {
            if (exerciseId.equals(id))
                return true;
        }
        return false;
    }

    private void proceed() {

        Intent i = new Intent();
        Bundle args = new Bundle();
        args.putSerializable("chosenExercise", choosenExercisesList);
        i.putExtras(args);
        setResult(1, i);
        finish();

    }

    private void copyExerciseToUser(Exercise definedExercise) {
        if (definedExercise != null) {
            definedExercise.setUserId(userId);
            ExercisesManagement exercisesManagement = new ExercisesManagement();
            exercisesManagement.addExercise(definedExercise, (status, id) -> {
                if (status == DbStatus.Success) {
                    //TODO pomyśl
                    getMyExercises();
                    Toast.makeText(getApplicationContext(), "Ćwiczenie zostało pomyślnie dodane do Twoich ćwiczeń", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wystąpił błąd przy dodawaniu ćwiczenia", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void sendInput(HelpfulExercise exercise) {
        if (pickRequest) {
            if (exercise != null) {
                choosenExercisesCard.setVisibility(View.VISIBLE);
                expand_btn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                choosenExercisesList.add(exercise);
            } else
                Toast.makeText(this, "Nie można dodać ćwiczenia", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {

            getMyExercises();
        }
        if (data != null) {
            if (requestCode == PICK_DEFINED_EXERCISE_REQUEST) {
                Exercise exercise = (Exercise) data.getSerializableExtra("Exercise");
                Log.d("Dostałem", exercise.getName() + " " + exercise.getDescription() + " " + exercise.getMusculeGroup());
                copyExerciseToUser(exercise);
            }
            if (requestCode == CREATE_EXERCISE_REQUEST) {
                Exercise exercise = (Exercise) data.getSerializableExtra("Exercise");
                Log.d("Dostałem", exercise.getName() + " " + exercise.getDescription() + " " + exercise.getMusculeGroup());
                getMyExercises();
            }
        } else Toast.makeText(this, "Nie przesłano nic", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_exercises_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        MenuItem menuItemCheck = menu.findItem(R.id.action_check);
        menuItemCheck.setVisible(pickRequest);

        searchView.setOnQueryTextListener(this);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_check: {
                proceed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (pickRequest) {
            setResult(1, null);
            finish();
        }
    }

    @Override
    protected void onResume() {
//        firestoreAdapter.notifyDataSetChanged();
        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putSerializable("choosenExercises", choosenExercisesList);
        savedInstanceState.putInt("recyclerVisible", choosenExercisesRecycler.getVisibility());

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        ArrayList<HelpfulExercise> savedExercisesList = (ArrayList<HelpfulExercise>) savedInstanceState.getSerializable("choosenExercises");


        if (!savedExercisesList.isEmpty()) {
            choosenExercisesList.addAll(savedExercisesList);
            choosenExercisesCard.setVisibility(View.VISIBLE);

            if (savedInstanceState.getInt("recyclerVisible") == View.VISIBLE) {
                choosenExercisesRecycler.setVisibility(View.VISIBLE);
                expand_btn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            } else {
                choosenExercisesRecycler.setVisibility(View.GONE);
                expand_btn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            }

        }
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        String userInput = s.toLowerCase();

        ArrayList<HelpfulExercise> updatedList = new ArrayList<>();

        for (HelpfulExercise e : userExercises
                ) {
            if (e.getName().contains(userInput))
                updatedList.add(e);
        }

        myExercisesAdapter.updateList(updatedList);

        return false;
    }
}
