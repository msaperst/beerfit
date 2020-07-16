package com.fatmax.beerfit.views;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Elements;

import java.util.ArrayList;
import java.util.List;

public class Modal {

    private final Context context;
    private final SQLiteDatabase sqLiteDatabase;

    public Modal(Context context, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void createSpinner(View view, String table, String column, int element, boolean sort) {
        Database database = new Database(sqLiteDatabase);
        List<String> items = new ArrayList<>();
        if (sort) {
            items = Elements.getSortedMeasurements(sqLiteDatabase, 1);
        } else {
            List<Object> objects = database.getFullColumn(table, column);
            for (Object object : objects) {
                items.add(object.toString());
            }
        }
        items.add(0, "");
        ArrayAdapter<String> activitiesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
        activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner activitySpinner = view.findViewById(element);
        activitySpinner.setAdapter(activitiesAdapter);
    }
}
