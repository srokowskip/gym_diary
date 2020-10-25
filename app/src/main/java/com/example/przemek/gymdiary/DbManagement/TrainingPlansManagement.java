package com.example.przemek.gymdiary.DbManagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Models.ExerciseHistory;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulTrainingPlan;
import com.example.przemek.gymdiary.Models.Set;
import com.example.przemek.gymdiary.Models.TrainingPlan;
import com.example.przemek.gymdiary.Models.TrainingPlanHistory;
import com.example.przemek.gymdiary.Models.TrainingPlanSession;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingPlansManagement extends DbManagement {

    private String collection = FirestoreCollections.TrainingPlans.getName();

    public TrainingPlansManagement() {
        super(FirestoreCollections.TrainingPlans.getName());
    }

    @Override
    public <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus) {


        Map<String, Object> updateDataMap = new HashMap<>();
        DocumentReference plansRef = db.collection(this.collection).document(documentId);

        getDocumentFromDb(documentId, data -> {
            if (data == null) {
                Toast.makeText(context, "Nie znaleziono takiego planu", Toast.LENGTH_SHORT).show();
                return;
            }
            TrainingPlan existingTrainingPlan = data.toObject(TrainingPlan.class);
            TrainingPlan updatedTrainingPlan = (TrainingPlan) updateData;

            String trainingPlanTitle = updatedTrainingPlan.getTitle() != null || !TextUtils.isEmpty(updatedTrainingPlan.getTitle())
                    ? updatedTrainingPlan.getTitle()
                    : existingTrainingPlan.getTitle();

            String trainingPlanDescription = updatedTrainingPlan.getDescription() != null || !TextUtils.isEmpty(updatedTrainingPlan.getDescription())
                    ? updatedTrainingPlan.getDescription()
                    : existingTrainingPlan.getDescription();

            List<HelpfulExercise> updateExercises = updatedTrainingPlan.getExerciseList();

            List<Map<String, Object>> updateExercisesMapList = new ArrayList<>();

            for (HelpfulExercise e : updateExercises
                    ) {
                Map<String, Object> exerciseMap = new HashMap<>();
                exerciseMap.put("name", e.getName());
                exerciseMap.put("musculeGroup", e.getMusculeGroup());
                exerciseMap.put("description", e.getDescription());
                exerciseMap.put("userId", e.getUserId());
                exerciseMap.put("id", e.getId());
                exerciseMap.put("listOfRepeats", e.getListOfRepeats());

                updateExercisesMapList.add(exerciseMap);

            }

            updateDataMap.put("title", trainingPlanTitle);
            updateDataMap.put("description", trainingPlanDescription);
            updateDataMap.put("exerciseList", updateExercisesMapList);

            plansRef.update(updateDataMap)
                    .addOnCompleteListener(c -> callbackStatus.onCallback(DbStatus.Success))
                    .addOnFailureListener(c -> callbackStatus.onCallback(DbStatus.Failed));

        });
    }


    public FirestoreRecyclerOptions getUsersTrainingPlans(String userId) {

        Query query = db.collection(this.collection).whereEqualTo("userId", userId);

        return new FirestoreRecyclerOptions.Builder<TrainingPlan>()
                .setQuery(query, TrainingPlan.class)
                .build();
    }

    public void updateLastUsedProperty(Date date, String documentId, FirestoreCompleteCallbackStatus status) {

        DocumentReference plansRef = db.collection(this.collection).document(documentId);

        plansRef.update("lastUsed", date).addOnSuccessListener(s -> {
            status.onCallback(DbStatus.Success);
        }).addOnFailureListener(f -> {
            status.onCallback(DbStatus.Failed);
        });


    }

    public void getTrainingPlan(String planId, FirestoreCompleteCallbackData<HelpfulTrainingPlan> data) {

        getDocumentFromDb(planId, doc -> {
            String id = doc.getId();
            TrainingPlan tp = doc.toObject(TrainingPlan.class);
            HelpfulTrainingPlan trainingPlan = new HelpfulTrainingPlan(tp, id);
            data.onCallback(trainingPlan);
        });
    }

    public void addTrainingPlan(TrainingPlan trainingPlan, FirestoreCompleteCallbackStatus status) {
        addDocumentToDb(trainingPlan, status);
    }


    @Override
    public void removeDocumentFromDb(String documentId, FirestoreCompleteCallbackStatus status) {

        //Before perform remove - check available sessions and remove them

        LiveTrainingManagement liveTrainingManagement = new LiveTrainingManagement();
        liveTrainingManagement.removeDocumentFromDb(documentId, resp -> {
            if (resp == DbStatus.Success) {
                super.removeDocumentFromDb(documentId, status);
            } else {
                status.onCallback(DbStatus.Failed);
            }
        });
    }


}
