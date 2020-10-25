package com.example.przemek.gymdiary.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.przemek.gymdiary.DbManagement.ExercisesManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.MusculeGroup;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.Validateable;
import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.Models.Post;
import com.example.przemek.gymdiary.R;
import com.google.firebase.auth.FirebaseUser;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseActivity extends AppCompatActivity implements Validateable {
    @BindView(R.id.create_exercise_toolbar)
    Toolbar toolbar;
    @BindView(R.id.create_exercise_name)
    TextInputEditText tiet_name;
    @BindView(R.id.create_exercise_descripiton)
    TextInputEditText tiet_description;
    @BindView(R.id.create_exercise_muscule_group)
    Spinner spinner_muscule_group;
    @BindView(R.id.exercise_photo)
    ImageView photoExercise;
    @BindView(R.id.btn_remove_exercise)
    MaterialFancyButton removeExercise;
    String exerciseId;
    FirebaseUser currentLoggedUser;
    boolean editing = false;
    boolean photoIsChanged = false;

    Uri exercisePhotoUri = null;

    Exercise exercise;

    ExercisesManagement exercisesManagement;

    String message = "Proszę czekać...";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise);
        currentLoggedUser = UserManagement.checkUserLoggedIn();
        ButterKnife.bind(this);


        exercisesManagement = new ExercisesManagement();
        exercise = (Exercise) getIntent().getSerializableExtra("exercise");
        exerciseId = getIntent().getStringExtra("exerciseId");


        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);
        init();
    }

    private void init() {


        photoExercise.setOnClickListener(c -> {
            addExercisePhoto();
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        spinner_muscule_group.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, MusculeGroup.values()));
        if (!TextUtils.isEmpty(exerciseId)) {
            //Jeśli przesłano exerciseId to znaczy że edycja
            editing = true;
            removeExercise.setVisibility(View.VISIBLE);
            removeExercise.setOnClickListener(c -> {
                remove();
            });
            Toast.makeText(this, exerciseId, Toast.LENGTH_SHORT).show();
            if (exercise == null) {
                getExercise(exerciseId);
            }
            updateUI();
            toolbar.setTitle(getString(R.string.edit_exercise));
        }

    }

    private void remove() {
        exercisesManagement.removeExercise(exerciseId, status -> {
            if (status == DbStatus.Success) {
                Toast.makeText(this, "Usunięto ćwiczenie", Toast.LENGTH_SHORT).show();
                setResult(101);
                finish();
            } else
                Toast.makeText(this, "Wystąpił błąd podczas usuwania!", Toast.LENGTH_SHORT).show();
        });
    }

    private void addExercisePhoto() {
        if (Helper.check_storage_permissions(this)) {
            Toast.makeText(this, R.string.permisson_granted, Toast.LENGTH_LONG).show();
            ImagePicker.create(this).returnMode(ReturnMode.CAMERA_ONLY).folderMode(true).toolbarImageTitle("Wybierz zdjęcie").single().showCamera(true).start();

        } else {
            Toast.makeText(this, R.string.permisson_denied, Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void getExercise(String exerciseId) {

        exercisesManagement.getExercise(exerciseId, exercise -> {
            if (exercise != null) {
                this.exercise = exercise;
                updateUI();
            }
        });
    }

    private void updateUI() {
        tiet_name.setText(exercise.getName());
        tiet_description.setText(exercise.getDescription());
        spinner_muscule_group.setSelection(exercise.getMusculeGroup(), true);
        Glide.with(this).setDefaultRequestOptions(Helper.getPlaceholder()).load(exercise.getPhotoUri()).into(photoExercise);
    }

    private void createExercise() {
        MaterialDialog progressDialog = new MaterialDialog.Builder(ExerciseActivity.this)
                .title("Tworzenie ćwiczenie")
                .contentColor(getColor(R.color.white))
                .backgroundColor(getColor(R.color.colorPrimaryDark))
                .titleColor(getColor(R.color.white))
                .content(message)
                .progress(true, 0)
                .canceledOnTouchOutside(false).show();
        exercisesManagement.addExercise(prepareExerciseToAction(), (status, id) -> {
            if (id != null) {
                Toast.makeText(ExerciseActivity.this, "Poprawnie dodano do bazy ćwiczenie", Toast.LENGTH_SHORT).show();
                uploadExercisePhoto(id, progressDialog);

            } else {
                Toast.makeText(ExerciseActivity.this, "Nie dodano ćwiczenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processPost() {

        if (isValid()) {
            createExercise();
        } else
            tiet_name.setError("Musisz nazwać ćwiczenie");

    }

    private void uploadExercisePhoto(String exerciseId, MaterialDialog progressDialog) {
        exercisesManagement.addExercisePhoto(exercisePhotoUri, exerciseId, uri -> {
            exercisesManagement.updateExercisePhotoUri(exerciseId, uri, status -> {
                Toast.makeText(this, "Dodano zdjęcie dla ćwiczenia i jest git generalnie", Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.putExtra("Exercise", prepareExerciseToAction());
                setResult(2, i);
                progressDialog.dismiss();
                finish();

            });
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
                if (!editing)
                    processPost();
                else
                    editExercise();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void editExercise() {
        MaterialDialog progressDialog = new MaterialDialog.Builder(ExerciseActivity.this)
                .title("Edycja ćwiczenia")
                .contentColor(getColor(R.color.white))
                .backgroundColor(getColor(R.color.colorPrimaryDark))
                .titleColor(getColor(R.color.white))
                .content(message)
                .progress(true, 0)
                .canceledOnTouchOutside(false).show();
        if (photoIsChanged) {
            exercisesManagement.addExercisePhoto(exercisePhotoUri, exerciseId, uri -> {
                exercisesManagement.updateExercisePhotoUri(exerciseId, uri, status -> {
                    Toast.makeText(this, "Dodano zdjęcie dla ćwiczenia i jest git generalnie", Toast.LENGTH_SHORT).show();
                    exercisesManagement.updateDocument(this, exerciseId, prepareExerciseToAction(), status2 -> {
                        if (status2 == DbStatus.Success) {
                            Toast.makeText(ExerciseActivity.this, "Pomyślnie zaktualizowano ćwiczenie", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent();
                            i.putExtra("Exercise", prepareExerciseToAction());
                            setResult(2, i);
                            progressDialog.dismiss();
                            finish();
                        } else
                            Toast.makeText(ExerciseActivity.this, "Nie można zaktualizować ćwiczenia", Toast.LENGTH_SHORT).show();

                    });
                });
            });
        } else
            exercisesManagement.updateDocument(this, exerciseId, prepareExerciseToAction(), status -> {
                if (status == DbStatus.Success) {
                    Toast.makeText(ExerciseActivity.this, "Pomyślnie zaktualizowano ćwiczenie", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent();
                    i.putExtra("Exercise", prepareExerciseToAction());
                    setResult(2, i);
                    progressDialog.dismiss();
                    finish();
                } else
                    Toast.makeText(ExerciseActivity.this, "Nie można zaktualizować ćwiczenia", Toast.LENGTH_SHORT).show();

            });


    }

    private Exercise prepareExerciseToAction() {

        String userId = currentLoggedUser.getUid();
        Exercise exercise = new Exercise();
        exercise.setUserId(userId);
        exercise.setName(tiet_name.getText().toString());
        exercise.setDescription(tiet_description.getText().toString());
        int group = spinner_muscule_group.getSelectedItemPosition();
        exercise.setMusculeGroup(group);

        return exercise;
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            exercisePhotoUri = Uri.fromFile(new File(image.getPath()));
            Glide.with(this).load(exercisePhotoUri).into(photoExercise);
            if (editing)
                photoIsChanged = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);
        super.onResume();
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(tiet_name.getText());
    }
}
