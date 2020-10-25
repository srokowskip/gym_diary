package com.example.przemek.gymdiary.DbManagement;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.util.Log;
import android.widget.Toast;

import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackWithId;
import com.example.przemek.gymdiary.Interfaces.ObserveCollectionInterface;
import com.example.przemek.gymdiary.Models.Chat;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulChat;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulFriendshipRequest;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulMessage;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.Models.Message;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class ChatManagement extends DbManagement {

    private String collection = FirestoreCollections.Chats.getName();
    private ListenerRegistration listenerMessages;
    private DocumentSnapshot lastVisibleMessage;

    public ChatManagement() {
        super(FirestoreCollections.Chats.getName());
    }


    @Override
    <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus) {
    }

    public void startOrGoChat(String invitedUserId, FirestoreCompleteCallbackWithId status) {

        String currentUser = getCurrentUserId();
        ArrayList<String> userIds = new ArrayList<>();
        userIds.add(currentUser);
        userIds.add(invitedUserId);

        db.collection(collection).whereEqualTo("participantIds", userIds).limit(1).get().addOnCompleteListener(c -> {

            if (c.isSuccessful()) {
                QuerySnapshot snapshots = c.getResult();
                if (snapshots.isEmpty()) {
                    Collections.reverse(userIds);
                    db.collection(collection).whereEqualTo("participantIds", userIds).limit(1).get().addOnCompleteListener(d -> {
                        QuerySnapshot snapshots2 = d.getResult();
                        if (snapshots2.isEmpty()) {
                            Collections.reverse(userIds);
                            Chat chat = new Chat(userIds);
                            addDocumentToDb(chat, status);
                        } else {
                            String chatId = snapshots2.getDocuments().get(0).getId();
                            status.onCallback(DbStatus.Success, chatId);
                        }
                    });
                } else {
                    String chatId = snapshots.getDocuments().get(0).getId();
                    status.onCallback(DbStatus.Success, chatId);
                }
            } else {
                status.onCallback(DbStatus.Failed, null);
            }
        });


    }

    public void getChat(String chatId, FirestoreCompleteCallbackData<HelpfulChat> data) {

        getDocumentFromDb(chatId, response -> {
            String id = response.getId();
            Chat ch = response.toObject(Chat.class);
            HelpfulChat chat = new HelpfulChat(id, ch);

            data.onCallback(chat);

        });

    }

    public void getInitialChatMessages(String chatId, FirestoreCompleteCallbackData<ArrayList<HelpfulMessage>> data) {

        db.collection(collection).document(chatId).collection("Messages").orderBy("wroteDate", Query.Direction.DESCENDING).limit(15).get().addOnCompleteListener(c -> {

            if (c.isSuccessful()) {
                ArrayList<HelpfulMessage> messages = new ArrayList<>();
                QuerySnapshot docs = c.getResult();
                for (DocumentSnapshot doc : docs.getDocuments()
                        ) {
                    String id = doc.getId();
                    Message m = doc.toObject(Message.class);
                    HelpfulMessage message = new HelpfulMessage(id, m);
                    messages.add(message);
                }

                if (!docs.isEmpty())
                    lastVisibleMessage = c.getResult().getDocuments().get(c.getResult().size() - 1);
                data.onCallback(messages);
            }

        });

    }

    public void getNextChatMessages(String chatId, FirestoreCompleteCallbackData<ArrayList<HelpfulMessage>> data) {

        db.collection(collection).document(chatId).collection("Messages").orderBy("wroteDate", Query.Direction.DESCENDING).startAt(lastVisibleMessage).limit(5).get().addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                ArrayList<HelpfulMessage> messages = new ArrayList<>();
                QuerySnapshot docs = c.getResult();
                if (!docs.isEmpty()) {
                    for (DocumentSnapshot doc : docs.getDocuments()
                            ) {
                        if (doc.exists()) {
                            String id = doc.getId();
                            Message m = doc.toObject(Message.class);
                            HelpfulMessage message = new HelpfulMessage(id, m);
                            messages.add(message);
                        }
                    }
                    lastVisibleMessage = c.getResult().getDocuments().get(c.getResult().size() - 1);
                }
                data.onCallback(messages);
            }
        });

    }

    public void sendMessage(String chatId, Message message, FirestoreCompleteCallbackStatus status) {
        db.collection(collection).document(chatId).collection("Messages").add(message).addOnCompleteListener(c -> {
            if (c.isSuccessful())
                status.onCallback(DbStatus.Success);
            else
                status.onCallback(DbStatus.Failed);

        });
    }

    public void observeMessages(ObserveCollectionInterface<HelpfulMessage> response, Activity context, String chatId) {

        listenerMessages = db.collection(collection).document(chatId).collection("Messages").addSnapshotListener(context, (queryDocumentSnapshots, e) -> {

            if (e != null) {
                Log.w("asd", "Listen failed.", e);
                return;
            }

            HelpfulMessage message = new HelpfulMessage();

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                switch (dc.getType()) {

                    case ADDED:
                        Map<String, Object> map = dc.getDocument().getData();
                        message.setId(dc.getDocument().getId());
                        message.setWriter(Helper.getUserFromHashMap((Map<String, Object>) map.get("writer")));
                        message.setMessage((String) map.get("message"));
                        message.setWroteDate((Date) map.get("wroteDate"));
                        response.onAdd(message);
                        break;
                    case MODIFIED:
                        Map<String, Object> map2 = dc.getDocument().getData();
                        Log.d("dupa", map2.toString());
                    case REMOVED:
                        //TODO in the future
                        break;
                }
            }

        });
    }

    public ListenerRegistration getListenerMessages() {
        return listenerMessages;
    }
}
