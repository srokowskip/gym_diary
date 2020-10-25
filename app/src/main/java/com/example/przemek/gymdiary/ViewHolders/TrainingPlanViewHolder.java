package com.example.przemek.gymdiary.ViewHolders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.przemek.gymdiary.Activities.LiveTrainingActivity;
import com.example.przemek.gymdiary.Activities.TrainingPlanActivity;
import com.example.przemek.gymdiary.Activities.TrainingPlanHistoryListActivity;
import com.example.przemek.gymdiary.DbManagement.LiveTrainingManagement;
import com.example.przemek.gymdiary.DbManagement.TrainingPlansManagement;
import com.example.przemek.gymdiary.Enums.DbStatus;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulExercise;
import com.example.przemek.gymdiary.Models.HelpfulModels.HelpfulTrainingPlan;
import com.example.przemek.gymdiary.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingPlanViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.training_plan_card_title)
    TextView tv_title;
    @BindView(R.id.training_plan_card_last_used)
    TextView tv_last_used;

    @BindView(R.id.training_plan_card_play)
    Button btn_play;
    @BindView(R.id.training_plan_card_history)
    Button btn_history;
    @BindView(R.id.training_plan_card_edit)
    Button btn_edit;
    @BindView(R.id.training_plan_card_delete)
    Button btn_delete;
    @BindView(R.id.training_plan_card_description_text)
    TextView planDescription;
    @BindView(R.id.training_plan_card_description_row)
    RelativeLayout descripitionRow;


    private Context mContext;


    private HelpfulTrainingPlan trainingPlan;
    private LiveTrainingManagement liveTrainingManagement = new LiveTrainingManagement();


    public TrainingPlanViewHolder(View itemView, Context mContext) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mContext = mContext;
        planDescription.setMovementMethod(new ScrollingMovementMethod());
    }


    public void initData() {


        ExercisesListAdapter exercisesListAdapter = new ExercisesListAdapter(mContext, R.layout.item_exercise_listview, trainingPlan.getExerciseList());

        planDescription.setText(trainingPlan.getDescription());

        btn_play.setOnClickListener(c -> {
            prepareStartSession();
        });
        btn_delete.setOnClickListener(c -> {
            showDeletingConfirmationDialog();
        });
        btn_edit.setOnClickListener(c -> {
            takeToTrainingPlanEdit();
        });
        btn_history.setOnClickListener(c -> {
            takeToTrainingPlanHistory();
        });

        descripitionRow.setOnClickListener(l -> {
            showOrHideDescription();
        });

    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setLastUsed(String lastUsed) {
        tv_last_used.setText("Ostatnio używany: " + lastUsed);
    }

    public void setTrainingPlan(HelpfulTrainingPlan trainingPlan) {
        this.trainingPlan = trainingPlan;
        initData();
    }


    private void showOrHideDescription() {

        if (planDescription.getVisibility() == View.GONE)
            planDescription.setVisibility(View.VISIBLE);
        else
            planDescription.setVisibility(View.GONE);

    }

    private void takeToLiveTrainingPlans(Bundle dataToPass) {

        Intent liveTraining = new Intent(mContext, LiveTrainingActivity.class);
        liveTraining.putExtras(dataToPass);
        mContext.startActivity(liveTraining);

    }

    private void takeToTrainingPlanEdit() {
        Intent i = new Intent(mContext, TrainingPlanActivity.class);
        i.putExtra("title", trainingPlan.getTitle());
        i.putExtra("id", trainingPlan.getId());
        i.putExtra("editing", "true");
        mContext.startActivity(i);
    }

    private void takeToTrainingPlanHistory() {

        Intent i = new Intent(mContext, TrainingPlanHistoryListActivity.class);
        i.putExtra("planId", trainingPlan.getId());
        i.putExtra("planTitle", trainingPlan.getTitle());
        mContext.startActivity(i);

    }

    private void prepareStartSession() {

        liveTrainingManagement.addDocumentToDb(trainingPlan, (status, documentId) -> {
            if (status == DbStatus.Success) {
                Bundle data = new Bundle();
                data.putString("sessionId", documentId);
                takeToLiveTrainingPlans(data);

            } else if (status == DbStatus.UserAlreadyHaveSession) {
                showSessionDialog(documentId);
            } else {
                Toast.makeText(mContext, "Wystąpił nieoczekiwany błąd", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showSessionDialog(String sessionId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.DefaultDialogTheme);
        alertDialog.setTitle("Masz aktywną sesję treningową !");
        alertDialog.setMessage("Jednocześnie możesz posiadać tylko jedną sesję treningową.");
        alertDialog.setPositiveButton("Usuń istniejącą sesję", (dialogInterface, pos) -> {

            liveTrainingManagement.removeLiveTraining(sessionId, status -> {
                if (status == DbStatus.Success)
                    Toast.makeText(mContext, "Usunięto sesję, można rozpocząć kolejną ", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, "Wystąpił błąd poczas usuwania sesji, spróbuj ponownie", Toast.LENGTH_SHORT).show();
            });

        });
        alertDialog.setNegativeButton("Przejdź do istniejącej sesji", ((dialogInterface, i) -> {
            Bundle b = new Bundle();
            b.putString("sessionId", sessionId);
            b.putBoolean("resumed", true);
            takeToLiveTrainingPlans(b);
        }));

        alertDialog.setNeutralButton("Anuluj", ((dialogInterface, i) -> {
            dialogInterface.cancel();

        }));
        alertDialog.show();
    }

    private void showDeletingConfirmationDialog() {

        AlertDialog.Builder deletionDialog = new AlertDialog.Builder(mContext, R.style.DefaultDialogTheme);
        deletionDialog.setTitle(R.string.deleting_training_plan);
        deletionDialog.setMessage(R.string.confirm_deleting_training_plan);
        deletionDialog.setCancelable(false);
        deletionDialog.setNeutralButton(R.string.delete_plan, (dialogInterface, i) -> {
            TrainingPlansManagement trainingPlansManagement = new TrainingPlansManagement();
            trainingPlansManagement.removeDocumentFromDb(trainingPlan.getId(), status -> {
                if (status != DbStatus.Success) {
                    Toast.makeText(mContext, "Nie można usunąć planu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Poprawnie usunięto plan", Toast.LENGTH_SHORT).show();

                }
            });
        });
        deletionDialog.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
        deletionDialog.show();

    }

    private class ExercisesListAdapter extends ArrayAdapter<HelpfulExercise> {

        List<HelpfulExercise> listOfData;
        Context context;

        public ExercisesListAdapter(@NonNull Context context, int resource, List<HelpfulExercise> exercises) {
            super(context, resource, exercises);
            listOfData = exercises;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            HelpfulExercise e = listOfData.get(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.item_exercise_listview, parent, false);
            TextView exerciseName = rowView.findViewById(R.id.tv_exercise_name);

            exerciseName.setText(e.getName());

            return rowView;
        }
    }


}
