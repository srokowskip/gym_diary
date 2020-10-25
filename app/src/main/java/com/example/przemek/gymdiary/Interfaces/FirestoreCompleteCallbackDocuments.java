package com.example.przemek.gymdiary.Interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public interface FirestoreCompleteCallbackDocuments {

    void onCallback(ArrayList<DocumentSnapshot> docs);


}
