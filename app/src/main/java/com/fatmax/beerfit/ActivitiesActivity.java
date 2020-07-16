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
import com.fatmax.beerfit.utilities.TableBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ActivitiesActivity extends AppCompatActivity {

    static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("EEE, MMM d yyyy, kk:mm", Locale.US);
    SQLiteDatabase sqLiteDatabase;
    Database database;

    public static void populateActivities(Context context, SQLiteDatabase sqLiteDatabase) {
        TableBuilder tableBuilder = new TableBuilder(context);
        TableLayout tableLayout = ((android.app.Activity) context).findViewById(R.id.activityBodyTable);
        if( tableLayout == null ) {
            return;
        }
        tableLayout.removeAllViews();
        List<Activity> activities = Elements.getAllActivities(sqLiteDatabase);
        for (Activity activity : activities) {
            // setup our time cell
            TextView timeView = tableBuilder.createTextView(datetimeFormat.format(activity.getDateTime()));
            // setup our activity cell
            TextView activityView;
            activityView = tableBuilder.createTextView(activity.getString());
            activityView.setOnClickListener(view -> editActivity(context, sqLiteDatabase, view));
            tableLayout.addView(tableBuilder.createTableRow(String.valueOf(activity.getId()),
                    Arrays.asList(timeView, activityView)));
        }
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

    static void editActivity(Context context, SQLiteDatabase sqLiteDatabase, View view) {
        TableRow row = (TableRow) view.getParent();
        Activity activity = new Activity(sqLiteDatabase, Integer.parseInt(row.getTag().toString()));
        ActivityModal activityModal = new ActivityModal(context, sqLiteDatabase);
        activityModal.launch(activity);
    }
}