package com.fatmax.beerfit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Elements;
import com.fatmax.beerfit.utilities.Goal;

import java.util.ArrayList;
import java.util.List;

import static com.fatmax.beerfit.MainActivity.getScreenWidth;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class AddGoalActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        database = new Database(sqLiteDatabase);

        // setup our two spinners
        createSpinner(EXERCISES_TABLE, "current", R.id.goalSelection, false);
        createSpinner(MEASUREMENTS_TABLE, "unit", R.id.goalDurationUnits, true);
        //setup our object widths
        findViewById(R.id.goalDurationInput).getLayoutParams().width = (int) (getScreenWidth(this) * 0.3);

        Intent myIntent = getIntent();
        if (myIntent.hasExtra("goalId")) {
            int goalId = myIntent.getIntExtra("goalId", -1);
            setTitle(getString(R.string.edit_your_goal));
            Button submit = findViewById(R.id.submitGoal);
            submit.setTag(goalId);
            submit.setText(getString(R.string.update_goal));

            Goal goal = new Goal(sqLiteDatabase, goalId);
            ((Spinner) findViewById(R.id.goalSelection)).setSelection(goal.getExercise().getId());
            ((TextView) findViewById(R.id.goalDurationInput)).setText(String.valueOf(goal.getAmount()));
            ((Spinner) findViewById(R.id.goalDurationUnits)).setSelection(Elements.getSortedMeasurement(sqLiteDatabase, 1, goal.getMeasurement()) + 1);
        }
    }

    public void addGoal(View view) {
        boolean isFilledOut = true;
        Spinner activity = findViewById(R.id.goalSelection);
        Spinner units = findViewById(R.id.goalDurationUnits);
        EditText duration = findViewById(R.id.goalDurationInput);
        if ("".equals(activity.getSelectedItem().toString())) {
            TextView errorText = (TextView) activity.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(R.string.indicate_exercise);
            isFilledOut = false;
        }
        if ("".equals(units.getSelectedItem().toString())) {
            TextView errorText = (TextView) units.getSelectedView();
            errorText.setError("");
            isFilledOut = false;
        }
        if ("".equals(duration.getText().toString())) {
            duration.setError(getString(R.string.indicate_duration));
            isFilledOut = false;
        }
        if (!isFilledOut) {
            return;
        }
        Button submit = findViewById(R.id.submitGoal);
        if (submit.getTag() instanceof Integer) {
            // if we're updating a goal
            int goalId = (int) submit.getTag();
            database.removeGoal(goalId);
            database.addGoal(String.valueOf(goalId), activity.getSelectedItem().toString(), units.getSelectedItem().toString(), Double.parseDouble(duration.getText().toString()));
        } else {
            database.addGoal(activity.getSelectedItem().toString(), units.getSelectedItem().toString(), Double.parseDouble(duration.getText().toString()));
        }
        Intent intent = new Intent(this, ViewGoalsActivity.class);
        startActivity(intent);
    }

    private void createSpinner(String activity, String type, int p, boolean sort) {
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
        ArrayAdapter<String> activitiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner activitySpinner = findViewById(p);
        activitySpinner.setAdapter(activitiesAdapter);
    }
}
