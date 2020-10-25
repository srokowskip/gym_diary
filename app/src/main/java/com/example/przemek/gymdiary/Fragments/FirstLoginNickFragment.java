package com.example.przemek.gymdiary.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.User;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.PhotoViewModel;
import com.example.przemek.gymdiary.ViewModels.UserViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.theartofdev.edmodo.cropper.CropImage;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FirstLoginNickFragment extends Fragment implements BlockingStep {

    @BindView(R.id.firstlogin_circleimageview)
    CircleImageView img_profile;
    @BindView(R.id.firstlogin_til_nick)
    TextInputLayout til_nick;
    @BindView(R.id.firstlogin_tiet_nick)
    TextInputEditText tiet_nick;
    User userModel;
    CollectionReference userRef;
    PhotoViewModel photoViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_first_login_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        userRef = mDatabase.collection("users");
        userModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class).getUser();
        photoViewModel = ViewModelProviders.of(getActivity()).get(PhotoViewModel.class);
        img_profile.setOnClickListener(v -> {

            if (Helper.check_storage_permissions(getActivity())) {
                Toast.makeText(getActivity(), R.string.permisson_granted, Toast.LENGTH_LONG).show();
                Helper.bringImagePicker(getContext(), this);

            } else {
                Toast.makeText(getActivity(), R.string.permisson_denied, Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {


        new Handler().postDelayed(() -> {
            if (verifyStep() == null) {
                userModel.setNick(tiet_nick.getText().toString());
                callback.complete();
            }
        }, 0L);// delay open another fragment,


    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {

        if (TextUtils.isEmpty(tiet_nick.getText().toString())) {
            return new VerificationError("emptyNick");
        } else {

        }
        til_nick.setError("");
        return null;
    }


    @Override
    public void onSelected() {
        if (photoViewModel != null) {
            img_profile.setImageURI(photoViewModel.getLocalPhotoUri());
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {

        switch (error.getErrorMessage()) {
            case "emptyNick": {
                til_nick.setError(getString(R.string.put_your_nick));
                break;
            }
            case "usedNick": {
                til_nick.setError(getString(R.string.nick_used));
                break;
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri photo = result.getUri();
                photoViewModel.setLocalPhotoUri(photo);
                img_profile.setImageURI(photo);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}

