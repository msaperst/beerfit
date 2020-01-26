package com.fatmax.beerfit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddActivityActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // setup our two spinners
        createSpinner("Activities", "type", R.id.activitySelection);
        createSpinner("Measurements", "unit", R.id.activityDurationUnits);
    }

    private void createSpinner(String activity, String type, int p) {
        ArrayList activities = beerFitDatabase.getFullColumn(activity, type);
        ArrayAdapter activitiesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, activities);
        activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner activitySpinner = findViewById(p);
        activitySpinner.setAdapter(activitiesAdapter);
    }

    private String getSpinnerSelection(int p) {
        Spinner activitySpinner = findViewById(p);
        return activitySpinner.getSelectedItem().toString();
    }

    public void logActivity(View view) {
        String activity = getSpinnerSelection(R.id.activitySelection);
        String units = getSpinnerSelection(R.id.activityDurationUnits);
        EditText durationInput = findViewById(R.id.activityDurationInput);
        if (durationInput.getText().toString() == null || "".equals(durationInput.getText().toString())) {
            durationInput.setError("You need to indicate some duration of your activity");
            return;
        }
        beerFitDatabase.logActivity(activity, units, Double.valueOf(durationInput.getText().toString()));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
