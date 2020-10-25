package com.example.przemek.gymdiary.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.przemek.gymdiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {
    private TextInputEditText tiet_email;
    private TextInputEditText tiet_password;
    private TextInputEditText tiet_password_rep;
    private View lay_login;
    private Button btn_create_acc;
    private Button btn_back;
    private FirebaseAuth mAuth;
    private final String TAG = "Create account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        lay_login = (View) findViewById(R.id.lay_create_acc);
        tiet_email = (TextInputEditText) findViewById(R.id.reg_tiet_email);
        tiet_password = (TextInputEditText) findViewById(R.id.reg_tiet_password);
        tiet_password_rep = (TextInputEditText) findViewById(R.id.reg_tiet_password_rep);
        btn_create_acc = (Button) findViewById(R.id.login_btn_signin);
        btn_back = (Button) findViewById(R.id.reg_btn_have_acc);


        btn_create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInfo();
            }

        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeToLogin();
            }
        });


    }


    private void checkInfo() {
        String password = tiet_password.getText().toString();
        String password2 = tiet_password_rep.getText().toString();

        if (tiet_email.toString().trim().isEmpty() || tiet_email.getText().toString().trim().equals("")) {

            Snackbar.make(lay_login, R.string.empty_email, Snackbar.LENGTH_LONG).show();

        } else if (tiet_password.toString().trim().isEmpty() || tiet_password.getText().toString().trim().equals("")) {

            Snackbar.make(lay_login, R.string.empty_password, Snackbar.LENGTH_LONG).show();

        } else if (tiet_password_rep.toString().trim().isEmpty() || tiet_password_rep.getText().toString().trim().equals("")) {

            Snackbar.make(lay_login, R.string.empty_password, Snackbar.LENGTH_LONG).show();

        } else if (password.equals(password2)) {

            createNewUser();


        } else {

            Log.d(TAG, tiet_password.getText() + " " + tiet_password_rep.getText());
            Snackbar.make(lay_login, R.string.different_password, Snackbar.LENGTH_LONG).show();

        }
    }

    private void takeToMain() {
        Intent i = new Intent(CreateAccountActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void takeToLogin() {
        Intent i = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void createNewUser() {

        final MaterialDialog progressDialog = new MaterialDialog.Builder(CreateAccountActivity.this)
                .title(R.string.dialog_create_account_title)
                .contentColor(getColor(R.color.white))
                .backgroundColor(getColor(R.color.colorPrimaryDark))
                .titleColor(getColor(R.color.white))
                .content(R.string.please_wait)
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .show();

        String email = tiet_email.getText().toString().trim();
        String password = tiet_password.getText().toString().trim();
        Log.d(TAG, " przekazywane parametry : " + email + " " + password);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                Toast.makeText(CreateAccountActivity.this, "Wystąpił błąd", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                            Snackbar.make(lay_login, R.string.sb_create_acc_success_, Snackbar.LENGTH_LONG)
                                    .show();
                            takeToLogin();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progressDialog.dismiss();
                            try {

                                throw task.getException();

                            } catch (FirebaseAuthWeakPasswordException e) {

                                Snackbar.make(lay_login, R.string.exception_password, Snackbar.LENGTH_LONG)
                                        .show();
                            } catch (FirebaseAuthEmailException e) {

                                Snackbar.make(lay_login, R.string.exception_email, Snackbar.LENGTH_LONG)
                                        .show();
                            } catch (FirebaseAuthUserCollisionException e) {

                                Snackbar.make(lay_login, R.string.exception_user_exists, Snackbar.LENGTH_LONG)
                                        .show();

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }

                        }

                    }
                });
    }


}
