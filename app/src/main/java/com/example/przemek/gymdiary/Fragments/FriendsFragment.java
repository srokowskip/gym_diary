package com.example.przemek.gymdiary.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.przemek.gymdiary.Activities.FriendsListActivity;
import com.example.przemek.gymdiary.Adapters.FriendshipRequestsAdapter;
import com.example.przemek.gymdiary.Adapters.UserListAdapter;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulFriendshipRequest;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.R;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsFragment extends Fragment {

    @BindView(R.id.my_friend_list_layout)
    LinearLayout myFriendsLayout;
    @BindView(R.id.my_users_list)
    RecyclerView list;
    @BindView(R.id.list_tv)
    TextView header;
    @BindView(R.id.sec_list)
    RecyclerView list2;
    @BindView(R.id.sec_tv)
    TextView header2;

    @BindView(R.id.invitations_list_layout)
    LinearLayout invitationsListLayout;
    @BindView(R.id.container_your_invitations)
    ConstraintLayout layoutYourInvitations;
    @BindView(R.id.tv_your_invitations)
    TextView tvYourInvitations;
    @BindView(R.id.btn_your_invitations)
    ImageButton btnYourInvitations;
    @BindView(R.id.list_your_invitations)
    RecyclerView listYourInvitations;
    @BindView(R.id.container_my_invitations)
    ConstraintLayout layoutMyInvitations;
    @BindView(R.id.tv_my_invitations)
    TextView tvMyInvitations;
    @BindView(R.id.btn_my_invitations)
    ImageButton btnMyInvitations;
    @BindView(R.id.list_my_invitations)
    RecyclerView listMyInvitations;

    FriendsListActivity parent;

    UserListAdapter usersAdapter;
    FriendshipRequestsAdapter receivedRequestsAdapter;
    FriendshipRequestsAdapter sentRequestsAdapter;

    private ArrayList<HelpfulUser> listOfUsers = new ArrayList<>();
    private ArrayList<HelpfulFriendshipRequest> listOfRequests = new ArrayList<>();
    private ArrayList<HelpfulFriendshipRequest> listOfRequestsSentByUser = new ArrayList<>();

    private int fragmentKind = -1;
    public final static int LIST_OF_USER_FRIENDS = 1;
    public final static int LIST_OF_USER_INVITATIONS = 2;
    public final static int LIST_OF_USER_PROPOSALS = 3;
    public final static int LIST_OF_USER_SENT_INVITATIONS = 4;


    public static android.support.v4.app.Fragment newInstance() {
        return new FriendsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        parent = (FriendsListActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Please note the third parameter should be false, otherwise a java.lang.IllegalStateException maybe thrown.
        View retView = inflater.inflate(R.layout.fragment_user_friends_layout, container, false);
        return retView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        performActions();

    }

    private void performActions() {

        if (fragmentKind == LIST_OF_USER_PROPOSALS) {
            myFriendsLayout.setVisibility(View.VISIBLE);
            parent.searcher(true);
        } else if (fragmentKind == LIST_OF_USER_FRIENDS) {
            myFriendsLayout.setVisibility(View.VISIBLE);
            parent.searcher(false);
            usersAdapter = new UserListAdapter(listOfUsers, UserListAdapter.LIST_OF_FRIENDS, getActivity());
            prepareList(list, usersAdapter);
            usersAdapter.notifyDataSetChanged();
        } else if (fragmentKind == LIST_OF_USER_INVITATIONS) {
            parent.searcher(false);
            invitationsListLayout.setVisibility(View.VISIBLE);

            if (listOfRequests.isEmpty()) {
                tvYourInvitations.setText(R.string.no_friend_invitations);
                //TODO weź to zobacz
                btnYourInvitations.setVisibility(View.INVISIBLE);
            }
            if (listOfRequestsSentByUser.isEmpty()) {
                tvMyInvitations.setText("Nikogo nie zapraszasz do znajomych");
                btnMyInvitations.setVisibility(View.INVISIBLE);
            }

            btnYourInvitations.setOnClickListener(c -> {
                listYourInvitations.setVisibility(listYourInvitations.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                btnYourInvitations.setImageResource(listYourInvitations.getVisibility() == View.VISIBLE ? R.drawable.ic_keyboard_arrow_up_primary_24dp : R.drawable.ic_keyboard_arrow_down_primary_24dp);
            });

            btnMyInvitations.setOnClickListener(c -> {
                listMyInvitations.setVisibility(listMyInvitations.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                btnMyInvitations.setImageResource(listMyInvitations.getVisibility() == View.VISIBLE ? R.drawable.ic_keyboard_arrow_up_primary_24dp : R.drawable.ic_keyboard_arrow_down_primary_24dp);
            });

            receivedRequestsAdapter = new FriendshipRequestsAdapter(listOfRequests, FriendshipRequestsAdapter.LIST_OF_FRIENDSHIP_REQUESTS, getActivity());
            sentRequestsAdapter = new FriendshipRequestsAdapter(listOfRequestsSentByUser, FriendshipRequestsAdapter.LIST_OF_SENT_REQUESTS, getActivity());

            prepareList(listYourInvitations, receivedRequestsAdapter);
            prepareList(listMyInvitations, sentRequestsAdapter);

            receivedRequestsAdapter.notifyDataSetChanged();
            sentRequestsAdapter.notifyDataSetChanged();

        }

    }


    private void prepareList(RecyclerView users, RecyclerView.Adapter adapter) {

        users.setHasFixedSize(true);
        users.setLayoutManager(new LinearLayoutManager(getActivity()));
        users.setItemAnimator(new DefaultItemAnimator());
        users.setAdapter(adapter);
    }


    public void setFragmentKind(int fragmentKind) {
        this.fragmentKind = fragmentKind;
    }

    public void setListOfUsers(ArrayList<HelpfulUser> data) {
        this.listOfUsers = data;
    }

    public ArrayList<HelpfulUser> getListOfUsers() {
        return listOfUsers;
    }

    public void setListOfRequests(ArrayList<HelpfulFriendshipRequest> listOfRequests) {
        this.listOfRequests = listOfRequests;
    }

    public ArrayList<HelpfulFriendshipRequest> getListOfRequests() {
        return listOfRequests;
    }

    public void setListOfRequestsSentByUser(ArrayList<HelpfulFriendshipRequest> listOfRequestsSentByUser) {
        this.listOfRequestsSentByUser = listOfRequestsSentByUser;
    }

    public ArrayList<HelpfulFriendshipRequest> getListOfRequestsSentByUser() {
        return listOfRequestsSentByUser;
    }

    public void refresh() {
        usersAdapter = new UserListAdapter(listOfUsers, UserListAdapter.LIST_OF_PROPOSAL_FRIENDS, getActivity());
        prepareList(list, usersAdapter);
        usersAdapter.notifyDataSetChanged();
    }

    public UserListAdapter getUserAdapter() {
        return usersAdapter;
    }

    public FriendshipRequestsAdapter getReceivedRequestsAdapter() {
        return receivedRequestsAdapter;
    }

    public FriendshipRequestsAdapter getSentRequestsAdapter() {
        return sentRequestsAdapter;

    }

}
