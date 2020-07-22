package com.fatmax.beerfit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fatmax.beerfit.utilities.Activity;
import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Elements;
import com.fatmax.beerfit.views.ViewBuilder;

import java.util.Arrays;
import java.util.List;

import static com.fatmax.beerfit.utilities.Activity.FULL_DATE_TIME_FORMAT;

public class ActivitiesActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    Database database;

    public static void populateActivities(Context context, SQLiteDatabase sqLiteDatabase) {
        ViewBuilder viewBuilder = new ViewBuilder(context);
        TableLayout tableLayout = ((android.app.Activity) context).findViewById(R.id.activitiesTable);
        if (tableLayout == null) {
            return;
        }
        tableLayout.removeAllViews();
        List<Activity> activities = Elements.getAllActivities(sqLiteDatabase);
        for (Activity activity : activities) {
            // setup our time cell
            TextView timeView = viewBuilder.createTextView(FULL_DATE_TIME_FORMAT.format(activity.getDateTime()));
            timeView.setOnClickListener(view -> editActivity(context, sqLiteDatabase, view));
            // setup our activity cell
            TextView activityView = viewBuilder.createTextView(activity.getString());
            activityView.setOnClickListener(view -> editActivity(context, sqLiteDatabase, view));
            tableLayout.addView(viewBuilder.createTableRow(String.valueOf(activity.getId()),
                    Arrays.asList(timeView, activityView)));
        }
    }

    static void editActivity(Context context, SQLiteDatabase sqLiteDatabase, View view) {
        TableRow row = (TableRow) view.getParent();
        Activity activity = new Activity(sqLiteDatabase, Integer.parseInt(row.getTag().toString()));
        ActivityModal activityModal = new ActivityModal(context, sqLiteDatabase);
        activityModal.launch(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        database = new Database(sqLiteDatabase);

        // dynamically build our table
        populateActivities(this, sqLiteDatabase);
    }
}