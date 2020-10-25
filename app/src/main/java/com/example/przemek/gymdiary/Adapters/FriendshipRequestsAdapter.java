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
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulFriendshipRequest;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.UserViewHolder;

import java.util.ArrayList;

public class FriendshipRequestsAdapter extends RecyclerView.Adapter<UserViewHolder> {
    public static final int LIST_OF_FRIENDSHIP_REQUESTS = 1;
    public static final int LIST_OF_SENT_REQUESTS = 2;
    private ArrayList<HelpfulFriendshipRequest> mData;
    private Context context;

    private int kindOfList = -1;

    public FriendshipRequestsAdapter(ArrayList<HelpfulFriendshipRequest> data, int kindOfList, Context context) {
        this.mData = data;
        this.kindOfList = kindOfList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        HelpfulFriendshipRequest request = mData.get(position);
        holder.setUserName(kindOfList == LIST_OF_FRIENDSHIP_REQUESTS ? request.getInitiatorNick() : request.getAskedNick());
        holder.setUserId(kindOfList == LIST_OF_FRIENDSHIP_REQUESTS ? request.getInitiatorId() : request.getAskedId());

        holder.setFriendshipRequest(request);
        if (kindOfList == LIST_OF_FRIENDSHIP_REQUESTS)
            holder.setReactionOnFriendshipButtonVisiblility();
        if (kindOfList == LIST_OF_SENT_REQUESTS)
            holder.setDate(request.getStartDate());

        UserManagement userManagement = new UserManagement();
        userManagement.getUserProfilePhotoUri(holder.getUserId(), response -> {

            if (response != null)
                Glide.with(context).setDefaultRequestOptions(Helper.getPlaceholder()).load(response).into(holder.getProfilePhoto());
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
