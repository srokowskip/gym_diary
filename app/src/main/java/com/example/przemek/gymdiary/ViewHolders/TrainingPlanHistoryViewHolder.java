package com.example.przemek.gymdiary.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.przemek.gymdiary.Activities.LiveTrainingSummaryActivity;
import com.example.przemek.gymdiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingPlanHistoryViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.tv_plan_history_date)
    TextView tv_date;
    @BindView(R.id.btn_plan_history_proceed)
    ImageView btn_proceed;
    Context mContext;
    String historyId;

    public TrainingPlanHistoryViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = context;
        btn_proceed.setOnClickListener(c -> {
            proceedToHistory();
        });
    }

    private void proceedToHistory() {

        Intent i = new Intent(mContext, LiveTrainingSummaryActivity.class);
        i.putExtra("historyId", historyId);
        mContext.startActivity(i);

    }

    public void setDate(String date) {
        tv_date.setText(date);
    }

    public void setHistoryId(String id) {
        this.historyId = id;
    }

}
