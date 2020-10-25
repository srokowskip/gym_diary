package com.example.przemek.gymdiary.DbManagement;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackWithId;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulTrainingPlan;
import com.example.przemek.gymdiary.Models.Set;
import com.example.przemek.gymdiary.Models.TrainingPlanSession;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LiveTrainingManagement extends DbManagement {

    public String collection = FirestoreCollections.LiveTrainings.getName();

    public LiveTrainingManagement() {
        super(FirestoreCollections.LiveTrainings.getName());
    }

    @Override
    <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus) {
    }


    @Override
    public <TModel> void addDocumentToDb(TModel data, FirestoreCompleteCallbackWithId callback) {

        String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection(collection).whereEqualTo("userId", currentUserId).get().addOnCompleteListener(l -> {

            QuerySnapshot snapshot = l.getResult();

            if (snapshot.isEmpty()) {

                HelpfulTrainingPlan helpfulTrainingPlan = (HelpfulTrainingPlan) data;

                TrainingPlanSession planSession = new TrainingPlanSession(helpfulTrainingPlan.getUserId(), helpfulTrainingPlan.getId());

                super.addDocumentToDb(planSession, ((status, documentId) -> {
                    if (status == DbStatus.Success && !documentId.isEmpty())
                        callback.onCallback(DbStatus.Success, documentId);
                    else
                        callback.onCallback(DbStatus.Failed, "");


                }));

            } else {
                DocumentSnapshot session = snapshot.getDocuments().get(0);
                callback.onCallback(DbStatus.UserAlreadyHaveSession, session.getId());
            }
        });
    }
    public void getLiveTraining(String id, FirestoreCompleteCallbackData<TrainingPlanSession> data){

        getDocumentFromDb(id, doc->{
            TrainingPlanSession session = doc.toObject(TrainingPlanSession.class);
            data.onCallback(session);
        });

    }

    public void removeLiveTraining(String id, FirestoreCompleteCallbackStatus status){
        removeDocumentFromDb(id, status);
    }

    public void initSessionWithExercises(String sessionId, ArrayList<String> exerciseNames, FirestoreCompleteCallbackStatus callback) {

        for (String exerciseName : exerciseNames
                ) {
            ArrayMap<String, Set> map = new ArrayMap<>();

            db.collection(collection).document(sessionId).collection("exercises").document(exerciseName).set(map).addOnCompleteListener(c -> {

                        if (c.isSuccessful()) {
                            callback.onCallback(DbStatus.Success);
                        } else
                            callback.onCallback(DbStatus.Failed);
                    }
            );

        }
    }

    public void getResumedExercises(String sessionId, FirestoreCompleteCallbackData<Map<String, Map<String, Object>>> callback) {

        db.collection(collection).document(sessionId).collection("exercises").get().addOnCompleteListener(c -> {

            QuerySnapshot snapshot = c.getResult();
            if (!snapshot.isEmpty()) {
                List<DocumentSnapshot> list = snapshot.getDocuments();

                Map<String, Map<String, Object>> savedData = new ArrayMap<>();
                for (DocumentSnapshot doc : list
                        ) {
                    savedData.put(doc.getId(), doc.getData());
                }
                callback.onCallback(savedData);
            } else {
                callback.onCallback(null);
            }

        });
    }


    public void saveSession(int serieNumber, String sessionId, Set set, String exerciseName, FirestoreCompleteCallbackStatus callback) {

        ArrayMap<String, Object> map = new ArrayMap<>();

        map.put("repeats", set.getRepeats());
        map.put("weight", set.getWeight());

        String val = String.valueOf(serieNumber);


        db.collection(collection).document(sessionId).collection("exercises").document(exerciseName).update(val, map).addOnCompleteListener(c -> {

            if (c.isSuccessful()) {
                callback.onCallback(DbStatus.Success);
            } else {
                callback.onCallback(DbStatus.Failed);
            }

        });

    }

    public void removeSessionByPlanId(String planId, FirestoreCompleteCallbackStatus status) {

        getDocumentIdWithSpecifiedField("planId", planId, response -> {
            if (response != null) {
                String sessionId = (String) response;
                removeDocumentFromDb(sessionId, status);
            } else
                status.onCallback(DbStatus.Success);
        });


    }
}

