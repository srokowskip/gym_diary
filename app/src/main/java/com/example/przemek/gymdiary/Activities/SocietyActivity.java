package com.example.przemek.gymdiary.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.AbsListView;
import android.widget.Toast;

import com.example.przemek.gymdiary.Adapters.PostsAdapter;
import com.example.przemek.gymdiary.DbManagement.PostsManagement;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulPost;
import com.example.przemek.gymdiary.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SocietyActivity extends AppCompatActivity {

    @BindView(R.id.society_posts)
    RecyclerView societyPosts;
    private PostsAdapter postsAdapter;
    ArrayList<HelpfulPost> posts = new ArrayList<>();
    @BindView(R.id.society_toolbar)
    Toolbar toolbar;

    boolean isScrolling;
    boolean isLastItemReached;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_society);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getFirstFivePosts();

    }

    private void getFirstFivePosts() {
        PostsManagement postsManagement = new PostsManagement();
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        postsManagement.getInitialSocietyPosts(data -> {
            posts = data;
            postsAdapter = new PostsAdapter(posts, societyPosts, this);
            societyPosts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            societyPosts.setLayoutManager(mLinearLayoutManager);
            societyPosts.setItemAnimator(new DefaultItemAnimator());
            societyPosts.setAdapter(postsAdapter);
            postsAdapter.notifyDataSetChanged();
        });
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = mLinearLayoutManager.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();

                if (isScrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItemReached) {
                    isScrolling = false;
                    Toast.makeText(SocietyActivity.this, "Dół", Toast.LENGTH_SHORT).show();
                    postsManagement.getNextSocietyPosts(data -> {
                        posts.addAll(data);
                        postsAdapter.notifyDataSetChanged();
                        if (data.size() < 5) {
                            isLastItemReached = true;
                        }
                    });
                }
            }
        };
        societyPosts.addOnScrollListener(onScrollListener);
    }
}
