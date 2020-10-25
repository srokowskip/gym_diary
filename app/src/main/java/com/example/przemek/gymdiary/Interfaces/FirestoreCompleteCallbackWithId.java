package com.example.przemek.gymdiary.Interfaces;

import com.example.przemek.gymdiary.Enums.DbStatus;

public interface FirestoreCompleteCallbackWithId {

    void onCallback(DbStatus status, String documentId);

}
