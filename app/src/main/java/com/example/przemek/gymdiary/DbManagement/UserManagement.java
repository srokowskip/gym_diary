package com.example.przemek.gymdiary.DbManagement;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Interfaces.ObserveCollectionInterface;
import com.example.przemek.gymdiary.Models.Friend;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class UserManagement extends DbManagement {

    private String collection = FirestoreCollections.Users.getName();

    public UserManagement() {
        super(FirestoreCollections.Users.getName());
    }

    @Override
    <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus) {
        //TODO edycja usera
    }

    public void fillUserData(String userId, User data, FirestoreCompleteCallbackStatus callback) {
        if (!(userId != null && userId.isEmpty())) {
            db.collection(collection).document(userId).set(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("addUserData: ", "Success");
                    callback.onCallback(DbStatus.Success);
                } else {
                    Log.d("addUserData: ", task.getException().getMessage());
                    callback.onCallback(DbStatus.Failed);
                }
            });
        } else
            callback.onCallback(DbStatus.Failed);

    }

    public void checkNickAvailability(String nick, FirestoreCompleteCallbackStatus callbackStatus) {
        Log.d("Chcecking nick availibility: ", "Pending");
        Query nickQuery = db.collection(this.collection).whereEqualTo("nick", nick);
        nickQuery.get().addOnCompleteListener(documents -> {
            if (documents.isSuccessful()) {
                Log.d("Chcecking nick availibility: ", "Success");

                if (documents.getResult().isEmpty()) {
                    callbackStatus.onCallback(DbStatus.Success);

                } else {
                    callbackStatus.onCallback(DbStatus.Failed);
                }
            } else {

                Log.d("Chcecking nick availibility: ", "Success");
                Log.d("Checkng nick availibility error: ", documents.getException().getMessage());

            }
        });
    }

    public void getUser(String userId, FirestoreCompleteCallbackData<HelpfulUser> data) {


        getDocumentFromDb(userId, doc -> {

            if (doc == null)
                data.onCallback(null);
            else {
                String id = doc.getId();
                User u = doc.toObject(User.class);
                HelpfulUser user = new HelpfulUser(id, u);
                data.onCallback(user);
            }
        });
    }

    public void getAllUsers(FirestoreCompleteCallbackData<ArrayList<HelpfulUser>> data) {
        ArrayList<HelpfulUser> listOfUsers = new ArrayList<>();

        getAllDocumentsFromCollection(c -> {
            if (!c.isEmpty()) {
                for (DocumentSnapshot doc : c
                        ) {
                    User userObj = doc.toObject(User.class);
                    HelpfulUser user = new HelpfulUser(doc.getId(), userObj);
                    listOfUsers.add(user);
                }
            }
            data.onCallback(listOfUsers);
        });
    }

    public void getCurrentUserFriends(FirestoreCompleteCallbackData<ArrayList<HelpfulUser>> response) {

        ArrayList<HelpfulUser> data = new ArrayList<>();

        db.collection(collection).document(getCurrentUserId()).collection("Friends").get().addOnCompleteListener(c -> {

            if (c.isSuccessful()) {
                QuerySnapshot docs = c.getResult();
                for (DocumentSnapshot doc : docs.getDocuments()
                        ) {
                    Friend friend = doc.toObject(Friend.class);
                    HelpfulUser user = new HelpfulUser();

                    user.setId(doc.getId());
                    user.setNick(friend.getFriendNick());

                    data.add(user);
                }

            }
            response.onCallback(data);

        });


    }

    public void getUserByNick(String nick, FirestoreCompleteCallbackData<ArrayList<HelpfulUser>> response) {
        ArrayList<HelpfulUser> data = new ArrayList<>();
        getDocumentsFromDbWithSpecifiedField("searchName", nick.toUpperCase(), result -> {

            for (DocumentSnapshot doc : result
                    ) {
                if (!doc.getId().equals(getCurrentUserId())) {
                    HelpfulUser user = doc.toObject(HelpfulUser.class);
                    user.setId(doc.getId());
                    data.add(user);
                }
            }
            response.onCallback(data);

        });

    }

    public void observeFriends(ObserveCollectionInterface<HelpfulUser> response, Activity context) {

        CollectionReference ref = db.collection(collection).document(getCurrentUserId()).collection("Friends");

        ref.addSnapshotListener(context, (queryDocumentSnapshots, e) -> {

            if (e != null) {
                Log.w("asd", "Listen failed.", e);
                return;
            }

            HelpfulUser user = new HelpfulUser();

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                switch (dc.getType()) {

                    case ADDED:
                        //TODO dziaffa
                        Map<String, Object> map1 = dc.getDocument().getData();
                        user.setNick((String) map1.get("friendNick"));
                        user.setId(dc.getDocument().getId());
                        response.onAdd(user);
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        Map<String, Object> map = dc.getDocument().getData();
                        user.setNick((String) map.get("friendNick"));
                        user.setId(dc.getDocument().getId());
                        response.onDelete(user);
                        break;
                }
            }

        });
    }

    public void getCurrentUser(FirestoreCompleteCallbackData<HelpfulUser> document) {

        getDocumentFromDb(getCurrentUserId(), r -> {
            if (r != null) {
                User user = r.toObject(User.class);
                HelpfulUser userWithId = new HelpfulUser(r.getId(), user);
                document.onCallback(userWithId);
            } else {
                document.onCallback(null);
            }
        });

    }


    public static FirebaseUser checkUserLoggedIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentLoggedUser = mAuth.getCurrentUser();
        return currentLoggedUser;

    }

    public static void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    public void addUserProfilePhoto(String name, Uri photoUri, FirestoreCompleteCallbackData<Uri> callback) {
        updatePhoto(name, photoUri, "profilePhoto", USER_DEFAULT_PHOTO, callback);
    }

    public void getUserProfilePhotoUri(String userId, FirestoreCompleteCallbackData<Uri> uri) {

        StorageReference storageRef = storage.getReferenceFromUrl("gs://gymdiary-3ba5e.appspot.com");

        Task<Uri> pathReference = storageRef.child("profilePhoto/" + userId + ".jpg").getDownloadUrl();
        pathReference.addOnCompleteListener(c -> {

            if (c.isSuccessful()) {
                Uri photoUri = c.getResult();
                uri.onCallback(photoUri);
            }
        });
    }

    public void checkUserIsMyFriend(String userId, FirestoreCompleteCallbackData<Boolean> isMyFriend) {
        db.collection(collection).document(getCurrentUserId()).collection("Friends").document(userId).get().addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                if (c.getResult().exists())
                    isMyFriend.onCallback(true);
                else isMyFriend.onCallback(false);
            } else
                isMyFriend.onCallback(false);

        });
    }

    public void recoverPassword(String userEmail, FirestoreCompleteCallbackStatus status) {
        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                status.onCallback(DbStatus.Success);
            } else
                status.onCallback(DbStatus.Failed);
        });
    }

}
