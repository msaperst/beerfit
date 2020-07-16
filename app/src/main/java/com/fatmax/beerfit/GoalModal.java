package com.fatmax.beerfit;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Elements;
import com.fatmax.beerfit.utilities.Exercise;
import com.fatmax.beerfit.utilities.Goal;
import com.fatmax.beerfit.utilities.Measurement;

import java.util.ArrayList;
import java.util.List;

import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class GoalModal {

    Context context;
    SQLiteDatabase sqLiteDatabase;
    private AlertDialog dialog;

    public GoalModal(Context context, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void launch(Goal goal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setTitle(R.string.to_earn_a_beer);
        View goalView = inflater.inflate(R.layout.modal_goal, null);
        createSpinner(goalView, EXERCISES_TABLE, "current", R.id.goalSelection, false);
        createSpinner(goalView, MEASUREMENTS_TABLE, "unit", R.id.goalDurationUnits, true);
        // customization for edit or add
        if (goal.getId() != -1) {
            ((Spinner) goalView.findViewById(R.id.goalSelection)).setSelection(goal.getExercise().getId());
            ((TextView) goalView.findViewById(R.id.goalDurationInput)).setText(String.valueOf(goal.getAmount()));
            ((Spinner) goalView.findViewById(R.id.goalDurationUnits)).setSelection(Elements.getSortedMeasurement(sqLiteDatabase, 1, goal.getMeasurement()) + 1);
            builder.setPositiveButton(R.string.delete, (dialog, id) -> deleteGoal(goal));
            builder.setNegativeButton(R.string.update, null);
        } else {
            builder.setPositiveButton(R.string.add_new, null);
        }
        builder.setView(goalView);
        this.dialog = builder.create();
        if (goal.getId() != -1) {
            dialog.setOnShowListener(dialog -> {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                button.setOnClickListener(view -> viewGoal(goalView, goal));
            });
        } else {
            dialog.setOnShowListener(dialog -> {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view -> viewGoal(goalView, goal));
            });
        }
        dialog.show();
    }

    private void viewGoal(View goalView, Goal goal) {
        boolean isFilledOut = true;
        Spinner exercise = goalView.findViewById(R.id.goalSelection);
        Spinner unit = goalView.findViewById(R.id.goalDurationUnits);
        EditText duration = goalView.findViewById(R.id.goalDurationInput);

        goal.setExercise(new Exercise(sqLiteDatabase, exercise.getSelectedItem().toString()));
        goal.setMeasurement(new Measurement(sqLiteDatabase, unit.getSelectedItem().toString()));
        if (!"".equals(duration.getText().toString())) {
            goal.setAmount(Double.parseDouble(duration.getText().toString()));
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
            duration.setError(context.getString(R.string.indicate_duration));
            isFilledOut = false;
        }
        if (!isFilledOut) {
            return;
        }
        goal.save();
        GoalsActivity.populateGoals(context, sqLiteDatabase);
        dialog.dismiss();
    }

    private void deleteGoal(Goal goal) {
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

    private void createSpinner(View goalView, String activity, String type, int p, boolean sort) {
        Database database = new Database(sqLiteDatabase);
        List<String> items = new ArrayList<>();
        if (sort) {
            items = Elements.getSortedMeasurements(sqLiteDatabase, 1);
        } else {
            List<Object> objects = database.getFullColumn(activity, type);
            for (Object object : objects) {
                items.add(object.toString());
            }
        }
        items.add(0, "");
        ArrayAdapter<String> activitiesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
        activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner activitySpinner = goalView.findViewById(p);
        activitySpinner.setAdapter(activitiesAdapter);
    }
}
