package com.fatmax.beerfit;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fatmax.beerfit.utilities.Activity;
import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Elements;
import com.fatmax.beerfit.utilities.TableBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ViewActivitiesActivity extends AppCompatActivity {

    final SimpleDateFormat datetimeFormat = new SimpleDateFormat("EEE, MMM d yyyy, kk:mm", Locale.US);
    SQLiteDatabase sqLiteDatabase;
    Database database;
    TableBuilder tableBuilder;

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
        List<Activity> activities = Elements.getAllActivities(sqLiteDatabase);
        for (Activity activity : activities) {
            // setup our time cell
            TextView timeView = tableBuilder.createTextView(datetimeFormat.format(activity.getDateTime()));
            // setup our activity cell
            TextView activityView;
            if (activity.getExercise().getId() == -1 && activity.getMeasurement().getId() == -1) {    // if it's beer
                activityView = tableBuilder.createTextView("Drank " + (int) activity.getAmount() + " beer");
                if (activity.getAmount() != 1) {
                    activityView.setText(getString(R.string.plural, activityView.getText()));
                }
            } else {
                activityView = tableBuilder.createTextView(activity.getExercise().getPast() + " for " + activity.getAmount() + " " + Elements.getProperStringPluralization(activity.getMeasurement().getUnit(), activity.getAmount()));
            }

            // create and setup our edit button
            ImageButton editButton = tableBuilder.createEditButton();
            editButton.setOnClickListener(this::editActivity);
            // create and setup our delete button
            ImageButton deleteButton = tableBuilder.createDeleteButton();
            deleteButton.setOnClickListener(this::deleteActivity);

            // build our row
            tableLayout.addView(tableBuilder.createTableRow(String.valueOf(activity.getId()),
                    Arrays.asList(timeView, activityView, editButton, deleteButton)));
        }
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
