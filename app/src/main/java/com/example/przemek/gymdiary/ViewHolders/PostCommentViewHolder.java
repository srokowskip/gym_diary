package com.example.przemek.gymdiary.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.przemek.gymdiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class PostCommentViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.comment_message)
    TextView tvMessage;
    @BindView(R.id.comment_user_name)
    TextView userName;
    @BindView(R.id.comment_user_photo)
    CircleImageView profilePhoto;

    public PostCommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public void setTvMessage(String tvMessage) {
        this.tvMessage.setText(tvMessage);
    }

    public TextView getUserName() {
        return userName;
    }

    public CircleImageView getProfilePhoto() {
        return profilePhoto;
    }

    public TextView getTvMessage() {
        return tvMessage;
    }
}
