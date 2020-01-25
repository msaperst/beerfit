package com.fatmax.beerfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewGoalsActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goals);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // dynamically build our table
        TableLayout tableLayout = findViewById(R.id.goalsTable);
        Cursor res = sqLiteDatabase.rawQuery("SELECT Goals.id, Activities.type, Goals.amount, Measurements.unit FROM Goals LEFT JOIN Activities ON Goals.activity = Activities.id LEFT JOIN Measurements ON Goals.measurement = Measurements.id", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            // setup our table row
            TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            // setup our id cell
            TextView id = new TextView(this);
            id.setText(res.getString(0));
            id.setWidth(0);
            row.addView(id);

            // setup our activity cell
            TextView activity = new TextView(this);
            activity.setText(res.getString(1));
            row.addView(activity);

            // setup our duration cell
            TextView duration = new TextView(this);
            duration.setText(res.getString(2) + " " + res.getString(3));
            row.addView(duration);

            tableLayout.addView(row);
            res.moveToNext();
        }
        res.close();
    }
}
