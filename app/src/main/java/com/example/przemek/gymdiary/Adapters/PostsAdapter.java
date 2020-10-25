package com.example.przemek.gymdiary.Adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.przemek.gymdiary.DbManagement.DbManagement;
import com.example.przemek.gymdiary.DbManagement.PostsManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulPost;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.PostViewHolder;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private ArrayList<HelpfulPost> mPosts;
    private RecyclerView mRecyclerView;
    private Activity mContext;
    private String userId;

    public PostsAdapter(ArrayList<HelpfulPost> data, RecyclerView recyclerView, Activity context) {

        UserManagement userManagement = new UserManagement();

        this.mPosts = data;
        this.mRecyclerView = recyclerView;
        this.mContext = context;
        this.userId = userManagement.getCurrentUserId();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(view, mContext);

    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        HelpfulPost helpfulPost = mPosts.get(position);

        holder.setPostMessage(helpfulPost.getMessage());
        holder.setUserName(helpfulPost.getUserName());
        holder.setLikesHanding(helpfulPost);
        UserManagement userManagement = new UserManagement();

        boolean isThisPostLikedByMe = helpfulPost.getLikersIds().contains(userManagement.getCurrentUserId());


        Drawable drawable = isThisPostLikedByMe ? mContext.getDrawable(R.drawable.ic_thumb_up_primary_24dp) : mContext.getDrawable(R.drawable.ic_thumb_up_black_24dp);
        holder.getLikeButton().setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

        holder.getLikeButton().setTextColor(isThisPostLikedByMe ? mContext.getColor(R.color.colorPrimaryDark) : mContext.getColor(R.color.colorButtonDisabled));
        userManagement.getUserProfilePhotoUri(helpfulPost.getUserId(), uri -> Glide.with(mContext).setDefaultRequestOptions(Helper.getPlaceholder()).load(uri).into(holder.getProfilePhoto()));

        if (helpfulPost.getPhotoUrl() != null) {
            holder.getPostImage().setVisibility(View.VISIBLE);
            Glide.with(mContext).setDefaultRequestOptions(Helper.getPlaceholder()).load(helpfulPost.getPhotoUrl()).into(holder.getPostImage());
        }

        holder.getPostDate().setText(Helper.DateFormat(helpfulPost.getTimestamp(), "dd.MM.yyyy hh:mm"));
        holder.setPostId(helpfulPost.getId());
        holder.setCommentHandling(helpfulPost);

        if (helpfulPost.getUserId().equals(userId))
            holder.setRemoveButton();

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }


}
