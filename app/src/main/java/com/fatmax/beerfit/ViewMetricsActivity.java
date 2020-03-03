package com.fatmax.beerfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.MEASUREMENTS_TABLE;

public class ViewMetricsActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    final SimpleDateFormat datetimeFormat = new SimpleDateFormat("EEE, MMM d yyyy, kk:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_metrics);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        TableLayout metricsView = findViewById(R.id.metricsBodyTable);
        //create each of our year metrics - these can then be drilled into
        Cursor yearCursor = sqLiteDatabase.rawQuery("SELECT DISTINCT strftime('%Y',time) FROM " + ACTIVITY_LOG_TABLE, null);
        if (yearCursor != null) {
            if (yearCursor.getCount() > 0) {
                yearCursor.moveToFirst();
                while (!yearCursor.isAfterLast()) {
                    String year = yearCursor.getString(0);
                    // each year needs it's own title row and data
                    metricsView.addView(createTableRow(year, Collections.singletonList(createHeaderView(year))));
                    //for each activity in the year, tally them all
                    Cursor activityCursor = sqLiteDatabase.rawQuery("SELECT " + ACTIVITIES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit FROM " + ACTIVITY_LOG_TABLE + " LEFT JOIN " + ACTIVITIES_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".activity = " + ACTIVITIES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE strftime('%Y',time) = '" + year + "' GROUP BY " + ACTIVITIES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit", null);
                    if (activityCursor != null) {
                        if (activityCursor.getCount() > 0) {
                            activityCursor.moveToFirst();
                            while (!activityCursor.isAfterLast()) {
                                // each distinct activity needs it's own row and data
                                String text;
                                if (activityCursor.getString(0) == null) {
                                    text = "Drank " + activityCursor.getDouble(1) + " beer";
                                    if (activityCursor.getInt(1) > 1) {
                                        text += "s";
                                    }
                                } else {
                                    text = activityCursor.getString(0) + " for " + activityCursor.getDouble(1) + " " + activityCursor.getString(2);
                                }
                                metricsView.addView(createTableRow(year, Collections.singletonList(createTextView(text))));
                                //for each activity in the year, tally them all
                                activityCursor.moveToNext();
                            }
                        }
                        activityCursor.close();
                    }
                    yearCursor.moveToNext();
                }
            }
            yearCursor.close();
        }
    }

    TableRow createTableRow(String tag, List<TextView> cells) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        row.setTag(tag);
        for (TextView cell : cells) {
            row.addView(cell);
        }
        return row;
    }

    TextView createHeaderView(String text) {
        TextView view = createTextView(text);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(null, Typeface.BOLD);
        return view;
    }

    TextView createTextView(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        return view;
    }
}
