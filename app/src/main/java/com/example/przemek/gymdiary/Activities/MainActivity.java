package com.example.przemek.gymdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.przemek.gymdiary.Adapters.PostsAdapter;
import com.example.przemek.gymdiary.AppBarStateChangeListener;
import com.example.przemek.gymdiary.DbManagement.PostsManagement;
import com.example.przemek.gymdiary.DbManagement.UserManagement;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.ObserveCollectionInterface;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulMessage;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulPost;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulUser;
import com.example.przemek.gymdiary.Models.User;
import com.example.przemek.gymdiary.R;
import com.google.firebase.auth.FirebaseUser;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.progress_bar_main)
    ProgressBar pb1;
    @BindView(R.id.firstlogin_circleimageview)
    CircleImageView img_profile;
    @BindView(R.id.photo_on_toolbar)
    CircleImageView profile_image_toolbar;
    @BindView(R.id.btn_add_post)
    MaterialFancyButton addPostButton;
    @BindView(R.id.main_tv_name)
    TextView tv_name;
    @BindView(R.id.main_tv_age)
    TextView tv_age;
    @BindView(R.id.main_tv_city)
    TextView tv_city;
    TextView tv_email_drawer;
    @BindView(R.id.main_tv_gender)
    TextView tv_gender;
    //    @BindView(R.id.main_pb)
