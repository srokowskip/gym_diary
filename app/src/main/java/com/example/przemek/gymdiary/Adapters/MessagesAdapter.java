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
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulMessage;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewHolders.ReceivedMessageViewHolder;
import com.example.przemek.gymdiary.ViewHolders.SentMessageViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MessagesAdapter extends RecyclerView.Adapter {

    private final static int MESSAGE_SENT = 1;
    private final static int MESSAGE_RECEIVED = 2;
    private Context context;
    private ArrayList<HelpfulMessage> data;
    private UserManagement userManagement = new UserManagement();

    public MessagesAdapter(Context context, ArrayList<HelpfulMessage> messages) {
        this.context = context;
        this.data = messages;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sent_message, parent, false);
            return new SentMessageViewHolder(view);
        } else if (viewType == MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_received_message, parent, false);
            return new ReceivedMessageViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HelpfulMessage message = data.get(position);
        Date messageSendDate = message.getWroteDate();

        switch (holder.getItemViewType()) {
            case MESSAGE_SENT:
                ((SentMessageViewHolder) holder).getTvBody().setText(message.getMessage());
                if (Calendar.getInstance().getTime().equals(messageSendDate)) {
                    ((SentMessageViewHolder) holder).getTvTime().setText(Helper.DateFormat(messageSendDate, "HH:mm"));
                } else {
                    ((SentMessageViewHolder) holder).getTvTime().setText(Helper.DateFormat(messageSendDate, "dd.MM"));
                }
                break;
            case MESSAGE_RECEIVED:
                userManagement.getUserProfilePhotoUri(message.getWriter().getId(), response -> {
                    if (response != null)
                        Glide.with(context).setDefaultRequestOptions(Helper.getPlaceholder()).load(response).into(((ReceivedMessageViewHolder) holder).getImgProfile());
                });
                ((ReceivedMessageViewHolder) holder).getTextBody().setText(message.getMessage());
                ((ReceivedMessageViewHolder) holder).getTextName().setText(message.getWriter().getNick());

                if (Calendar.getInstance().getTime().equals(messageSendDate)) {
                    ((ReceivedMessageViewHolder) holder).getTextTime().setText(Helper.DateFormat(messageSendDate, "HH:mm"));
                } else {
                    ((ReceivedMessageViewHolder) holder).getTextTime().setText(Helper.DateFormat(messageSendDate, "dd.MM"));
                }

        }

    }

    @Override
    public int getItemViewType(int position) {

        HelpfulMessage message = data.get(position);
        if (message.getWriter().getId().equals(userManagement.getCurrentUserId())) {
            return MESSAGE_SENT;
        } else
            return MESSAGE_RECEIVED;

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
