package com.example.przemek.gymdiary.Activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.przemek.gymdiary.Adapters.FriendsFragmentAdapter;
import com.example.przemek.gymdiary.DbManagement.FriendshipManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Fragments.FriendsFragment;
import com.example.przemek.gymdiary.Interfaces.ObserveCollectionInterface;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulFriendshipRequest;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.FriendsViewModel;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsListActivity extends AppCompatActivity {

    private final static int USER_FRIEND_OBSERVATOR = 1;
    private final static int USER_FRIENDSHIP_REQUESTS_OBSERVATOR = 2;
    private final static int USER_SENT_FRIENDSHIP_REQUESTS_OBSERVATOR = 3;

    @BindView(R.id.my_friends_toolbar)
    Toolbar toolbar;
    @BindView(R.id.my_friend_searcher_layout)
    LinearLayout searcherContainer;
    @BindView(R.id.my_friend_searcher_et)
    EditText searcherEditText;
    @BindView(R.id.my_friend_searcher_button)
    ImageButton searcherButton;
    @BindView(R.id.my_friend_bottom_navigation)
    BottomNavigationView bottomNavigationView;
    FriendsViewModel mModel = new FriendsViewModel();
    String fragmentTag = "";
    FriendsFragment userFriendsFragment;
    FriendsFragment userInvitationsFragment;
    FriendsFragment userFriendsToAddFragment;
    FriendsFragmentAdapter friendsAdapter;
    FragmentManager fragmentManager = getSupportFragmentManager();
    UserManagement userManagement = new UserManagement();
    FriendshipManagement friendshipManagement = new FriendshipManagement();

    ListenerRegistration reg;
    ListenerRegistration reg2;

    HelpfulUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        currentUser = (HelpfulUser) getIntent().getSerializableExtra("user");

        friendsAdapter = new FriendsFragmentAdapter(fragmentManager);
//        init();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_user_friends:
                    getUserFriends();
                    toolbar.setTitle("Twoi znajomi");
                    break;
                case R.id.action_add_friend:
                    getFriendsToAdd();
                    toolbar.setTitle("Dodaj znajomego");
                    break;
                case R.id.action_requests:
                    getRequests();
                    toolbar.setTitle("Zaproszenia");
                    break;
            }
            return true;
        });

    }

