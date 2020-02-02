package com.fatmax.beerfit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static com.fatmax.beerfit.MainActivity.getScreenWidth;

public class AddGoalActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // setup our two spinners
        createSpinner("Activities", "current", R.id.goalSelection);
        createSpinner("Measurements", "unit", R.id.goalDurationUnits);
        //setup our object widths
        findViewById(R.id.goalDurationInput).getLayoutParams().width = (int) (getScreenWidth(this) * 0.3);

        Intent myIntent = getIntent();
        if (myIntent.hasExtra("goalId")) {
            // TODO - load the provided goal
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
            errorText.setText("You need to indicate some activity");
            isFilledOut = false;
        }
        if ("".equals(units.getSelectedItem().toString())) {
            TextView errorText = (TextView) units.getSelectedView();
            errorText.setError("");
            isFilledOut = false;
        }
        if ("".equals(duration.getText().toString())) {
            duration.setError("You need to indicate some duration of your activity");
            isFilledOut = false;
        }
        if (!isFilledOut) {
            return;
        }
        TextView header = findViewById(R.id.addGoalHeader);
        if (header.getTag() != null && header.getTag() instanceof Integer) {
            // if we're updating a goal
            int goalId = (int) header.getTag();
            beerFitDatabase.removeGoal(goalId);
            beerFitDatabase.addGoal(String.valueOf(goalId), activity.getSelectedItem().toString(), units.getSelectedItem().toString(), Double.valueOf(duration.getText().toString()));
        } else {
            beerFitDatabase.addGoal(activity.getSelectedItem().toString(), units.getSelectedItem().toString(), Double.valueOf(duration.getText().toString()));
        }
        Intent intent = new Intent(this, ViewGoalsActivity.class);
        startActivity(intent);
    }

    private void createSpinner(String activity, String type, int p) {
        ArrayList<Object> activities = beerFitDatabase.getFullColumn(activity, type);
        activities.add(0, "");
        ArrayAdapter<Object> activitiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activities);
        activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner activitySpinner = findViewById(p);
        activitySpinner.setAdapter(activitiesAdapter);
    }
}