//    ProgressBar main_progress_bar;
    TextView tv_name_drawer;
    @BindView(R.id.main_profile_layout)
    View profile_view;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.post_list)
    RecyclerView postsList;

    @BindView(R.id.main_root2)
    ConstraintLayout cl;
    @BindView(R.id.progress_bar_main2)
    ProgressBar pb2;


    @BindView(R.id.tv_no_posts)
    TextView tvNoPosts;

    CircleImageView img_profile_drawer;

    private UserManagement userManagement = new UserManagement();

    FirebaseUser currentLoggedUser;
    HelpfulUser userObj;
    PostsAdapter postsAdapter;
    PostsManagement postsManagement = new PostsManagement();

    private final int EDIT_PROFILE_RC = 110;

    ArrayList<HelpfulPost> posts = new ArrayList<>();


    private final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);
        currentLoggedUser = UserManagement.checkUserLoggedIn();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);

        userObj = (HelpfulUser) getIntent().getSerializableExtra("user");

        if (userObj != null) {
            updateUi(userObj);
        } else {
            getUser();
        }
        ConstraintLayout.LayoutParams existsParam = (ConstraintLayout.LayoutParams) postsList.getLayoutParams();
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    profile_image_toolbar.setVisibility(View.VISIBLE);
                    addPostButton.setVisibility(View.GONE);
                    params.setMargins(0, 15, 0, 0);
                    postsList.setLayoutParams(params);
                }
                if (state == State.IDLE || state == State.EXPANDED) {
                    profile_image_toolbar.setVisibility(View.GONE);
                    if (state == State.EXPANDED) {
                        postsList.setLayoutParams(existsParam);
                        addPostButton.setVisibility(View.VISIBLE);
                        if (userObj != null)
                            toolbar.setTitle(userObj.getNick());
                    }
                }
            }
        });

        addPostButton.setOnClickListener(c -> {
            Intent i = new Intent(MainActivity.this, CreatePostActivity.class);
            i.putExtra("user", userObj);
            startActivity(i);
        });

    }

    private void getUser() {

        userManagement.getUser(currentLoggedUser.getUid(), user -> {
            if (user != null) {
                userObj = user;
                updateUi(user);
            } else {
                Log.d(TAG, "User nie ma danych");
                Intent i = new Intent(MainActivity.this, FirstLoginActivity.class);
                startActivity(i);
                finish();
                Log.d(TAG, "Proszę o wypełnienie danych");
            }
        });
    }

    private void updateUi(User user) {

        //Ustawianie navigationDravera
        toolbar.setTitle(user.getNick());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //TODO do butterknife
        View hView = navigationView.getHeaderView(0);
        tv_email_drawer = hView.findViewById(R.id.drawer_tv_email);
        tv_name_drawer = hView.findViewById(R.id.drawer_tv_name);
        img_profile_drawer = hView.findViewById(R.id.drawer_img_profile);


        //Ustawienia głównego widoku
        try {
            tv_age.setText(String.valueOf(Helper.getAge(user.getBirthday())));
            tv_city.setText(user.getCity());
            tv_name.setText(user.getName() + " " + user.getSurname());
            switch (user.getGender()) {
                case 0:
                    tv_gender.setText("Mężczyzna");
                    break;
                case 1:
                    tv_gender.setText("Kobieta");
                    break;
                case 2:
                    tv_gender.setText("Inna");
            }
            tv_email_drawer.setText(currentLoggedUser.getEmail());
            tv_name_drawer.setText(user.getName() + user.getSurname());
        } catch (Exception e) {
            Log.d("Ac main", e.getMessage());
            finish();
        }
        Glide.with(MainActivity.this).setDefaultRequestOptions(Helper.getPlaceholder()).load(user.getProfilePhotoUrl()).into(img_profile);

        profile_view.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.VISIBLE);
        pb1.setVisibility(View.GONE);
        getPosts();


    }

    private void getPosts() {
        postsManagement.getUserPosts(userObj.getId(), posts -> {
            postsList.setVisibility(View.VISIBLE);
            addPostButton.setVisibility(View.VISIBLE);

            pb2.setVisibility(View.GONE);
            if (posts.isEmpty()) {
                tvNoPosts.setVisibility(View.VISIBLE);
            }
            this.posts = posts;
            postsAdapter = new PostsAdapter(posts, postsList, this);
            postsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            postsList.setLayoutManager(new LinearLayoutManager(this));
            postsList.setItemAnimator(new DefaultItemAnimator());
            postsList.setAdapter(postsAdapter);
            postsAdapter.notifyDataSetChanged();

            runObservators();
        });
    }

    private void runObservators() {
        postsManagement.observePosts(new ObserveCollectionInterface<HelpfulPost>() {
            @Override
            public void onAdd(HelpfulPost object) {
                addMessageToChat(object);
            }

            @Override
            public void onDelete(HelpfulPost object) {
                removePost(object);
            }

            @Override
            public void onModify(HelpfulPost object) {
                editPost(object);
            }
        }, this, userObj.getId());
    }

    private void removePost(HelpfulPost object) {
        //TODO można łatwiej
        for (int i = 0; i < posts.size(); i++) {
            HelpfulPost p = posts.get(i);
            if (p.getId().equals(object.getId())) {
                posts.remove(i);
                postsAdapter.notifyDataSetChanged();
                postsAdapter.notifyItemRemoved(i);
            }
        }
    }

    private void editPost(HelpfulPost object) {
        for (int i = 0; i < posts.size(); i++) {
            HelpfulPost p = posts.get(i);
            if (p.getId().equals(object.getId())) {
                posts.set(i, object);
                postsAdapter.notifyDataSetChanged();
                postsAdapter.notifyItemInserted(i);
            }
        }
    }

    private void addMessageToChat(HelpfulPost object) {
        if (tvNoPosts.getVisibility() == View.VISIBLE)
            tvNoPosts.setVisibility(View.GONE);

        if (!checkItemIsInArray(object)) {
            posts.add(0, object);
            postsAdapter.notifyDataSetChanged();
        }

    }

    private boolean checkItemIsInArray(HelpfulPost message) {

        for (int i = 0; i < posts.size(); i++) {
            HelpfulPost p = posts.get(i);
            if (p.getId().equals(message.getId()))
                return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_training_plans) {

            Intent i = new Intent(MainActivity.this, TrainingsPlansListActivity.class);
            startActivity(i);


        } else if (id == R.id.nav_my_exercises) {

            Intent i = new Intent(MainActivity.this, MyExercisesActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_logout) {

            logout();

        } else if (id == R.id.nav_my_friends) {
            Intent i = new Intent(MainActivity.this, FriendsListActivity.class);
            i.putExtra("user", userObj);
            startActivity(i);
        } else if (id == R.id.nav_society) {
            Intent i = new Intent(MainActivity.this, SocietyActivity.class);
            startActivity(i);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_RC) {
            if (resultCode == EditProfileActivity.RESULT_OK) {
                getUser();
            }
        }

    }

    private void logout() {

        UserManagement.logout();
        Helper.sendToLoginActivity(this);

    }

    public boolean click(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                intent.putExtra("user", userObj);
                startActivityForResult(intent, EDIT_PROFILE_RC);
                break;
        }
        return true;
    }


    @Override
    protected void onResume() {

        if (postsAdapter != null)
            runObservators();

        currentLoggedUser = UserManagement.checkUserLoggedIn();
        if (currentLoggedUser == null)
            Helper.sendToLoginActivity(this);

        super.onResume();
    }
}
