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
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.UserViewHolder;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private ArrayList<HelpfulUser> mData;
    private int kindOfList;
    private Context context;

    public static final int LIST_OF_FRIENDS = 1;
    public static final int LIST_OF_PROPOSAL_FRIENDS = 2;


    public UserListAdapter(ArrayList<HelpfulUser> data, int kindOfList, Context context) {
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

        String userNick = "";
        String userId = "";

        HelpfulUser u = mData.get(position);
        userNick = u.getNick();
        userId = u.getId();

        if (kindOfList == LIST_OF_FRIENDS) {
            holder.setMessageButton();
        } else {
            holder.setAddButtonVisibility();
        }

        holder.setUserNick(userNick);
        holder.setUserId(userId);

        Glide.with(context).setDefaultRequestOptions(Helper.getPlaceholder()).load(u.getProfilePhotoUrl()).into(holder.getProfilePhoto());

    }

    @Override
    public int getItemCount() {
        return mData.size();

    }
}
