package com.example.przemek.gymdiary.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Fragments.DatePickerFragment;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private String userId;
    private final String TAG = "EditProfileActivity";
    private static final int RESULT_LOAD_IMAGE = 100;
    Toolbar toolbar;

    public final int OK_RESULT = 100;
    public final int NOTHING_CHANGED = 101;
    public final int FAULT = 102;

    private boolean photoIsChanged = false;

    private Uri photo = null;

    @BindView(R.id.nick)
    EditText tv_nick;
    @BindView(R.id.imie)
    EditText tv_name;
    @BindView(R.id.nazwisko)
    EditText tv_nazwisko;
    @BindView(R.id.miasto)
    EditText tv_city;
    @BindView(R.id.country)
    EditText tv_country;
    @BindView((R.id.plec))
    Spinner tv_plec;
    @BindView((R.id.data))
    EditText tv_data;
    @BindView(R.id.editprofile_circleimageview)
    CircleImageView tv_image;

    private HelpfulUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        ButterKnife.bind(this);

        user = (HelpfulUser) getIntent().getSerializableExtra("user");
        if (user != null)
            updateUi(user);
        else {
            Toast.makeText(this, "Coś poszło nie tak....", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.check_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_check) {
            updateUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUi(HelpfulUser user) {

        int gender = user.getGender();
        tv_nick.setText(user.getNick());
        tv_name.setText(user.getName());
        tv_nazwisko.setText(user.getSurname());
        tv_city.setText(user.getCity());
        tv_country.setText(user.getCountry());

        RequestOptions placeholder = new RequestOptions();

        Glide.with(EditProfileActivity.this).setDefaultRequestOptions(placeholder).load(user.getProfilePhotoUrl()).into(tv_image);

        List<String> categories = new ArrayList<String>();
        categories.add("Mężczyzna");
        categories.add("Kobieta");
        categories.add("Inna");
        tv_image.setOnClickListener(onClickListener);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tv_plec.setAdapter(dataAdapter);

        switch (gender) {
            case 0:
                tv_plec.setSelection(0);
                break;
            case 1:
                tv_plec.setSelection(1);
                break;
            case 2:
                tv_plec.setSelection(2);
        }

        tv_data.setText(user.getBirthday());
        tv_data.setOnClickListener(c -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);

        date.show(getSupportFragmentManager(), "DatePicker");
    }

    DatePickerDialog.OnDateSetListener ondate = (view, year, monthOfYear, dayOfMonth) -> {
        String zeroOrNot = monthOfYear >= 9 ? "." : ".0";

        String date = String.valueOf(String.valueOf(dayOfMonth) + zeroOrNot + String.valueOf(monthOfYear + 1)
                + "." + String.valueOf(year));

        tv_data.setText(date);
    };

    private void updateUser() {
        UserManagement userManagement = new UserManagement();
        String message = "Proszę czekać...";
        MaterialDialog progressDialog = new MaterialDialog.Builder(EditProfileActivity.this)
                .title("Edycja konta")
                .contentColor(getColor(R.color.white))
                .backgroundColor(getColor(R.color.colorPrimaryDark))
                .titleColor(getColor(R.color.white))
                .content(message)
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .show();

        if (Helper.getAge(tv_data.getText().toString()) > 13) {

            if (photoIsChanged) {
                userManagement.addUserProfilePhoto(userId, photo, uri -> {
                    userManagement.updateData(tv_plec, uri.toString(), status -> {
                        if (status == DbStatus.Success) {
                            Toast.makeText(this, "Konto zostało zaktualizowane", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            progressDialog.dismiss();
                            finish();
                        } else
                            Toast.makeText(this, "Coś poszło nie tak...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }, tv_name, tv_nazwisko, tv_nick, tv_city, tv_country, tv_data);
                });
            } else
                userManagement.updateData(tv_plec, null, status -> {
                    setResult(RESULT_OK);
                    progressDialog.dismiss();
                    finish();
                }, tv_name, tv_nazwisko, tv_nick, tv_city, tv_country, tv_data);
        } else {
            tv_data.setError("Jesteś za młody!");
            progressDialog.dismiss();

        }

    }

    private View.OnClickListener onClickListener = view -> {

        switch (view.getId()) {
//                case (R.id.zapisz):
//                    break;
            case (R.id.editprofile_circleimageview):
                chooseNewImage();
        }

    };

    private void chooseNewImage() {
        if (Helper.check_storage_permissions(this)) {
            Toast.makeText(this, R.string.permisson_granted, Toast.LENGTH_LONG).show();
            Helper.bringImagePicker(this);

        } else {
            Toast.makeText(this, R.string.permisson_denied, Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photoIsChanged = true;
                photo = result.getUri();
                tv_image.setImageURI(photo);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                photoIsChanged = false;
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
