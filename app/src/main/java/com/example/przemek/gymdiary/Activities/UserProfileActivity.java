package com.example.przemek.gymdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.przemek.gymdiary.Adapters.PostsAdapter;
import com.example.przemek.gymdiary.AppBarStateChangeListener;
import com.example.przemek.gymdiary.DbManagement.ChatManagement;
import com.example.przemek.gymdiary.DbManagement.FriendshipManagement;
import com.example.przemek.gymdiary.DbManagement.PostsManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.R;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.post_list)
    RecyclerView postsList;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.photo_on_toolbar)
    CircleImageView profile_image_toolbar;
    @BindView(R.id.progress_bar_main)
    ProgressBar pb1;
    @BindView(R.id.progress_bar_main2)
    ProgressBar pb2;

    @BindView(R.id.firstlogin_circleimageview)
    CircleImageView img_profile;
    @BindView(R.id.main_tv_name)
    TextView tv_name;
    @BindView(R.id.main_tv_age)
    TextView tv_age;
    @BindView(R.id.main_tv_city)
    TextView tv_city;
    @BindView(R.id.main_tv_gender)
    TextView tv_gender;
    @BindView(R.id.main_profile_layout)
    View profile_view;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_add_post)
    MaterialFancyButton addPostButton;
    @BindView(R.id.user_profile_actions)
    LinearLayout userActions;
    @BindView(R.id.user_history)
    ImageView userHistory;
    @BindView(R.id.user_message)
    ImageView userMessage;
    @BindView(R.id.user_profile_actions2)
    LinearLayout userActions2;
    @BindView(R.id.user_add_friend)
    ImageView addFriend;

    UserManagement userManagement = new UserManagement();
    PostsAdapter postsAdapter;

    private String userId;
    private HelpfulUser userData;
    private HelpfulUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userId = getIntent().getStringExtra("userId");
        userManagement.getUser(userId, user -> {
            userData = user;
            if (userData != null)
                userManagement.getCurrentUser(current -> {
                    currentUser = current;
                    init();
                });
            else
                Toast.makeText(this, "Wystąpił nieokreślony błąd", Toast.LENGTH_SHORT).show();
        });
    }

    private void init() {
        setUserData();
        Glide.with(UserProfileActivity.this).setDefaultRequestOptions(Helper.getPlaceholder()).load(userData.getProfilePhotoUrl()).into(img_profile);

        addPostButton.setVisibility(View.GONE);
        userManagement.checkUserIsMyFriend(userId, isMyFriend ->
        {
            if (isMyFriend) {
                userHistory.setVisibility(View.VISIBLE);
                userMessage.setVisibility(View.VISIBLE);
                addFriend.setVisibility(View.GONE);
            } else {
                userHistory.setVisibility(View.GONE);
                userMessage.setVisibility(View.GONE);
                addFriend.setVisibility(View.VISIBLE);
            }
            userActions.setVisibility(View.VISIBLE);
            userActions2.setVisibility(View.VISIBLE);
        });

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        postsList.setLayoutParams(params);

        PostsManagement postsManagement = new PostsManagement();
        postsManagement.getUserPosts(userId, posts ->

        {

            postsAdapter = new PostsAdapter(posts, postsList, this);
            postsList.setLayoutManager(new LinearLayoutManager(this));
            postsList.setItemAnimator(new DefaultItemAnimator());
            postsList.setAdapter(postsAdapter);
            postsAdapter.notifyDataSetChanged();

            postsList.setVisibility(View.VISIBLE);

            pb2.setVisibility(View.GONE);

        });

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Toast.makeText(UserProfileActivity.this, state.name(), Toast.LENGTH_SHORT).show();
                if (state == AppBarStateChangeListener.State.COLLAPSED) {
                    profile_image_toolbar.setVisibility(View.VISIBLE);
                }
                if (state == State.IDLE || state == State.EXPANDED) {
                    getSupportActionBar().setTitle(null);
                    profile_image_toolbar.setVisibility(View.GONE);
                }

            }
        });

        userHistory.setOnClickListener(c -> {
            getUsersTrainingHistory();
        });

        addFriend.setOnClickListener(c -> {
            addToFriends();
        });
        userMessage.setOnClickListener(c -> {
            startChat();
        });
    }

    private void startChat() {
        ChatManagement chatManagement = new ChatManagement();
        ArrayList<String> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(chatManagement.getCurrentUserId());
        chatManagement.startOrGoChat(userId, (status, id) -> {
            if (status == DbStatus.Success) {
                Toast.makeText(this, "Utworzono czat" + id, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(UserProfileActivity.this, ChatActivity.class);
                i.putExtra("chatId", id);
                i.putExtra("user", currentUser);
                Toast.makeText(this, currentUser.getId(), Toast.LENGTH_SHORT).show();
                startActivity(i);
                //TODO FINISH?
            } else
                Toast.makeText(this, "Nie utworzono", Toast.LENGTH_SHORT).show();
        });
    }

    private void addToFriends() {
        FriendshipManagement friendshipManagement = new FriendshipManagement();
        userManagement.getCurrentUser(user -> {
            friendshipManagement.addFriendshipRequest(userId, user.getNick(), user, status -> {
                if (status == DbStatus.Success) {
                    Toast.makeText(this, "Zaproszenie zostało wysłane", Toast.LENGTH_SHORT).show();
                    addFriend.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, "Wystąpił błąd, spróbuj później", Toast.LENGTH_SHORT).show();

                }
            });

        });
    }

    private void getUsersTrainingHistory() {
        Intent i = new Intent(UserProfileActivity.this, FriendTrainingHistoryActivity.class);
        i.putExtra("userId", userId);
        startActivity(i);
    }

    private void setUserData() {
        toolbar.setTitle(userData.getNick());

        try {
            tv_age.setText(String.valueOf(Helper.getAge(userData.getBirthday())));
            tv_city.setText(userData.getCity());
            tv_name.setText(userData.getName() + " " + userData.getSurname());
            switch (userData.getGender()) {
                case 0:
                    tv_gender.setText("Mężczyzna");
                    break;
                case 1:
                    tv_gender.setText("Kobieta");
                    break;
                case 2:
                    tv_gender.setText("Inna");
            }
        } catch (Exception e) {
            Log.d("Ac main", e.getMessage());
            finish();
        }
        appBarLayout.setVisibility(View.VISIBLE);
        pb1.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

