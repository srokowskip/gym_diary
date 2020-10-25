package com.example.przemek.gymdiary.DbManagement;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Interfaces.ObserveCollectionInterface;
import com.example.przemek.gymdiary.Models.Friend;
import com.example.przemek.gymdiary.Models.FriendshipRequest;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulFriendshipRequest;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.Models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FriendshipManagement extends DbManagement {

    private String collection = FirestoreCollections.FriendshipRequest.getName();

    private ListenerRegistration listenerFriendsRequest;
    private ListenerRegistration listenerSentResuest;

    public FriendshipManagement() {
        super(FirestoreCollections.FriendshipRequest.getName());
    }

    @Override
    <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus) {
    }

    public void addFriendshipRequest(String askedId, String askedNick, HelpfulUser initiator, FirestoreCompleteCallbackStatus status) {

        FriendshipRequest f = new FriendshipRequest(initiator.getId(), initiator.getNick(), askedId, askedNick);
        addDocumentToDb(f, status::onCallback);
    }

    public void handleAcceptFriendshipRequestAcceptation(HelpfulFriendshipRequest request, FirestoreCompleteCallbackStatus status) {

        Friend friend = new Friend(request.getInitiatorNick());
        Friend friend2 = new Friend(request.getAskedNick());
        WriteBatch batch = db.batch();

        DocumentReference requestRemovationRef = db.collection(collection).document(request.getId());
        batch.delete(requestRemovationRef);

        DocumentReference ref = db.collection("Users").document(getCurrentUserId()).collection("Friends").document(request.getInitiatorId());
        DocumentReference ref2 = db.collection("Users").document(request.getInitiatorId()).collection("Friends").document(request.getAskedId());
        batch.set(ref, friend);
        batch.set(ref2, friend2);

        batch.commit().addOnCompleteListener(c -> {

            if (c.isSuccessful()) {
                status.onCallback(DbStatus.Success);
            } else {
                status.onCallback(DbStatus.Failed);
            }

        });
    }

    public void decelineFriendshipRequest(HelpfulFriendshipRequest request, FirestoreCompleteCallbackStatus status) {
        removeDocumentFromDb(request.getId(), status::onCallback);
    }


    public void observeFriendsRequests(ObserveCollectionInterface<HelpfulFriendshipRequest> response, Activity context) {

        listenerFriendsRequest = db.collection(collection).whereEqualTo("askedId", getCurrentUserId()).addSnapshotListener(context, (queryDocumentSnapshots, e) -> {

            if (e != null) {
                Log.w("asd", "Listen failed.", e);
                return;
            }

            HelpfulFriendshipRequest request = new HelpfulFriendshipRequest();

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                switch (dc.getType()) {

                    case ADDED:
                        //TODO dziaffa
                        Map<String, Object> map1 = dc.getDocument().getData();
                        request.setId(dc.getDocument().getId());
                        request.setAskedId((String) map1.get("askedId"));
                        request.setAskedNick((String) map1.get("askedNick"));
                        request.setInitiatorId((String) map1.get("initiatorId"));
                        request.setInitiatorNick((String) map1.get("initiatorNick"));
                        request.setStartDate((Date) map1.get("startDate"));
                        response.onAdd(request);
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        Map<String, Object> map = dc.getDocument().getData();
                        request.setId(dc.getDocument().getId());
                        request.setAskedId((String) map.get("askedId"));
                        request.setAskedNick((String) map.get("askedNick"));
                        request.setInitiatorId((String) map.get("initiatorId"));
                        request.setInitiatorNick((String) map.get("initiatorNick"));
                        request.setStartDate((Date) map.get("startDate"));
                        response.onDelete(request);
                        break;
                }
            }

        });
    }

    public void observeSentFriendshipRequests(ObserveCollectionInterface<HelpfulFriendshipRequest> response, Activity context) {

        listenerSentResuest = db.collection(collection).whereEqualTo("initiatorId", getCurrentUserId()).addSnapshotListener(context, (queryDocumentSnapshots, e) -> {

            if (e != null) {
                Log.w("asd", "Listen failed.", e);
                return;
            }

            HelpfulFriendshipRequest request = new HelpfulFriendshipRequest();

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                switch (dc.getType()) {

                    case ADDED:
                        //TODO dziaffa
                        Map<String, Object> map1 = dc.getDocument().getData();
                        request.setId(dc.getDocument().getId());
                        request.setAskedId((String) map1.get("askedId"));
                        request.setAskedNick((String) map1.get("askedNick"));
                        request.setInitiatorId((String) map1.get("initiatorId"));
                        request.setInitiatorNick((String) map1.get("initiatorNick"));
                        request.setStartDate((Date) map1.get("startDate"));
                        response.onAdd(request);
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        Map<String, Object> map = dc.getDocument().getData();
                        request.setId(dc.getDocument().getId());
                        request.setAskedId((String) map.get("askedId"));
                        request.setAskedNick((String) map.get("askedNick"));
                        request.setInitiatorId((String) map.get("initiatorId"));
                        request.setInitiatorNick((String) map.get("initiatorNick"));
                        request.setStartDate((Date) map.get("startDate"));
                        response.onDelete(request);
                        break;
                }
            }

        });
    }


    public void getCurrentUserSentRequests(FirestoreCompleteCallbackData<ArrayList<HelpfulFriendshipRequest>> response) {

        ArrayList<HelpfulFriendshipRequest> listOfInitiators = new ArrayList<>();

        getDocumentsFromDbWithSpecifiedField("initiatorId", getCurrentUserId(), docs -> {

            if (docs.isEmpty())
                response.onCallback(new ArrayList<>());
            else {
                for (DocumentSnapshot doc : docs
                        ) {

                    FriendshipRequest fr = doc.toObject(FriendshipRequest.class);

                    HelpfulFriendshipRequest requestWithId = new HelpfulFriendshipRequest(doc.getId(), fr);

                    listOfInitiators.add(requestWithId);
                }
                response.onCallback(listOfInitiators);

            }
        });

    }

    public void getFriendshipRequests(FirestoreCompleteCallbackData<ArrayList<HelpfulFriendshipRequest>> response) {

        ArrayList<HelpfulFriendshipRequest> listOfInitiators = new ArrayList<>();

        getDocumentsFromDbWithSpecifiedField("askedId", getCurrentUserId(), docs -> {

            if (docs.isEmpty())
                response.onCallback(new ArrayList<>());
            else {
                for (DocumentSnapshot doc : docs
                        ) {

                    FriendshipRequest fr = doc.toObject(FriendshipRequest.class);

                    HelpfulFriendshipRequest requestWithId = new HelpfulFriendshipRequest(doc.getId(), fr);

                    listOfInitiators.add(requestWithId);
                }
                response.onCallback(listOfInitiators);

            }
        });


    }

    public ListenerRegistration getListenerFriendsRequest() {
        return listenerFriendsRequest;
    }

    public ListenerRegistration getListenerSentResuest() {
        return listenerSentResuest;
    }
}
