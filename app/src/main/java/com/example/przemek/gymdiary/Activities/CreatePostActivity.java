package com.example.przemek.gymdiary.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.przemek.gymdiary.DbManagement.PostsManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.Validateable;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.sql.Timestamp;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreatePostActivity extends AppCompatActivity implements Validateable {

    @BindView(R.id.post_activity_tiet_message)
    TextInputEditText postMessage;
    @BindView(R.id.post_activity_til_message)
    TextInputLayout postMessageLayout;
    @BindView(R.id.create_post_toolbar)
    Toolbar toolbar;
    @BindView(R.id.post_activity_image)
    ImageView postImage;
    Uri postImageUri = null;

    HelpfulUser user;

    PostsManagement postsManagement = new PostsManagement();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        user = (HelpfulUser) getIntent().getSerializableExtra("user");
        postImage.setOnClickListener(c -> addPostPhoto());

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
            processPost();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPost(Uri photoUri, MaterialDialog progressDialog) {

        postsManagement.addPost(user, postMessage.getText().toString(), photoUri, status -> {
            if (status == DbStatus.Success) {
                Toast.makeText(this, "Post dodano", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            } else
                Toast.makeText(this, "Wystąpił błąd podczas dodawania postu", Toast.LENGTH_SHORT).show();
        });

    }


    private void processPost() {
        if (isValid()) {
            String message = "Proszę czekać...";
            MaterialDialog progressDialog = new MaterialDialog.Builder(CreatePostActivity.this)
                    .title("Tworzenie postu")
                    .contentColor(getColor(R.color.white))
                    .backgroundColor(getColor(R.color.colorPrimaryDark))
                    .titleColor(getColor(R.color.white))
                    .content(message)
                    .progress(true, 0)
                    .canceledOnTouchOutside(false)
                    .show();
            if (postImageUri != null)
                uploadPhoto(progressDialog);
            else {
                createPost(null, progressDialog);
            }
        } else
            postMessageLayout.setError("Musisz wpisać treść postu!");

    }

    private void uploadPhoto(MaterialDialog progressDialog) {

        postsManagement.addPostPhoto(postImageUri, String.valueOf(com.google.firebase.Timestamp.now().getNanoseconds()) + user.getId(), uri -> {
            if (uri != null) {
                createPost(uri, progressDialog);
            }
        });

    }


    private void addPostPhoto() {

        if (Helper.check_storage_permissions(this)) {
            Toast.makeText(this, R.string.permisson_granted, Toast.LENGTH_LONG).show();
            ImagePicker.create(this).returnMode(ReturnMode.CAMERA_ONLY).folderMode(true).toolbarImageTitle("Wybierz zdjęcie").single().showCamera(true).start();

        } else {
            Toast.makeText(this, R.string.permisson_denied, Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            postImageUri = Uri.fromFile(new File(image.getPath()));
            Glide.with(this).load(postImageUri).into(postImage);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(postMessage.getText().toString());
    }
}
