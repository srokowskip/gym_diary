package com.example.przemek.gymdiary.DbManagement;

import android.content.Context;

import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Models.ExerciseHistory;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulTrainingPlanHistory;
import com.example.przemek.gymdiary.Models.Set;
import com.example.przemek.gymdiary.Models.TrainingPlanHistory;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Map;

public class TrainingPlansHistoryManagement extends DbManagement {
    private String collection = FirestoreCollections.TrainingPlansHistory.getName();

    public TrainingPlansHistoryManagement() {
        super(FirestoreCollections.TrainingPlansHistory.getName());
    }

    @Override
    <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus) {

    }

    //TODO OVERIDE ??
    public void addTrainingPlanHistoryAndRemoveSession(String planId, ArrayList<Map<String, Object>> exercisesData, TrainingPlanHistory trainingPlanHistory, FirestoreCompleteCallbackStatus status) {

        CollectionReference ref = db.collection(this.collection);

        WriteBatch batch = db.batch();

        ref.add(trainingPlanHistory).addOnCompleteListener(c -> {

            if (c.isSuccessful()) {
                for (Map<String, Object> map : exercisesData
                        ) {
                    HelpfulExercise exercise = (HelpfulExercise) map.get("exercise");
                    ArrayList<Set> listOfSets = (ArrayList<Set>) map.get("listOfSets");

                    ExerciseHistory data = new ExerciseHistory(listOfSets, exercise.getName());

                    String historyId = c.getResult().getId();
                    DocumentReference docRef = ref.document(historyId).collection("Exercises").document(exercise.getId());
                    batch.set(docRef, data);
                }
            }
            batch.commit().addOnCompleteListener(a -> {
                if (a.isSuccessful()) {
                    LiveTrainingManagement liveTrainingManagement = new LiveTrainingManagement();
                    liveTrainingManagement.removeSessionByPlanId(planId, resp -> {
                        status.onCallback(DbStatus.Success);
                    });
                }
            }).addOnFailureListener(f -> {
                status.onCallback(DbStatus.Failed);
            });
        }).addOnFailureListener(f -> {
            status.onCallback(DbStatus.Failed);
        });

    }


    public FirestoreRecyclerOptions getPlanHistoryListByPlanId(String planId) {
        Query query = db.collection(this.collection).whereEqualTo("planId", planId);

        return new FirestoreRecyclerOptions.Builder<TrainingPlanHistory>()
                .setQuery(query, TrainingPlanHistory.class)
                .build();

    }


    public void getPlanHistoryDataById(String historyId, FirestoreCompleteCallbackData<ArrayList<ExerciseHistory>> data) {

        CollectionReference docRef = db.collection(collection).document(historyId).collection("Exercises");
        docRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<ExerciseHistory> listOfHistory = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            listOfHistory.add(document.toObject(ExerciseHistory.class));
                        }
                        data.onCallback(listOfHistory);

                    } else {
                        data.onCallback(null);
                    }
                });
    }

    public void getUserHistory(String userId, FirestoreCompleteCallbackData<ArrayList<HelpfulTrainingPlanHistory>> history) {
        ArrayList<HelpfulTrainingPlanHistory> data = new ArrayList<>();
        getDocumentsFromDbWithSpecifiedField("userId", userId, documents -> {
            for (DocumentSnapshot doc : documents
                    ) {
                TrainingPlanHistory h = doc.toObject(TrainingPlanHistory.class);
                HelpfulTrainingPlanHistory trainingHistory = new HelpfulTrainingPlanHistory(doc.getId(), h);
                data.add(trainingHistory);
            }
            history.onCallback(data);
        });

    }


}
