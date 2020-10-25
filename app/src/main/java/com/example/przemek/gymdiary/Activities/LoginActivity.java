package com.example.przemek.gymdiary.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;

    @BindView(R.id.login_btn_signin)
    Button btn_login;
    @BindView(R.id.login_btn_signin_google)
    SignInButton btn_google;

    @BindView(R.id.login_tv_create_account)
    TextView tv_create_account;
    @BindView(R.id.login_tv_forgot_password)
    TextView tv_forgot_password;
    @BindView(R.id.login_tv_left)
    TextView tv_left;
    @BindView(R.id.login_tv_right)
    TextView tv_right;
    @BindView(R.id.login_tv_or)
    TextView tv_or;

    @BindView(R.id.login_tiet_email)
    TextInputEditText tiet_email;
    @BindView(R.id.login_tiet_password)
    TextInputEditText tiet_password;

    @BindView(R.id.login_layout)
    View lay_login;

    @BindView(R.id.login_pb)
    ProgressBar pb;

    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 997;
    String TAG = "Login Activity";

    UserManagement userManagement = new UserManagement();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        pb.setVisibility(View.GONE);
        btn_login.setOnClickListener(this);
        tv_create_account.setOnClickListener(this);
        btn_google.setOnClickListener(this);
        tv_forgot_password.setOnClickListener(this);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_purple));

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.login_btn_signin:

                String email = tiet_email.getText().toString();
                String password = tiet_password.getText().toString();

                if (TextUtils.isEmpty(email)) {

                    pbDisappear();
                    Snackbar.make(lay_login, R.string.empty_email, Snackbar.LENGTH_LONG).show();

                } else if (TextUtils.isEmpty(password)) {

                    pbDisappear();
                    Snackbar.make(lay_login, R.string.empty_password, Snackbar.LENGTH_LONG).show();

                } else {

                    pbAppear();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    pbDisappear();
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
                                    userManagement.getCurrentUser(user -> {
                                        if (user == null)
                                            takeToMain(user);
                                        else if (!user.getIsBanned())
                                            takeToMain(user);
                                        else {
                                            Toast.makeText(this, "Twoje konto jest zablokowane, skontaktuj się z administracją", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    pbDisappear();
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    try {

                                        throw task.getException();

                                    } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException e) {

                                        Snackbar.make(lay_login, R.string.exception_login_credentials, Snackbar.LENGTH_LONG).show();

                                    } catch (Exception e) {

                                        Log.e(TAG, e.getMessage());

                                    }
                                }
                            });
                }
                break;

            case R.id.login_tv_create_account:

                takeToCreateAccount();

                break;

            case R.id.login_btn_signin_google:

                signIn();

                break;
            case R.id.login_tv_forgot_password:
                recoverPassword();
                break;
        }

    }

    private void recoverPassword() {
        Intent i = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(i);
    }

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        userManagement.getCurrentUser(user -> {
                            if (user == null) {
                                takeToMain(user);
                            } else if (!user.getIsBanned())
                                takeToMain(user);
                            else {
                                Toast.makeText(this, "Twoje konto jest zablokowane, skontaktuj się z administracją", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }

                });
    }


    private void pbDisappear() {

        btn_login.setVisibility(View.VISIBLE);
        btn_google.setVisibility(View.VISIBLE);
        tv_or.setVisibility(View.VISIBLE);
        tv_left.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);

    }

    private void pbAppear() {

        btn_login.setVisibility(View.GONE);
        btn_google.setVisibility(View.GONE);
        tv_or.setVisibility(View.GONE);
        tv_left.setVisibility(View.GONE);
        tv_right.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);


    }

    private void takeToMain(HelpfulUser user) {

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        finish();

    }

    private void takeToCreateAccount() {

        Intent i = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            userManagement.getCurrentUser(user -> {
//                if (user == null)
//                    takeToMain(null);
//                else if (!user.getIsBanned()) {
//                    takeToMain(user);
//                } else {
//                    Toast.makeText(this, "Twoje konto jest zablokowane, skontaktuj się z administracją.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }

}



