package com.example.przemek.gymdiary.ViewHolders;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przemek.gymdiary.DbManagement.PostsManagement;
import com.example.przemek.gymdiary.Dialogs.PostCommentsDialog;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulPost;
import com.example.przemek.gymdiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.post_user_photo)
    CircleImageView profilePhoto;
    @BindView(R.id.post_user_name)
    TextView userName;
    @BindView(R.id.post_message)
    TextView postMessage;
    @BindView(R.id.post_image)
    ImageView postImage;
    @BindView(R.id.post_like_button)
    Button likeButton;
    @BindView(R.id.post_comment_button)
    Button commentButton;
    @BindView(R.id.post_date)
    TextView postDate;
    @BindView(R.id.post_remove)
    ImageView removeButton;

    private Activity context;

    private String postId;

    public PostViewHolder(View itemView, Activity context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;

        removeButton.setOnClickListener(c -> {
            removePost();
        });

    }

    private void removePost() {
        PostsManagement postsManagement = new PostsManagement();
        postsManagement.removePost(postId, status -> {
            if (status == DbStatus.Success)
                Toast.makeText(context, "Usunięto post", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Wystąpił błąd podczas usuwania", Toast.LENGTH_SHORT).show();
        });

    }


    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public void setPostImage(Uri uri) {
        this.postImage = postImage;
    }

    public void setPostMessage(String postMessage) {
        this.postMessage.setText(postMessage);
    }

    public void setProfilePhoto(CircleImageView profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public CircleImageView getProfilePhoto() {
        return profilePhoto;
    }

    public ImageView getPostImage() {
        return postImage;
    }

    public TextView getPostMessage() {
        return postMessage;
    }

    public TextView getUserName() {
        return userName;
    }

    public Button getLikeButton() {
        return likeButton;
    }

    public void setPostDate(TextView postDate) {
        this.postDate = postDate;
    }

    public TextView getPostDate() {
        return postDate;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setLikesHanding(HelpfulPost post) {

//        if (!likeButton.getText().toString().contains("("))
        likeButton.setText("Polub (" + String.valueOf(post.getLikersIds().size() + ")"));

        likeButton.setOnClickListener(c -> {

            PostsManagement postsManagement = new PostsManagement();
            postsManagement.addOrRemoveLike(post, status -> {
                if (status == DbStatus.Liked) {
                    Drawable img = context.getDrawable(R.drawable.ic_thumb_up_primary_24dp);
                    likeButton.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    likeButton.setTextColor(context.getColor(R.color.colorPrimaryDark));
                }
                if (status == DbStatus.Unliked) {
                    Drawable img = context.getDrawable(R.drawable.ic_thumb_up_black_24dp);
                    likeButton.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
                    likeButton.setTextColor(context.getColor(R.color.colorButtonDisabled));

                }

            });

        });
    }

    public void setRemoveButton() {
        this.removeButton.setVisibility(View.VISIBLE);
    }


    public void setCommentHandling(HelpfulPost post) {


//        if (!commentButton.getText().toString().contains("("))
        commentButton.setText("Skomentuj (" + String.valueOf(post.getCommentsCount() + ")"));


        commentButton.setOnClickListener(c -> {
            FragmentManager fm = context.getFragmentManager();
            PostCommentsDialog commentsDialog = new PostCommentsDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("post", post);
            commentsDialog.setArguments(bundle);
            commentsDialog.show(fm, "comments");
        });
    }

    public void setLikes(HelpfulPost post) {
        likeButton.setText(likeButton.getText() + " (" + String.valueOf(post.getLikersIds().size() + ")"));
    }

}
