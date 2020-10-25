package com.example.przemek.gymdiary.Fragments;

import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.przemek.gymdiary.Activities.LiveTrainingActivity;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Helper;
import com.example.przemek.gymdiary.Interfaces.Validateable;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.Set;
import com.example.przemek.gymdiary.R;
import com.example.przemek.gymdiary.ViewModels.LiveTrainingViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveTrainingExerciseFragment extends Fragment implements Validateable {

    int position;
    LiveTrainingViewModel liveTrainingViewModel;

    int initialSeriesCount;
    int seriesCount;

    @BindView(R.id.ll_live_training_exercise_series)
    LinearLayout linearLayout;
    @BindView(R.id.tv_name)
    TextView tv_exercise_name;
    @BindView(R.id.sv_live_training_exercise_series)
    ScrollView scrollView;
    @BindView(R.id.start_end_position_photos)
    ImageView exercisePhoto;

    List<Set> listOfExerciseSets = new ArrayList<>();

    LiveTrainingActivity parentActivity;
    List<Integer> listOfRepeats;

    HelpfulExercise currentExercise;

    public static android.support.v4.app.Fragment newInstance() {
        return new LiveTrainingExerciseFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_live_training_default_fragment, container, false);

        parentActivity = (LiveTrainingActivity) getActivity();
        position = getArguments().getInt("position");

        ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {

        liveTrainingViewModel = ViewModelProviders.of(parentActivity).get(LiveTrainingViewModel.class);
        currentExercise = liveTrainingViewModel.getExercise(position);

        listOfRepeats = currentExercise.getListOfRepeats();

        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.DISAPPEARING);
        tv_exercise_name.setText(currentExercise.getName());
        initialSeriesCount = listOfRepeats.size();

        Glide.with(this).setDefaultRequestOptions(Helper.getPlaceholder()).load(currentExercise.getPhotoUri()).into(exercisePhoto);

        for (int i = 0; i < initialSeriesCount; i++) {
            addSeries(listOfRepeats.get(i));
        }
    }


    private void initSeriesLayout(View rowView, int repeatsCount) {

        TextView tv_currentSerie = rowView.findViewById(R.id.tv_live_training_current_serie);
        TextView tv_serieCount = rowView.findViewById(R.id.live_training_series_count);
        TextView tv_repeat_count = rowView.findViewById(R.id.tv_live_training_repeat_count);
        ImageView btn_save = rowView.findViewById(R.id.btn_exercise_serie_save);


        tv_currentSerie.setText(String.valueOf(seriesCount));
        tv_serieCount.setText(String.valueOf(initialSeriesCount));
        tv_repeat_count.setText(String.valueOf(repeatsCount));


        handleButtonClick(btn_save, rowView);


    }

    private void handleButtonClick(ImageView btn_save, View rowView) {

        btn_save.setOnClickListener(l -> {

            int position = rowView.getId();
            ProgressBar pb_loading = rowView.findViewById(R.id.live_training_save_progress_bar);
            EditText et_repeats = rowView.findViewById(R.id.et_live_training_input_repeat);
            EditText et_weight = rowView.findViewById(R.id.et_live_training_weight);

            Set set = checkSerie(et_repeats, et_weight);
            if (set != null) {

                listOfExerciseSets.add(set);

                btn_save.setVisibility(View.INVISIBLE);
                pb_loading.setVisibility(View.VISIBLE);

                parentActivity.saveSession(position, set, currentExercise.getId(), statusListener -> {

                    if (statusListener == DbStatus.Success) {
                        pb_loading.setVisibility(View.INVISIBLE);
                        removeSerie(rowView);
                    } else {
                        pb_loading.setVisibility(View.INVISIBLE);
                        btn_save.setVisibility(View.VISIBLE);
                    }

                });

            } else {
                Toast.makeText(parentActivity, "Wystąpił błąd", Toast.LENGTH_SHORT).show();
            }

        });

    }


    @Nullable
    private Set checkSerie(EditText et_repeats, EditText et_weight) {


        if (!TextUtils.isEmpty(et_repeats.getText().toString()) && !TextUtils.isEmpty(et_weight.getText().toString())) {
            if (!(Integer.valueOf(et_repeats.getText().toString()) < 0) && !(Double.valueOf(et_weight.getText().toString()) < 0)) {
                if (!(et_repeats.getText().toString().startsWith("0")) && !(et_weight.getText().toString().startsWith("0")))
                    return new Set(Integer.valueOf(et_repeats.getText().toString()), Double.valueOf(et_weight.getText().toString()));
            }
            if ((Integer.valueOf(et_repeats.getText().toString()) < 0) || (et_repeats.getText().toString().startsWith("0")))
                et_repeats.setError("Wartość musi być większa od 0");
            if (Double.valueOf(et_weight.getText().toString()) < 0 || (et_weight.getText().toString().startsWith("0")))
                et_weight.setError("Wartość musi być większa od 0");

        } else {
            et_repeats.setError("Podaj wartość");
            et_weight.setError("Podaj wartość");
        }

        return null;
    }


    private void addSeries(int repeatsCount) {

        seriesCount++;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.item_live_training_exercise_serie, null);
        rowView.setId(seriesCount);
        initSeriesLayout(rowView, repeatsCount);

        linearLayout.addView(rowView, seriesCount - 1);

    }


    public HelpfulExercise getExerciseFromFragment() {
        return currentExercise;
    }

    public String getExerciseId() {
        return liveTrainingViewModel.getExercise(position).getId();
    }

    public void setSavedData(List<Set> data) {

        for (int i = 1; i <= data.size(); i++) {
            Set set = data.get(i - 1);

            listOfExerciseSets.add(set);

            View setLayout = linearLayout.findViewById(i);
            removeSerie(setLayout);
            //TODO pomyśleć nad tym

            EditText et_repeats = setLayout.findViewById(R.id.et_live_training_input_repeat);
            EditText et_weight = setLayout.findViewById(R.id.et_live_training_weight);

            et_repeats.setText(String.valueOf(set.getRepeats()));
            et_weight.setText(String.valueOf(set.getWeight()));

            removeSerie(setLayout);
        }

    }


    private void removeSerie(View serieLayout) {

        linearLayout.removeView(serieLayout);
        if (linearLayout.getChildCount() == 0) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View emptyView = inflater.inflate(R.layout.activity_live_training_empty_series, (ViewGroup) getView(), false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            emptyView.setLayoutParams(params);
            linearLayout.addView(emptyView);

        }

    }

    @Override
    public boolean isValid() {
        //Sprawdzać, czy są jakieś serie do wykonania
        return listOfExerciseSets.size() == initialSeriesCount;
    }

    public ArrayMap<String, Object> getExerciseDataToSummary() {

        ArrayMap<String, Object> exerciseWithSeries = new ArrayMap<>();
        HelpfulExercise exercise = liveTrainingViewModel.getExercise(position);

        exerciseWithSeries.put("exercise", exercise);
        exerciseWithSeries.put("listOfSets", listOfExerciseSets);

        return exerciseWithSeries;

    }

}
