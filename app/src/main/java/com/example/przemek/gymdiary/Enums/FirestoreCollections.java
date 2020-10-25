package com.example.przemek.gymdiary.Enums;

public enum FirestoreCollections {

    Exercises("Exercises"),
    DefinedExercises("DefinedExercises"),
    Users("Users"),
    TrainingPlans("TrainingPlans"),
    LiveTrainings("LiveTrainings"),
    FriendshipRequest("FriendshipRequests"),
    TrainingPlansHistory("TrainingPlansHistory"),
    Posts("Posts"),
    Chats("Chats");

    private String collection;

    FirestoreCollections(String collection) {
        this.collection = collection;
    }

    public String getName() {
        return this.collection;
    }

}
