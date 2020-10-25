package com.example.przemek.gymdiary.DbManagement;

import android.content.Context;

import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Models.Exercise;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class DefinedExercisesManagement extends DbManagement {

    private String collection = FirestoreCollections.DefinedExercises.getName();


    public DefinedExercisesManagement() {
        super(FirestoreCollections.DefinedExercises.getName());
    }

    @Override
    <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus) {
    }

    public FirestoreRecyclerOptions getDefinedExercises(int musculeGroup) {

        Query query = db.collection(collection).whereEqualTo("musculeGroup", musculeGroup);

        return new FirestoreRecyclerOptions.Builder<Exercise>()
                .setQuery(query, Exercise.class)
                .build();
    }

}
