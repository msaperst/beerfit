package com.fatmax.beerfit;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddActivityActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // setup the activities to choose from
        ArrayList activities = beerFitDatabase.getFullColumn("Activities", "type");
        ArrayAdapter activitiesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, activities);
        activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner activitySpinner = findViewById(R.id.activitySelection);
        activitySpinner.setAdapter(activitiesAdapter);

        //setup the measurements to choose from
        ArrayList measurements = beerFitDatabase.getFullColumn("Measurements", "unit");
        ArrayAdapter measurementsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, measurements);
        measurementsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner measurementsSpinner = findViewById(R.id.activityDurationUnits);
        measurementsSpinner.setAdapter(measurementsAdapter);
    }
}
