package com.example.przemek.gymdiary.bin;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public abstract class DbManagementold {

    FirebaseFirestore db;
    protected FirebaseAuth mAuth;

    DbManagementold() {
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    abstract public void removeDocumentById(String documentId, FirestoreCompleteCallbackStatus callbackStatus);
    abstract public <T extends Object> void addDocumentToDb (T data, FirestoreCompleteCallbackStatus callbackStatus);
    abstract public void getDocumentFromDb(String documentId, FirestoreCompleteCallbackData callbackData);
    abstract public void updateDocumentById(Context context, String documentId, Object model , FirestoreCompleteCallbackStatus callbackStatus);


    public void updatePhoto(String name, Uri uri, FirestoreCompleteCallbackData completeCallbackData) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference photo_reference = mStorage.child("profilePhoto").child(name + ".jpg");
        final String default_photo = "https://firebasestorage.googleapis.com/v0/b/gymdiary-3ba5e.appspot.com/o/profilePhoto%2Fdefault_username.jpeg?alt=media&token=db36e5ac-f6c9-4291-9b59-7cf3b113acc5"; //link do defaultowego zdjęcia na Firebase Storage
        Uri default_photo_uri = Uri.parse(default_photo);
        if (uri == null) {
            Uri photoUri = default_photo_uri;
            completeCallbackData.onCallback(photoUri);
        }
        else
            {
                photo_reference.putFile(uri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Putting photo  : ", "Success");

                        Uri photoUri = task.getResult().getUploadSessionUri();
                        completeCallbackData.onCallback(photoUri);
                    } else {
                        Log.d("Putting photo error : ", task.getException().getMessage());
                        completeCallbackData.onCallback(null);
                    }

                });
            }

    }



}
