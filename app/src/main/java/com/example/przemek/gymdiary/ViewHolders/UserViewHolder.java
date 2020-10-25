package com.example.przemek.gymdiary.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przemek.gymdiary.Activities.ChatActivity;
import com.example.przemek.gymdiary.Activities.FriendsListActivity;
import com.example.przemek.gymdiary.Activities.UserProfileActivity;
import com.example.przemek.gymdiary.DbManagement.ChatManagement;
import com.example.przemek.gymdiary.DbManagement.FriendshipManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.Chat;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulFriendshipRequest;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.R;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.item_user_container)
    ConstraintLayout container;
    @BindView(R.id.user_name)
    TextView tvUserName;
    @BindView(R.id.user_add_button)
    ImageButton addFriendButton;
    @BindView(R.id.user_accept_friendship_button)
    ImageButton acceptFriendshipButton;
    @BindView(R.id.user_deceline_frienship_button)
    ImageButton decelineFriendshipButton;
    @BindView(R.id.user_image)
    CircleImageView profilePhoto;
    @BindView(R.id.user_pb)
    ProgressBar pb;
    @BindView(R.id.user_date)
    TextView tvDate;
    @BindView(R.id.user_message)
    ImageButton messageButton;


    Context context;
    private String userId;
    private String userNick;
    private String userName;
    private String userSurname;


    private HelpfulFriendshipRequest friendshipRequest;

    public UserViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        setContainerClick();

    }

    private void setContainerClick() {

        container.setOnClickListener(c -> {
            Intent i = new Intent(context, UserProfileActivity.class);
            i.putExtra("userId", userId);
            context.startActivity(i);
        });

    }

    public void setMessageButton() {

        messageButton.setVisibility(View.VISIBLE);
        messageButton.setOnClickListener(c -> {
            ChatManagement chatManagement = new ChatManagement();
            ArrayList<String> userIds = new ArrayList<>();
            userIds.add(userId);
            userIds.add(chatManagement.getCurrentUserId());
            chatManagement.startOrGoChat(userId, (status, id) -> {
                if (status == DbStatus.Success) {
                    FriendsListActivity activity = (FriendsListActivity) context;
                    HelpfulUser currentUser = activity.getUserObj();
                    Toast.makeText(activity, "Utworzono czat" + id, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(activity, ChatActivity.class);
                    i.putExtra("chatId", id);
                    i.putExtra("user", currentUser);
                    Toast.makeText(activity, currentUser.getId(), Toast.LENGTH_SHORT).show();
                    activity.startActivity(i);
                    //TODO FINISH?
                } else
                    Toast.makeText(context, "Nie utworzono", Toast.LENGTH_SHORT).show();
            });
        });
    }

    public void setAddButtonVisibility() {

        addFriendButton.setVisibility(View.VISIBLE);

        addFriendButton.setOnClickListener(c -> {
            addFriendButton.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);

            FriendshipManagement friendshipManagement = new FriendshipManagement();
            UserManagement userManagement = new UserManagement();
            userManagement.getCurrentUser(r -> {
                if (r != null) {
                    friendshipManagement.addFriendshipRequest(userId, userNick, r, status -> {

                        if (status == DbStatus.Success)
                            pb.setVisibility(View.GONE);

                    });
                } else {
                    //TODO error handling
                    addFriendButton.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                }
            });
        });


    }

    public void setReactionOnFriendshipButtonVisiblility() {

        acceptFriendshipButton.setVisibility(View.VISIBLE);
        decelineFriendshipButton.setVisibility(View.VISIBLE);

        FriendshipManagement friendshipManagement = new FriendshipManagement();

        acceptFriendshipButton.setOnClickListener(c -> {

            acceptFriendshipButton.setVisibility(View.GONE);
            decelineFriendshipButton.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);

            friendshipManagement.handleAcceptFriendshipRequestAcceptation(friendshipRequest, response -> {

                if (response == DbStatus.Success) {
                    pb.setVisibility(View.GONE);
                } else {

                    pb.setVisibility(View.GONE);
                    acceptFriendshipButton.setVisibility(View.VISIBLE);
                    decelineFriendshipButton.setVisibility(View.VISIBLE);

                }
            });

        });
        decelineFriendshipButton.setOnClickListener(c -> {
            acceptFriendshipButton.setVisibility(View.GONE);
            decelineFriendshipButton.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);

            friendshipManagement.decelineFriendshipRequest(friendshipRequest, response -> {

                if (response == DbStatus.Success) {
                    pb.setVisibility(View.GONE);
                } else {
                    pb.setVisibility(View.GONE);
                    acceptFriendshipButton.setVisibility(View.VISIBLE);
                    decelineFriendshipButton.setVisibility(View.VISIBLE);
                }
            });
        });


    }

    public CircleImageView getProfilePhoto() {
        return profilePhoto;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
        tvUserName.setText(userNick);
    }

    public void setUserName(String userName) {
        this.userName = userName;
        tvUserName.setText(userName);
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
//        tvUserName.setText(userName + " " + userSurname + " (" + userNick + ")");

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public HelpfulFriendshipRequest getFriendshipRequest() {
        return friendshipRequest;
    }

    public void setFriendshipRequest(HelpfulFriendshipRequest friendshipRequest) {
        this.friendshipRequest = friendshipRequest;
    }

    public void setDate(Date date) {
        tvDate.setVisibility(View.VISIBLE);
        tvDate.setText("Wysłano " + Helper.DateFormat(date, "dd.MM.yyyy"));
    }

}

