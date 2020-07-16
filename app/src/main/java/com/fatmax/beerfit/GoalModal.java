package com.fatmax.beerfit;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fatmax.beerfit.utilities.Elements;
import com.fatmax.beerfit.utilities.Exercise;
import com.fatmax.beerfit.utilities.Goal;
import com.fatmax.beerfit.utilities.Measurement;
import com.fatmax.beerfit.views.Modal;

import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class GoalModal {

    private final Context context;
    private final SQLiteDatabase sqLiteDatabase;
    private AlertDialog dialog;
    private Modal modal;

    public GoalModal(Context context, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.sqLiteDatabase = sqLiteDatabase;
        this.modal = new Modal(context, sqLiteDatabase);
    }

    public void launch(Goal goal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(R.string.to_earn_a_beer);
        View goalView = inflater.inflate(R.layout.modal_goal, null);
        modal.createSpinner(goalView, EXERCISES_TABLE, "current", R.id.goalExercise, false);
        modal.createSpinner(goalView, MEASUREMENTS_TABLE, "unit", R.id.goalMeasurement, true);
        // customization for edit or add
        if (goal.getId() != -1) {
            ((Spinner) goalView.findViewById(R.id.goalExercise)).setSelection(goal.getExercise().getId());
            ((TextView) goalView.findViewById(R.id.goalAmount)).setText(String.valueOf(goal.getAmount()));
            ((Spinner) goalView.findViewById(R.id.goalMeasurement)).setSelection(Elements.getSortedMeasurement(sqLiteDatabase, 1, goal.getMeasurement()) + 1);
            builder.setPositiveButton(R.string.delete, (dialog, id) -> delete(goal));
            builder.setNegativeButton(R.string.update, null);
        } else {
            builder.setPositiveButton(R.string.add_new, null);
        }
        builder.setView(goalView);
        this.dialog = builder.create();
        if (goal.getId() != -1) {
            dialog.setOnShowListener(dialog -> {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                button.setOnClickListener(view -> save(goalView, goal));
            });
        } else {
            dialog.setOnShowListener(dialog -> {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view -> save(goalView, goal));
            });
        }
        dialog.show();
    }

    private void save(View goalView, Goal goal) {
        boolean isFilledOut = true;
        Spinner exercise = goalView.findViewById(R.id.goalExercise);
        Spinner unit = goalView.findViewById(R.id.goalMeasurement);
        EditText amount = goalView.findViewById(R.id.goalAmount);

        goal.setExercise(new Exercise(sqLiteDatabase, exercise.getSelectedItem().toString()));
        goal.setMeasurement(new Measurement(sqLiteDatabase, unit.getSelectedItem().toString()));
        if (!"".equals(amount.getText().toString())) {
            goal.setAmount(Double.parseDouble(amount.getText().toString()));
        }

        if (goal.getExercise().getId() == -1) {
            TextView errorText = (TextView) exercise.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(R.string.indicate_exercise);
            isFilledOut = false;
        }
        if (goal.getMeasurement().getId() == -1) {
            TextView errorText = (TextView) unit.getSelectedView();
            errorText.setError("");
            isFilledOut = false;
        }
        if (goal.getAmount() == 0) {
            amount.setError(context.getString(R.string.indicate_duration));
            isFilledOut = false;
        }
        if (!isFilledOut) {
            return;
        }
        goal.save();
        GoalsActivity.populateGoals(context, sqLiteDatabase);
        dialog.dismiss();
    }

    private void delete(Goal goal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_goal);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(context.getString(R.string.confirm_goal_delete, goal.getString()));
        builder.setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
            goal.delete();
            GoalsActivity.populateGoals(context, sqLiteDatabase);
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }
}
