package com.example.przemek.gymdiary.DbManagement;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Enums.FirestoreCollections;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackData;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackStatus;
import com.example.przemek.gymdiary.Interfaces.FirestoreCompleteCallbackWithId;
import com.example.przemek.gymdiary.Interfaces.ObserveCollectionInterface;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulMessage;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulPost;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulPostComment;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.Models.Like;
import com.example.przemek.gymdiary.Models.Message;
import com.example.przemek.gymdiary.Models.Post;
import com.example.przemek.gymdiary.Models.PostComment;
import com.google.android.gms.common.api.Batch;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class PostsManagement extends DbManagement {

    private String collection = FirestoreCollections.Posts.getName();
    private ListenerRegistration listenerPosts;
    private ListenerRegistration listenerPostsComments;
    private DocumentSnapshot lastVisibleMessage;


    public PostsManagement() {
        super(FirestoreCollections.Posts.getName());
    }


    @Override
    <TModel> void updateDocument(Context context, String documentId, TModel updateData, FirestoreCompleteCallbackStatus callbackStatus) {
    }

    public void addPost(HelpfulUser user, String message, Uri postPhoto, FirestoreCompleteCallbackStatus status) {
        Post post = new Post(message, user.getId(), user.getNick(), postPhoto == null ? null : postPhoto.toString());
        addDocumentToDb(post, status);
    }


    public void getUserPosts(String userId, FirestoreCompleteCallbackData<ArrayList<HelpfulPost>> posts) {
        ArrayList<HelpfulPost> data = new ArrayList<>();
        getDocumentsFromDbWithSpecifiedField("userId", userId, response -> {
            for (DocumentSnapshot doc : response
                    ) {
                Post p = doc.toObject(Post.class);
                HelpfulPost post = new HelpfulPost(doc.getId(), p);
                data.add(post);
            }
            posts.onCallback(data);
        });
    }

    public void getInitialSocietyPosts(FirestoreCompleteCallbackData<ArrayList<HelpfulPost>> posts) {

        ArrayList<HelpfulPost> data = new ArrayList<>();
        db.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                List<DocumentSnapshot> documents = c.getResult().getDocuments();
                for (DocumentSnapshot doc : documents
                        ) {
                    Post p = doc.toObject(Post.class);
                    HelpfulPost post = new HelpfulPost(doc.getId(), p);
                    data.add(post);
                }
                if (!documents.isEmpty())
                    lastVisibleMessage = c.getResult().getDocuments().get(c.getResult().size() - 1);
                posts.onCallback(data);
            }
        });
    }

    public void getNextSocietyPosts(FirestoreCompleteCallbackData<ArrayList<HelpfulPost>> posts) {
        db.collection(collection).orderBy("timestamp", Query.Direction.DESCENDING).startAt(lastVisibleMessage).limit(5).get().addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                ArrayList<HelpfulPost> data = new ArrayList<>();
                QuerySnapshot docs = c.getResult();
                if (!docs.isEmpty()) {
                    for (DocumentSnapshot doc : docs.getDocuments()
                            ) {
                        if (doc.exists()) {
                            String id = doc.getId();
                            Post p = doc.toObject(Post.class);
                            HelpfulPost post = new HelpfulPost(id, p);
                            data.add(post);
                        }
                    }
                    lastVisibleMessage = c.getResult().getDocuments().get(c.getResult().size() - 1);
                }
                posts.onCallback(data);
            }
        });
    }

    public void addOrRemoveLike(HelpfulPost post, FirestoreCompleteCallbackStatus status) {

        ArrayList<String> arrayOfLikes = post.getLikersIds();
        WriteBatch batch = db.batch();
        DocumentReference postRef = db.collection("Posts").document(post.getId());
        boolean isLike = isThereLike(arrayOfLikes);
        if (isLike) {
            arrayOfLikes.remove(getCurrentUserId());
        } else {
            arrayOfLikes.add(getCurrentUserId());
        }
        batch.update(postRef, "likersIds", arrayOfLikes);

        batch.commit().addOnCompleteListener(c -> {

            if (c.isSuccessful())
                status.onCallback(isLike ? DbStatus.Unliked : DbStatus.Liked);
            else {
                status.onCallback(DbStatus.Failed);
            }

        });

    }

    public void getPostComments(String postId, FirestoreCompleteCallbackData<ArrayList<HelpfulPostComment>> comments) {
        ArrayList<HelpfulPostComment> commentsList = new ArrayList<>();
        db.collection(collection).document(postId).collection("Comments").orderBy("wroteDate", Query.Direction.ASCENDING).get().addOnCompleteListener(c -> {
            if (c.isSuccessful()) {
                QuerySnapshot documentSnapshots = c.getResult();
                for (DocumentSnapshot doc : documentSnapshots.getDocuments()
                        ) {
                    HelpfulPostComment postComment = new HelpfulPostComment(doc.getId(), doc.toObject(PostComment.class));
                    commentsList.add(postComment);
                }
                comments.onCallback(commentsList);
            }
            comments.onCallback(commentsList);
        });

    }

    public void observePostsComments(ObserveCollectionInterface<HelpfulPostComment> response, Activity context, String postId) {

        listenerPostsComments = db.collection(collection).document(postId).collection("Comments").addSnapshotListener(context, (queryDocumentSnapshots, e) -> {

            if (e != null) {
                Log.w("asd", "Listen failed.", e);
                return;
            }

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                switch (dc.getType()) {

                    case ADDED:
                        HelpfulPostComment comment = new HelpfulPostComment();

                        Map<String, Object> map = dc.getDocument().getData();
                        comment.setId(dc.getDocument().getId());
                        comment.setMessage((String) map.get("message"));
                        comment.setUserId((String) map.get("userId"));
                        comment.setUserNick((String) map.get("userNick"));
                        comment.setWroteDate((Date) map.get("wroteDate"));
                        response.onAdd(comment);
                        break;
                    case MODIFIED:
                        HelpfulPost post2 = new HelpfulPost();

//                        Map<String, Object> map2 = dc.getDocument().getData();
//                        post2.setId(dc.getDocument().getId());
//                        post2.setCommentsCount(Math.toIntExact((Long) map2.get("postComments")));
//                        post2.setLikersIds((ArrayList<String>) map2.get("likersIds"));
//                        post2.setMessage((String) map2.get("message"));
//                        post2.setPhotoUrl((String) map2.get("photoUrl"));
//                        post2.setTimestamp((Date) map2.get("timestamp"));
//                        post2.setUserId((String) map2.get("userId"));
//                        post2.setUserName((String) map2.get("userName"));

                    case REMOVED:
                        //TODO in the future
                        break;
                }
            }

        });
    }

    public void addPostComment(String postId, String messsage, FirestoreCompleteCallbackStatus status) {

        final DocumentReference postRef = db.collection(collection).document(postId);
        final CollectionReference commentsRef = postRef.collection("Comments");

        UserManagement userManagement = new UserManagement();
        userManagement.getUser(getCurrentUserId(), user -> {
            PostComment comment = new PostComment(user.getId(), user.getNick(), messsage);
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot post = transaction.get(postRef);
                postRef.collection("Comments").add(comment);

                double commentsCount = post.getDouble("commentsCount") + 1;
                transaction.update(postRef, "commentsCount", commentsCount);

                return null;
            }).addOnSuccessListener(s -> {
                status.onCallback(DbStatus.Success);
            });
        });
    }

    public void removePost(String postId, FirestoreCompleteCallbackStatus status){
        removeDocumentFromDb(postId, status);
    }

    public void observePosts(ObserveCollectionInterface<HelpfulPost> response, Activity context, String userId) {

        listenerPosts = db.collection(collection).whereEqualTo("userId", userId).addSnapshotListener(context, (queryDocumentSnapshots, e) -> {

            if (e != null) {
                Log.w("asd", "Listen failed.", e);
                return;
            }


            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                switch (dc.getType()) {

                    case ADDED:
                        HelpfulPost post = new HelpfulPost();

                        Map<String, Object> map = dc.getDocument().getData();
                        post.setId(dc.getDocument().getId());
                        post.setCommentsCount(0);
                        post.setLikersIds((ArrayList<String>) map.get("likersIds"));
                        post.setMessage((String) map.get("message"));
                        post.setPhotoUrl((String) map.get("photoUrl"));
                        post.setTimestamp((Date) map.get("timestamp"));
                        post.setUserId((String) map.get("userId"));
                        post.setUserName((String) map.get("userName"));
                        response.onAdd(post);
                        break;
                    case MODIFIED:
                        HelpfulPost post2 = new HelpfulPost();
                        Map<String, Object> map2 = dc.getDocument().getData();
                        post2.setId(dc.getDocument().getId());
                        post2.setCommentsCount(dc.getDocument().getLong("commentsCount").intValue());
                        post2.setLikersIds((ArrayList<String>) map2.get("likersIds"));
                        post2.setMessage((String) map2.get("message"));
                        post2.setPhotoUrl((String) map2.get("photoUrl"));
                        post2.setTimestamp((Date) map2.get("timestamp"));
                        post2.setUserId((String) map2.get("userId"));
                        post2.setUserName((String) map2.get("userName"));
                        response.onModify(post2);
                    case REMOVED:
                        HelpfulPost post3= new HelpfulPost();
                        Map<String, Object> map3 = dc.getDocument().getData();
                        post3.setId(dc.getDocument().getId());
                        post3.setCommentsCount(dc.getDocument().getLong("commentsCount").intValue());
                        post3.setLikersIds((ArrayList<String>) map3.get("likersIds"));
                        post3.setMessage((String) map3.get("message"));
                        post3.setPhotoUrl((String) map3.get("photoUrl"));
                        post3.setTimestamp((Date) map3.get("timestamp"));
                        post3.setUserId((String) map3.get("userId"));
                        post3.setUserName((String) map3.get("userName"));
                        response.onDelete(post3);
                        break;
                }
            }

        });
    }


    private boolean isThereLike(ArrayList<String> likes) {

        for (String id : likes
                ) {
            if (id.equals(getCurrentUserId())) {
                return true;
            }
        }
        return false;
    }

    public void addPostPhoto(Uri uri, String name, FirestoreCompleteCallbackData<Uri> callback) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference photo_reference = mStorage.child("postImages").child(name + ".jpg");
        photo_reference.putFile(uri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Putting photo  : ", "Success");
                task.getResult().getMetadata().getReference().getDownloadUrl().addOnCompleteListener(c -> {
                    Uri photoUri = c.getResult();
                    callback.onCallback(photoUri);
                });
            } else {
                Log.d("Putting photo error : ", task.getException().getMessage());
                callback.onCallback(null);
            }
        });
    }


    public ListenerRegistration getListenerPosts() {
        return listenerPosts;
    }

}
