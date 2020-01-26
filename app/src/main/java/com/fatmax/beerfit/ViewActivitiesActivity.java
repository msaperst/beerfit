package com.fatmax.beerfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewActivitiesActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_activities);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // dynamically build our table
        TableLayout tableLayout = findViewById(R.id.activitiesTable);
        Cursor res = sqLiteDatabase.rawQuery("SELECT ActivityLog.id, ActivityLog.time, Activities.type, ActivityLog.amount, Measurements.unit FROM ActivityLog LEFT JOIN Activities ON ActivityLog.activity = Activities.id LEFT JOIN Measurements ON ActivityLog.measurement = Measurements.id ORDER BY ActivityLog.time DESC", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            // setup our table row
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            // setup our cells
            TextView id = createIdTextView(this, res);
            TextView time = createTextView(this, res.getString(1));
            TextView activity = createTextView(this, res.getString(2));
            TextView duration = createTextView(this, "for " + res.getString(3) + " " + res.getString(4));

            //if we drank, put in different values
            if( res.getString(2) == null || "".equals(res.getString(2))) {
                activity.setText("Drank a beer");
                duration.setText("");
            }
            // build our rows
            row.addView(id);
            row.addView(time);
            row.addView(activity);
            row.addView(duration);
            tableLayout.addView(row);
            res.moveToNext();
        }
        res.close();
    }

    static TextView createIdTextView(Context context, Cursor res) {
        TextView id = new TextView(context);
        id.setText(res.getString(0));
        id.setWidth(0);
        return id;
    }

    static TextView createTextView(Context context, String text) {
        TextView time = new TextView(context);
        time.setPadding(10, 2, 10, 2);
        time.setText(text);
        return time;
    }
}
