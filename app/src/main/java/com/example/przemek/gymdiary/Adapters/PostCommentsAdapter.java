package com.example.przemek.gymdiary.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulPostComment;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.PostCommentViewHolder;

import java.util.ArrayList;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentViewHolder> {

    private ArrayList<HelpfulPostComment> comments;
    private Context context;

    public PostCommentsAdapter(Context context, ArrayList<HelpfulPostComment> comments) {
        this.context = context;
        this.comments = comments;

    }

    @NonNull
    @Override
    public PostCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_comment, parent, false);

        return new PostCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentViewHolder holder, int position) {

        HelpfulPostComment comment = comments.get(position);

        holder.setTvMessage(comment.getMessage());
        holder.setUserName(comment.getUserNick());

        UserManagement userManagement = new UserManagement();
        userManagement.getUserProfilePhotoUri(comment.getUserId(), uri -> Glide.with(context).setDefaultRequestOptions(Helper.getPlaceholder()).load(uri).into(holder.getProfilePhoto()));


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
