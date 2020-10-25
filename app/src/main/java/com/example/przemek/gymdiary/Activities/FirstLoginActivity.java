package com.example.przemek.gymdiary.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.przemek.gymdiary.Adapters.StepperAdapter;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.User;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.PhotoViewModel;
import com.example.przemek.gymdiary.ViewModels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.gson.reflect.TypeToken.get;


public class FirstLoginActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    StepperLayout mStepperLayout;
    UserViewModel mModel;
    PhotoViewModel photoViewModel;
    @BindView(R.id.firstlogin_layout)
    ConstraintLayout layFirstLogin;
    @BindView(R.id.firstlogin_toolbar)
    Toolbar toolbar;
    User user;
    FirebaseUser currentLoggedUser;

    UserManagement userManagement;
    private String userCollection = FirestoreCollections.Users.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_first_login_stepper);
        ButterKnife.bind(this);
        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);

        userManagement = new UserManagement();

        init();


    }

    private void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mStepperLayout = findViewById(R.id.stepperLayout);
        mModel = ViewModelProviders.of(this).get(UserViewModel.class);
        photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel.class);
        user = new User();
        mModel.setUser(user);
        StepperAdapter mStepperAdapter = new StepperAdapter(getSupportFragmentManager(), this);
        mStepperLayout.setAdapter(mStepperAdapter);
        mStepperLayout.setListener(this);
    }

    @Override
    public void onCompleted(View completeButton) {

        user = mModel.getUser();
        writeNewUser(user);

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {
    }

    @Override
    public void onReturn() {


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void writeNewUser(User user) {


        final MaterialDialog progressDialog = new MaterialDialog.Builder(this)
                .contentColor(getColor(R.color.white))
                .backgroundColor(getColor(R.color.colorPrimaryDark))
                .titleColor(getColor(R.color.white))
                .title("Aktualizujemy Twoje konto")
                .content(R.string.please_wait)
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .show();


        userManagement.checkNickAvailability(user.getNick(), new FirestoreCompleteCallbackStatus() {
            @Override
            public void onCallback(DbStatus status) {
                if (status == DbStatus.Success) {
                    uploadUserPhoto();
                } else {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(layFirstLogin, R.string.nick_used, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.ok, c -> snackbar.dismiss());
                    snackbar.show();
                }
            }

            private void uploadUserPhoto() {

                userManagement.addUserProfilePhoto(currentLoggedUser.getUid(), photoViewModel.getLocalPhotoUri() , data -> {
                    if (data != null) {
                        Toast.makeText(FirstLoginActivity.this, "Dodano zdjęcie", Toast.LENGTH_SHORT).show();
                        user.setProfilePhotoUrl(data.toString());
                        addUserData();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(FirstLoginActivity.this, "Problem z dodaniem zdjęcia", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void addUserData() {
                userManagement.fillUserData(currentLoggedUser.getUid(), user, status -> {

                    if (status == DbStatus.Success) {
                        progressDialog.dismiss();
                        Intent i = new Intent(FirstLoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(FirstLoginActivity.this, "Wystąpił błąd", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });
    }

    @Override
    public void onBackPressed() {
        UserManagement.logout();
        finish();
    }

    @Override
    protected void onResume() {
        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);
        super.onResume();
    }
}