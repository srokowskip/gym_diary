package com.example.przemek.gymdiary.DbManagement;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackWithId;
import com.example.przemek.gymdiary.Models.Exercise;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.Post;
import com.firebase.ui.firestore.paging.FirestoreDataSource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExercisesManagement extends DbManagement {

    private String collection = FirestoreCollections.Exercises.getName();

    public ExercisesManagement() {
        super(FirestoreCollections.Exercises.getName());
    }

    @Override
    public <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus) {

        Map<String, Object> updateDataMap = new HashMap<>();
        DocumentReference exerciseRef = db.collection(collection).document(documentId);

        getDocumentFromDb(documentId, data -> {

            if (data == null) {
                Toast.makeText(context, "Nie znaleziono ćwiczenia", Toast.LENGTH_SHORT).show();
                return;
            }
            Exercise existingExercise = data.toObject(Exercise.class);
            Exercise updatedExercise = (Exercise) updateData;

            String exerciseName = existingExercise.getName() != null || !TextUtils.isEmpty(existingExercise.getName())
                    ? updatedExercise.getName()
                    : existingExercise.getName();

            String exerciseDescription = existingExercise.getDescription() != null || !TextUtils.isEmpty(existingExercise.getDescription())
                    ? updatedExercise.getDescription()
                    : existingExercise.getDescription();

            int exerciseMusculeGroup = updatedExercise.getMusculeGroup();

            updateDataMap.put("name", exerciseName);
            updateDataMap.put("description", exerciseDescription);
            updateDataMap.put("musculeGroup", exerciseMusculeGroup);

            exerciseRef.update(updateDataMap)
                    .addOnCompleteListener(c -> callbackStatus.onCallback(DbStatus.Success))
                    .addOnFailureListener(c -> callbackStatus.onCallback(DbStatus.Failed));

        });
    }

    public void getExercise(String exerciseId, FirestoreCompleteCallbackData<HelpfulExercise> data) {

        getDocumentFromDb(exerciseId, doc -> {
            String id = doc.getId();
            Exercise e = doc.toObject(Exercise.class);
            HelpfulExercise exercise = new HelpfulExercise(id, e);
            data.onCallback(exercise);
        });

    }

    public void addExercise(Exercise exercise, FirestoreCompleteCallbackWithId status) {

        addDocumentToDb(exercise, status);

    }

    public void getUsersExercises(String userId, FirestoreCompleteCallbackData<List<HelpfulExercise>> response) {

        List<HelpfulExercise> data = new ArrayList<>();

        getDocumentsFromDbWithSpecifiedField("userId", userId, callback -> {
            if (callback != null) {
                for (DocumentSnapshot doc : callback
                        ) {
                    Exercise e = doc.toObject(Exercise.class);
                    String exerciseId = doc.getId();
                    HelpfulExercise helpfulExercise = new HelpfulExercise(exerciseId, e);
                    data.add(helpfulExercise);
                }
            }
            response.onCallback(data);
        });

    }

    public void updateExercisePhotoUri(String exerciseId, Uri uri, FirestoreCompleteCallbackStatus status) {
        DocumentReference exerciseRef = db.collection(FirestoreCollections.Exercises.getName()).document(exerciseId);
        exerciseRef.update("photoUri", uri.toString()).addOnSuccessListener(s -> status.onCallback(DbStatus.Success)).addOnFailureListener(f -> status.onCallback(DbStatus.Failed));
    }

    public void getExercisePhotoURI(String exerciseId, FirestoreCompleteCallbackData<Uri> uri) {

        StorageReference storageRef = storage.getReferenceFromUrl("gs://gymdiary-3ba5e.appspot.com");

        Task<Uri> pathReference = storageRef.child("definedExercisesPhotos/" + "L4MyhK8UpixPToDyXQKi").getDownloadUrl();
        pathReference.addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                Uri photoUri = c.getResult();
                uri.onCallback(photoUri);
            }
        });
    }

    public void addExercisePhoto(Uri uri, String name, FirestoreCompleteCallbackData<Uri> callback) {
        updatePhoto(name, uri, "userExercisesPhotos/" + getCurrentUserId() + "/", EXERCISE_DEFAULT_PHOTO, callback);
    }

    public void removeExercise(String exerciseId, FirestoreCompleteCallbackStatus status) {
        removeDocumentFromDb(exerciseId, status);
    }
}
