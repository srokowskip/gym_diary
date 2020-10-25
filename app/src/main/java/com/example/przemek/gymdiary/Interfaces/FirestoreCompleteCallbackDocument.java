package com.example.przemek.gymdiary.Interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public interface FirestoreCompleteCallbackDocument {
    void onCallback(DocumentSnapshot doc);

}
