package com.fatmax.beerfit;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.TableBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class ViewActivitiesActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    Database database;
    TableBuilder tableBuilder;

    final SimpleDateFormat datetimeFormat = new SimpleDateFormat("EEE, MMM d yyyy, kk:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_activities);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        database = new Database(sqLiteDatabase);
        tableBuilder = new TableBuilder(this);

        // dynamically build our table
        TableLayout tableLayout = findViewById(R.id.activityBodyTable);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + ACTIVITY_LOG_TABLE + ".id, " + ACTIVITY_LOG_TABLE + ".time, " + ACTIVITIES_TABLE + ".past, " + ACTIVITY_LOG_TABLE + ".amount, " + MEASUREMENTS_TABLE + ".unit FROM " + ACTIVITY_LOG_TABLE + " LEFT JOIN " + ACTIVITIES_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".activity = " + ACTIVITIES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id ORDER BY " + ACTIVITY_LOG_TABLE + ".time DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // setup our cells
            LocalDateTime localDateTime = LocalDateTime.parse(cursor.getString(1).replace(" ", "T"));
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            TextView time = tableBuilder.createTextView(datetimeFormat.format(date));
            TextView activity;
            if (cursor.getString(2) == null) {
                activity = tableBuilder.createTextView("Drank " + cursor.getInt(3) + " beer");
                if (cursor.getInt(3) > 1) {
                    activity.setText(getString(R.string.plural, activity.getText()));
                }
            } else {
                activity = tableBuilder.createTextView(cursor.getString(2) + " for " + cursor.getDouble(3) + " " + cursor.getString(4));
            }

            // create and setup our edit button
            ImageButton editButton = tableBuilder.createEditButton();
            editButton.setOnClickListener(this::editActivity);
            // create and setup our delete button
            ImageButton deleteButton = tableBuilder.createDeleteButton();
            deleteButton.setOnClickListener(this::deleteActivity);

            // build our row
            tableLayout.addView(tableBuilder.createTableRow(cursor.getString(0),
                    Arrays.asList(time, activity, editButton, deleteButton)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    void editActivity(View editButton) {
        TableRow row = (TableRow) editButton.getParent();
        int activityId = Integer.parseInt(row.getTag().toString());
        Intent intent = new Intent(this, AddActivityActivity.class);
        intent.putExtra("activityId", activityId);
        startActivity(intent);
    }

    void deleteActivity(View deleteButton) {
        final TableRow row = (TableRow) deleteButton.getParent();
        final int activityId = Integer.parseInt(row.getTag().toString());
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Activity");
        alert.setMessage("Are you sure to delete the activity on " + database.getActivityTime(activityId));
        alert.setPositiveButton("YES", (dialog, which) -> {
            database.removeActivity(activityId);
            ((LinearLayout) findViewById(R.id.activityBodyTable)).removeView(row);
            dialog.dismiss();
        });
        alert.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        alert.show();
    }
}
