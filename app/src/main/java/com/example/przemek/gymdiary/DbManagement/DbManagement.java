package com.example.przemek.gymdiary.DbManagement;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.RequiresPermission;
import android.text.Editable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackDocument;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackDocuments;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackWithId;
import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.Models.ExerciseHistory;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.Set;
import com.example.przemek.gymdiary.Models.TrainingPlan;
import com.example.przemek.gymdiary.Models.TrainingPlanHistory;
import com.example.przemek.gymdiary.Models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DbManagement {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String collection;
    FirebaseStorage storage;

    final int USER_DEFAULT_PHOTO = 1;
    protected final int EXERCISE_DEFAULT_PHOTO = 2;


    DbManagement() {
        this.db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    DbManagement(String collection) {
        this.db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        this.collection = collection;
        this.storage = FirebaseStorage.getInstance();
    }

    protected void getAllDocumentsFromCollection(FirestoreCompleteCallbackDocuments response) {

        ArrayList<DocumentSnapshot> listOfDocuments = new ArrayList<>();

        db.collection(collection).get().addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                QuerySnapshot snapshots = c.getResult();
                listOfDocuments.addAll(snapshots.getDocuments());
            }
            response.onCallback(listOfDocuments);

        });


    }

    protected void getDocumentFromDb(String documentId, FirestoreCompleteCallbackDocument response) {

        db.collection(collection).document(documentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Log.d("task", "data" + snapshot.getData());
                    response.onCallback(snapshot);
                } else {
                    Log.d("task", " data nie istnieje");
                    response.onCallback(null);
                }

            } else {
                Log.d("getObjectFromDb", task.getException().getMessage());
            }
        });
    }

    protected void getDocumentsFromDbWithSpecifiedField(String fieldName, Object fieldValue, FirestoreCompleteCallbackDocuments response) {

        ArrayList<DocumentSnapshot> data = new ArrayList<>();

        db.collection(collection).whereEqualTo(fieldName, fieldValue).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                QuerySnapshot snapshots = task.getResult();

                if (snapshots.isEmpty())
                    response.onCallback(data);
                else {
                    data.addAll(snapshots.getDocuments());
                    response.onCallback(data);
                }
            }

        });
    }

    protected void getDocumentIdWithSpecifiedField(String fieldName, Object fieldValue, FirestoreCompleteCallbackData<String> response) {
        db.collection(collection).whereEqualTo(fieldName, fieldValue).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();

                if (snapshot.isEmpty()) {
                    response.onCallback(null);
                } else {
                    DocumentSnapshot data = snapshot.getDocuments().get(0);
                    if (data.exists()) {
                        response.onCallback(data.getId());
                    } else {
                        response.onCallback(null);
                    }
                }
            }
        });

    }


    //Bezpośrednie usuwanie dokumentu
    protected void removeDocumentFromDb(String documentId, FirestoreCompleteCallbackStatus status) {

        db.collection(collection).document(documentId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                status.onCallback(DbStatus.Success);
            } else {
                status.onCallback(DbStatus.Failed);
            }
        });

    }

    protected DocumentReference getDocumentReference(String documentId) {
        return db.collection(collection).document(documentId);
    }


    protected <TModel> void addDocumentToDb(TModel data, FirestoreCompleteCallbackStatus callbackStatus) {

        db.collection(collection).add(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callbackStatus.onCallback(DbStatus.Success);
            } else {
                callbackStatus.onCallback(DbStatus.Failed);
            }
        });
    }

    protected <TModel> void addDocumentToDb(TModel data, FirestoreCompleteCallbackWithId callbackStatus) {

        db.collection(collection).add(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callbackStatus.onCallback(DbStatus.Success, task.getResult().getId());
            } else {
                callbackStatus.onCallback(DbStatus.Failed, null);
            }
        });
    }

    abstract <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus);

    void updatePhoto(String name, Uri uri, String folderName, int defaultPhoto, FirestoreCompleteCallbackData<Uri> completeCallbackData) {

        String exerciseDefaultPhoto = "https://firebasestorage.googleapis.com/v0/b/gymdiary-3ba5e.appspot.com/o/definedExercisesPhotos%2Fplacek.jpg?alt=media&token=bce00831-da5d-4199-a838-006042376f69";
        String userDefaultPhoto = "https://firebasestorage.googleapis.com/v0/b/gymdiary-3ba5e.appspot.com/o/profilePhoto%2Fdefault_username.jpeg?alt=media&token=db36e5ac-f6c9-4291-9b59-7cf3b113acc5";

        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference photo_reference = mStorage.child(folderName).child(name + ".jpg");
        //link do defaultowego zdjęcia na Firebase Storage

        Uri default_photo_uri = (defaultPhoto == USER_DEFAULT_PHOTO) ? Uri.parse(userDefaultPhoto) : Uri.parse(exerciseDefaultPhoto);

        if ((uri == null) && (default_photo_uri != null)) {
            completeCallbackData.onCallback(default_photo_uri);
        } else {
            photo_reference.putFile(uri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Putting photo  : ", "Success");
                    task.getResult().getMetadata().getReference().getDownloadUrl().addOnCompleteListener(c -> {
                        Uri photoUri = c.getResult();
                        completeCallbackData.onCallback(photoUri);
                    });
                } else {
                    Log.d("Putting photo error : ", task.getException().getMessage());
                    completeCallbackData.onCallback(null);
                }
            });
        }
    }

    public void updatePhotoWithDefault(Uri uri, String name, FirestoreCompleteCallbackData<Uri> callback) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference photo_reference = mStorage.child("usersExercisePhotos").child(name + ".jpg");
        photo_reference.putFile(uri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Putting photo  : ", "Success");
                task.getResult().getMetadata().getReference().getDownloadUrl().addOnCompleteListener(c -> {
                    Uri photoUri = c.getResult();
                    callback.onCallback(photoUri);
                });
            } else {
                Log.d("Putting photo error : ", task.getException().getMessage());
                callback.onCallback(null);
            }
        });
    }

    public String getCurrentUserId() {

        if (mAuth.getCurrentUser() != null)
            return mAuth.getCurrentUser().getUid();
        else return "";
    }

    public void updateData(Spinner plec, String photoUri, FirestoreCompleteCallbackStatus status, EditText... data) {
        WriteBatch batch = db.batch();
        DocumentReference sfRef = db.collection("Users").document(getCurrentUserId());
        switch (plec.getSelectedItem().toString()) {
            case "Mężczyzna":
                batch.update(sfRef, "gender", 0);
                break;
            case "Kobieta":
                batch.update(sfRef, "gender", 1);
                break;
            case "Inna":
                batch.update(sfRef, "gender", 2);
                break;
        }

        if (photoUri != null && !TextUtils.isEmpty(photoUri))
            batch.update(sfRef, "profilePhotoUrl", photoUri);

        batch.update(sfRef, "name", data[0].getText().toString());
        batch.update(sfRef, "surname", data[1].getText().toString());
        batch.update(sfRef, "nick", data[2].getText().toString());
        batch.update(sfRef, "city", data[3].getText().toString());
        batch.update(sfRef, "country", data[4].getText().toString());
        batch.update(sfRef, "birthday", data[5].getText().toString());
        batch.commit().addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                status.onCallback(DbStatus.Success);
            } else
                status.onCallback(DbStatus.Failed);
        });
    }

}
