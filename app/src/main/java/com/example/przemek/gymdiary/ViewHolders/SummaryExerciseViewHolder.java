package com.example.przemek.gymdiary.ViewHolders;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.przemek.gymdiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SummaryExerciseViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.summary_exercise_name)
    TextView exerciseName;
    @BindView(R.id.summary_exercise_linear_layout)
    LinearLayout linearLayout;



    private int linearLayoutKids = 0;
    private boolean expanded = false;
    private Context context;


    public SummaryExerciseViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.APPEARING);
//
//        btn_expand.setImageDrawable(context.getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
//
//        btn_expand.setOnClickListener(l -> {
//            changeBtnExpandState();
//        });

    }

//    private void changeBtnExpandState() {
//        if (!expanded) {
//            linearLayout.setVisibility(View.VISIBLE);
//            btn_expand.setImageDrawable(context.getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
//        } else {
//            linearLayout.setVisibility(View.GONE);
//            btn_expand.setImageDrawable(context.getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
//        }
//        expanded = !expanded;
//    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName.setText(exerciseName);
    }

    public String getExerciseName() {
        return exerciseName.getText().toString();
    }

    public void addSerieToLinearLayout(View serieLayout) {

        linearLayoutKids++;
        serieLayout.setId(linearLayoutKids);
        linearLayout.addView(serieLayout, linearLayoutKids - 1);
    }
//
//    public void setTotalSeriesCount(String count) {
//        tvTotalSeriesCount.setText("Liczba serii: " + count);
//    }

}