//    private void init() {
//        bottomNavigationView.setSelectedItemId(R.id.action_user_friends);
//        getUserFriends();
//
//    }

    private void getUserFriends() {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (!friendsAdapter.arrayContains(1)) {

            userFriendsFragment = (FriendsFragment) FriendsFragment.newInstance();
            friendsAdapter.putFragment(1, userFriendsFragment);
            userManagement.getCurrentUserFriends(r -> {
                userFriendsFragment.setListOfUsers(r);
                userFriendsFragment.setFragmentKind(FriendsFragment.LIST_OF_USER_FRIENDS);
                detachCurrentFragment(fragmentTransaction, "1");

                fragmentTransaction.replace(R.id.fragment_cointainer, userFriendsFragment, "1");
                fragmentTag = userFriendsFragment.getTag();
                fragmentTransaction.commit();
            });
        } else {

            detachCurrentFragment(fragmentTransaction, "1");
            fragmentTag = userFriendsFragment.getTag();
            Fragment fragment = friendsAdapter.getRegisteredFragment(1);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.commit();

        }

    }

    private void getFriendsToAdd() {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (!friendsAdapter.arrayContains(2)) {

            userFriendsToAddFragment = (FriendsFragment) FriendsFragment.newInstance();
            userFriendsToAddFragment.setFragmentKind(FriendsFragment.LIST_OF_USER_PROPOSALS);
            friendsAdapter.putFragment(2, userFriendsToAddFragment);
            detachCurrentFragment(fragmentTransaction, "2");
            fragmentTransaction.replace(R.id.fragment_cointainer, userFriendsToAddFragment, "2");
            fragmentTag = userFriendsToAddFragment.getTag();
            fragmentTransaction.commit();

        } else {

            detachCurrentFragment(fragmentTransaction, "2");
            fragmentTag = userFriendsToAddFragment.getTag();
            Fragment fragment = friendsAdapter.getRegisteredFragment(2);
            fragmentTransaction.attach(fragment).commit();

        }

    }

    private void detachCurrentFragment(FragmentTransaction fragmentTransaction, String tag) {

        if (!fragmentTag.equals("") && !fragmentTag.equals(tag)) {
            FriendsFragment currentFragment = (FriendsFragment) fragmentManager.findFragmentByTag(fragmentTag);
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .detach(currentFragment);
        }

    }

    private void getRequests() {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (!friendsAdapter.arrayContains(3)) {

            userInvitationsFragment = (FriendsFragment) FriendsFragment.newInstance();
            userInvitationsFragment.setFragmentKind(FriendsFragment.LIST_OF_USER_INVITATIONS);
            friendsAdapter.putFragment(3, userInvitationsFragment);

            friendshipManagement = new FriendshipManagement();
            friendshipManagement.getFriendshipRequests(r -> friendshipManagement.getCurrentUserSentRequests(a -> {
                userInvitationsFragment.setListOfRequests(r);
                userInvitationsFragment.setListOfRequestsSentByUser(a);
                detachCurrentFragment(fragmentTransaction, "3");
                fragmentTransaction.replace(R.id.fragment_cointainer, userInvitationsFragment, "3");
                fragmentTag = userInvitationsFragment.getTag();
                fragmentTransaction.commit();

            }));
        } else {

            detachCurrentFragment(fragmentTransaction, "3");
            fragmentTag = userInvitationsFragment.getTag();
            Fragment fragment = friendsAdapter.getRegisteredFragment(3);
            fragmentTransaction.attach(fragment).commit();

        }

    }

    public void searcher(boolean visible) {
        if (!searcherEditText.getText().toString().isEmpty())
            searcherEditText.setText("");

        searcherContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
        searcherButton.setOnClickListener(c -> {
            userManagement.getUserByNick(searcherEditText.getText().toString(), r -> {
                userFriendsToAddFragment.setListOfUsers(r);
                userFriendsToAddFragment.refresh();
            });

        });

    }


    private <T> Pair<Boolean, Integer> checkItemIsInArray(ArrayList<T> models, T model) {

        for (int i = 0; i < models.size(); i++) {
            if (model instanceof HelpfulUser) {
                HelpfulUser u = (HelpfulUser) models.get(i);
                if (u.getId().equals(((HelpfulUser) model).getId()))
                    return new Pair<>(true, i);
            }
            if (model instanceof HelpfulFriendshipRequest) {
                HelpfulFriendshipRequest request = (HelpfulFriendshipRequest) models.get(i);
                if (request.getId().equals(((HelpfulFriendshipRequest) model).getId()))
                    return new Pair<>(true, i);
            }
        }
        return new Pair<>(false, -1);
    }


    private <Tmodel> void notifyFragmentToAddItem(FriendsFragment fragment, Tmodel object, int observatorType) {

        if (fragment != null && object instanceof HelpfulUser && observatorType == USER_FRIEND_OBSERVATOR) {
            ArrayList<HelpfulUser> users = fragment.getListOfUsers();

            if (!checkItemIsInArray(users, (HelpfulUser) object).first) {
                fragment.getListOfUsers().add((HelpfulUser) object);
                fragment.getUserAdapter().notifyDataSetChanged();
                Toast.makeText(FriendsListActivity.this, "Dodano" + ((HelpfulUser) object).getId(), Toast.LENGTH_SHORT).show();
            }

        }
        if (fragment != null && object instanceof HelpfulFriendshipRequest) {
            ArrayList<HelpfulFriendshipRequest> requests;

            if (observatorType == USER_FRIENDSHIP_REQUESTS_OBSERVATOR) {
                requests = fragment.getListOfRequests();

                if (!checkItemIsInArray(requests, (HelpfulFriendshipRequest) object).first) {
                    fragment.getListOfRequests().add((HelpfulFriendshipRequest) object);
                    fragment.getReceivedRequestsAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Dostałeś zapro", Toast.LENGTH_SHORT).show();
                }

            }
            if (observatorType == USER_SENT_FRIENDSHIP_REQUESTS_OBSERVATOR) {
                requests = fragment.getListOfRequestsSentByUser();
                if ((!checkItemIsInArray(requests, (HelpfulFriendshipRequest) object).first)) {
                    fragment.getListOfRequestsSentByUser().add((HelpfulFriendshipRequest) object);
                    fragment.getSentRequestsAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Wysłałeś zapro", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    private <Tmodel> void notifyFragmentToRemoveItem(FriendsFragment fragment, Tmodel object, int observatorType) {

        if (fragment != null && object instanceof HelpfulUser && observatorType == USER_FRIEND_OBSERVATOR) {
            ArrayList<HelpfulUser> users = fragment.getListOfUsers();
            Pair<Boolean, Integer> result = checkItemIsInArray(users, (HelpfulUser) object);
            boolean exists = result.first;
            int index = result.second;
            if (exists) {
                fragment.getListOfUsers().remove(index);
                fragment.getUserAdapter().notifyItemRemoved(index);
                Toast.makeText(FriendsListActivity.this, "Usunięto" + ((HelpfulUser) object).getId(), Toast.LENGTH_SHORT).show();
            }

        } else if (fragment != null && object instanceof HelpfulFriendshipRequest) {

            ArrayList<HelpfulFriendshipRequest> requests;

            if (observatorType == USER_FRIENDSHIP_REQUESTS_OBSERVATOR) {
                requests = fragment.getListOfRequests();
                Pair<Boolean, Integer> result = checkItemIsInArray(requests, (HelpfulFriendshipRequest) object);
                boolean exists = result.first;
                int index = result.second;
                if (exists) {
                    fragment.getListOfRequests().remove(index);
                    fragment.getReceivedRequestsAdapter().notifyItemRemoved(index);
                    Toast.makeText(FriendsListActivity.this, "Usunięto" + ((HelpfulFriendshipRequest) object).getId(), Toast.LENGTH_SHORT).show();
                }
            }
            if (observatorType == USER_SENT_FRIENDSHIP_REQUESTS_OBSERVATOR) {
                requests = fragment.getListOfRequestsSentByUser();
                Pair<Boolean, Integer> result = checkItemIsInArray(requests, (HelpfulFriendshipRequest) object);
                boolean exists = result.first;
                int index = result.second;
                if (exists) {
                    fragment.getListOfRequestsSentByUser().remove(index);
                    fragment.getSentRequestsAdapter().notifyItemRemoved(index);
                    Toast.makeText(FriendsListActivity.this, "Usunięto" + ((HelpfulFriendshipRequest) object).getId(), Toast.LENGTH_SHORT).show();
                }

            }
        }


    }

    private void runObservators() {


        userManagement.observeFriends(new ObserveCollectionInterface<HelpfulUser>() {
            @Override
            public void onAdd(HelpfulUser object) {
                notifyFragmentToAddItem(userFriendsFragment, object, USER_FRIEND_OBSERVATOR);
            }

            @Override
            public void onDelete(HelpfulUser object) {
                notifyFragmentToRemoveItem(userFriendsFragment, object, USER_FRIEND_OBSERVATOR);
            }

            @Override
            public void onModify(HelpfulUser object) {
            }
        }, this);

        friendshipManagement.observeFriendsRequests(new ObserveCollectionInterface<HelpfulFriendshipRequest>() {
            @Override
            public void onAdd(HelpfulFriendshipRequest object) {
                notifyFragmentToAddItem(userInvitationsFragment, object, USER_FRIENDSHIP_REQUESTS_OBSERVATOR);
            }

            @Override
            public void onDelete(HelpfulFriendshipRequest object) {
                notifyFragmentToRemoveItem(userInvitationsFragment, object, USER_FRIENDSHIP_REQUESTS_OBSERVATOR);
            }

            @Override
            public void onModify(HelpfulFriendshipRequest object) {

            }
        }, this);

        friendshipManagement.observeSentFriendshipRequests(new ObserveCollectionInterface<HelpfulFriendshipRequest>() {
            @Override
            public void onAdd(HelpfulFriendshipRequest object) {
                notifyFragmentToAddItem(userInvitationsFragment, object, USER_SENT_FRIENDSHIP_REQUESTS_OBSERVATOR);
            }

            @Override
            public void onDelete(HelpfulFriendshipRequest object) {
                notifyFragmentToRemoveItem(userInvitationsFragment, object, USER_SENT_FRIENDSHIP_REQUESTS_OBSERVATOR);
            }

            @Override
            public void onModify(HelpfulFriendshipRequest object) {
            }
        }, this);

        reg = friendshipManagement.getListenerFriendsRequest();
        reg2 = friendshipManagement.getListenerSentResuest();
    }


    @Override
    protected void onResume() {
        runObservators();
        super.onResume();
    }

    @Override
    protected void onStop() {
        reg.remove();
        reg2.remove();
        super.onStop();
    }

    public HelpfulUser getUserObj() {
        return currentUser;
    }

}

