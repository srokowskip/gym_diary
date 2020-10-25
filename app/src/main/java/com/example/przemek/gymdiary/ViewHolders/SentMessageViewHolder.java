package com.example.przemek.gymdiary.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.przemek.gymdiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SentMessageViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_message_body)
    TextView tvBody;
    @BindView(R.id.text_message_time)
    TextView tvTime;

    public SentMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getTvBody() {
        return tvBody;
    }

    public TextView getTvTime() {
        return tvTime;
    }
}
