package com.example.przemek.gymdiary.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.przemek.gymdiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.image_message_profile)
    CircleImageView imgProfile;
    @BindView(R.id.text_message_name)
    TextView textName;
    @BindView(R.id.text_message_body)
    TextView textBody;
    @BindView(R.id.text_message_time)
    TextView textTime;


    public ReceivedMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setImgProfile(CircleImageView imgProfile) {
        this.imgProfile = imgProfile;
    }

    public CircleImageView getImgProfile() {
        return imgProfile;
    }

    public TextView getTextBody() {
        return textBody;
    }

    public TextView getTextName() {
        return textName;
    }

    public TextView getTextTime() {
        return textTime;
    }
}
