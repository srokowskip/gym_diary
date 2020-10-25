package com.example.przemek.gymdiary.Dialogs;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatImageButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.R;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExercisePropertiesDialogFragment extends DialogFragment {

    public interface OnSetExercisePreferencesListener {
        void sendInput(HelpfulExercise exercise);
    }

    @BindView(R.id.exercise_properties_dialog_series_count)
    TextView tv_series_count;
    @BindView(R.id.toolbar_save_icon)
    ImageView toolbar_ok;
    @BindView(R.id.toolbar_close_icon)
    ImageView toolbar_close;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.exercise_properties_dialog_btn_plus)
    MaterialFancyButton btn_plus;
    @BindView(R.id.exercise_properties_dialog_btn_minus)
    MaterialFancyButton btn_minus;
    @BindView(R.id.exercise_properties_dialog_linearlayout)
    LinearLayout linear;
    int series_count = 0;
    HelpfulExercise exercise;
    private static final String TAG = "ExercisePropertiesDialog";
    public OnSetExercisePreferencesListener mOnInputListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_exercises_properties, container, false);
        ButterKnife.bind(this, view);
        exercise = (HelpfulExercise) getArguments().getSerializable("exercise");

        if (exercise == null) {
            mOnInputListener.sendInput(null);
            getDialog().dismiss();
        }

        setToolbar();

        btn_plus.setOnClickListener(c -> plusSeries());
        btn_minus.setOnClickListener(c -> minusSeries());

        return view;
    }

    private void setToolbar() {

        toolbar_close.setOnClickListener(l -> getDialog().dismiss());
        toolbar_ok.setOnClickListener(c -> setExerciseProperties());

    }

    private void setExerciseProperties() {

        if (isValid()) {

            List<Integer> list = new ArrayList<>();
            for (int i = 1; i <= series_count; i++) {
                View rowView = linear.findViewById(i);
                EditText et = rowView.findViewById(R.id.item_exercise_set_et_reps);
                String repsCountstr = et.getText().toString();
                int repsCount = Integer.parseInt(repsCountstr);
                list.add(repsCount);
            }
            exercise.setListOfRepeats(list);
            mOnInputListener.sendInput(exercise);
            getDialog().dismiss();
        }
    }

    private void minusSeries() {
        if (series_count > 0) {
            View rowView = linear.findViewById(series_count);
            linear.removeView(rowView);
            series_count--;
            tv_series_count.setText(String.valueOf(series_count));
        }

    }

    private void plusSeries() {

        series_count++;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.item_exercise_series, null);
        rowView.setId(series_count);
        TextView tv = rowView.findViewById(R.id.item_exercise_sets_tv_set);
        tv.setText(String.format("%s %s", tv.getText(), String.valueOf(rowView.getId())));
        linear.addView(rowView, series_count - 1);
        tv_series_count.setText(String.valueOf(series_count));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (OnSetExercisePreferencesListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    public boolean isValid() {

        boolean isValid = true;
        for (int i = 0; i <= linear.getChildCount() - 1; i++) {

            View serieView = linear.getChildAt(i);
            EditText seriesCount = serieView.findViewById(R.id.item_exercise_set_et_reps);

            if (TextUtils.isEmpty(seriesCount.getText())) {
                seriesCount.setError("Pole nie może być puste");
                isValid = false;
            } else if ((Integer.parseInt(seriesCount.getText().toString()) < 0)) {
                //TODO TEXTINPUT LAYOUT
                seriesCount.setError("Wprowadź dane poprawnie");
                isValid = false;
            }
        }
        return isValid;
    }


}
