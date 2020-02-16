package com.fatmax.beerfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.MEASUREMENTS_TABLE;
import static com.fatmax.beerfit.TableBuilder.createDeleteButton;
import static com.fatmax.beerfit.TableBuilder.createEditButton;
import static com.fatmax.beerfit.TableBuilder.createTextView;

public class ViewActivitiesActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    final SimpleDateFormat datetimeFormat = new SimpleDateFormat("EEE, MMM d yyyy, kk:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_activities);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // dynamically build our table
        TableLayout tableLayout = findViewById(R.id.activitiesTable);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + ACTIVITY_LOG_TABLE + ".id, " + ACTIVITY_LOG_TABLE + ".time, " + ACTIVITIES_TABLE + ".past, " + ACTIVITY_LOG_TABLE + ".amount, " + MEASUREMENTS_TABLE + ".unit FROM " + ACTIVITY_LOG_TABLE + " LEFT JOIN " + ACTIVITIES_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".activity = " + ACTIVITIES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id ORDER BY " + ACTIVITY_LOG_TABLE + ".time DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // setup our table row
            TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            row.setTag(cursor.getInt(0));

            // setup our cells
            LocalDateTime localDateTime = LocalDateTime.parse(cursor.getString(1).replace(" ", "T"));
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            TextView time = createTextView(this, "time", datetimeFormat.format(date));
            TextView activity;
            if (cursor.getString(2) == null) {
                activity = createTextView(this, "activity", "Drank " + cursor.getInt(3) + " beer");
                if (cursor.getInt(3) > 1) {
                    activity.setText(activity.getText() + "s");
                }
            } else {
                activity = createTextView(this, "activity", cursor.getString(2) + " for " + cursor.getDouble(3) + " " + cursor.getString(4));
            }

            // create and setup our edit button
            ImageButton editButton = createEditButton(this);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editActivity(v);
                }
            });
            // create and setup our delete button
            ImageButton deleteButton = createDeleteButton(this);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteActivity(v);
                }
            });

            // build our rows
            row.addView(time);
            row.addView(activity);
            row.addView(editButton);
            row.addView(deleteButton);
            tableLayout.addView(row);
            cursor.moveToNext();
        }
        cursor.close();
    }

    void editActivity(View editButton) {
        TableRow row = (TableRow) editButton.getParent();
        int activityId = (int) row.getTag();
        Intent intent = new Intent(this, AddActivityActivity.class);
        intent.putExtra("activityId", activityId);
        startActivity(intent);
    }

    void deleteActivity(View deleteButton) {
        final TableRow row = (TableRow) deleteButton.getParent();
        final int activityId = (int) row.getTag();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Activity");
        alert.setMessage("Are you sure to delete the activity on " + beerFitDatabase.getActivityTime(activityId));
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                beerFitDatabase.removeActivity(activityId);
                ((LinearLayout) findViewById(R.id.activitiesTable)).removeView(row);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
}
