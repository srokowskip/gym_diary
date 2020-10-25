package com.example.przemek.gymdiary.Activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Button;
import android.widget.Toast;

import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends AppCompatActivity {
    @BindView(R.id.recover_password_btn)
    Button recoverPasswordButton;
    @BindView(R.id.recover_password_et)
    TextInputEditText recoverPasswordEmail;

    private UserManagement userManagement = new UserManagement();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

        recoverPasswordButton.setOnClickListener(c -> {
            recoverPassword();
        });

    }

    private void recoverPassword() {
        String email = recoverPasswordEmail.getText().toString();
        if (!TextUtils.isEmpty(email)) {
            userManagement.recoverPassword(email, status -> {
                if (status == DbStatus.Success) {
                    Toast.makeText(this, "Na podany adres został wysłany email z instrukcjami", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Na podany adres został wysłany email z instrukcjami", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}
