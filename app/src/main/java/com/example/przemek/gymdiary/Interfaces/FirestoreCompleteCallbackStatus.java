package com.example.przemek.gymdiary.Interfaces;

import com.example.przemek.gymdiary.Enums.DbStatus;

public interface FirestoreCompleteCallbackStatus {
    void onCallback(DbStatus status);
}
