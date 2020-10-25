package com.example.przemek.gymdiary.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przemek.gymdiary.Adapters.PostCommentsAdapter;
import com.example.przemek.gymdiary.DbManagement.PostsManagement;
import com.example.przemek.gymdiary.Interfaces.ObserveCollectionInterface;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulPost;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulPostComment;
import com.example.przemek.gymdiary.Models.PostComment;
import com.example.przemek.gymdiary.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PostCommentsDialog extends DialogFragment {

    HelpfulPost post;
    PostCommentsAdapter postCommentsAdapter;

    @BindView(R.id.post_comment_list)
    RecyclerView listOfComments;
    @BindView(R.id.post_comment_button)
    ImageButton btnSend;
    @BindView(R.id.post_comment_text)
    EditText commentMessage;
    @BindView(R.id.post_comments_likes_tv)
    TextView likesCount;
    PostsManagement postsManagement = new PostsManagement();

    ArrayList<HelpfulPostComment> comments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_post_comments, container, false);
        ButterKnife.bind(this, view);
        post = (HelpfulPost) getArguments().getSerializable("post");
        if (post == null) {
            getDialog().dismiss();
        }
        init();
        return view;
    }

    private void init() {

        likesCount.setText(String.valueOf(post.getLikersIds().size()));

        postsManagement.getPostComments(post.getId(), comments -> {

            this.comments = comments;

            postCommentsAdapter = new PostCommentsAdapter(getActivity(), comments);
            listOfComments.setLayoutManager(new LinearLayoutManager(getActivity()));
            listOfComments.setItemAnimator(new DefaultItemAnimator());
            listOfComments.setAdapter(postCommentsAdapter);
            postCommentsAdapter.notifyDataSetChanged();
            runObserver();

        });
        btnSend.setOnClickListener(c -> {

            if (!TextUtils.isEmpty(commentMessage.getText())) {

                postsManagement.addPostComment(post.getId(), commentMessage.getText().toString(), status -> {
                    Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
                });

            }

        });
    }


    private void runObserver() {
        postsManagement.observePostsComments(new ObserveCollectionInterface<HelpfulPostComment>() {
            @Override
            public void onAdd(HelpfulPostComment object) {
                addCommentToPost(object);
            }

            @Override
            public void onDelete(HelpfulPostComment object) {

            }

            @Override
            public void onModify(HelpfulPostComment object) {

            }
        }, getActivity(), post.getId());
    }


    private void addCommentToPost(HelpfulPostComment object) {
        if (!checkItemIsInArray(object)) {
            comments.add(0, object);
            postCommentsAdapter.notifyDataSetChanged();
        }

    }

    private boolean checkItemIsInArray(HelpfulPostComment message) {

        for (int i = 0; i < comments.size(); i++) {
            HelpfulPostComment p = comments.get(i);
            if (p.getId().equals(message.getId()))
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

}
