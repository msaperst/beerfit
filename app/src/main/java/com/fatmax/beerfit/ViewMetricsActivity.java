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

import com.fatmax.beerfit.utilities.Metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.MEASUREMENTS_TABLE;

public class ViewMetricsActivity extends AppCompatActivity {

    //TODO
    // - cleanup CC of createDataTable
    // - fix/add title of breakdown
    // - fix styling...just fill whole row?
    // - add all data, don't just filter. but have it jump to relevant data on scroller
    // - add graphs of activities, maybe on bottom half of viewing screen

    public static final String AND = " AND ";
    public static final String STRFTIME_Y_TIME_YEAR = "strftime('%Y',time) = 'YEAR'";
    public static final String STRFTIME_M_TIME_MONTH = "strftime('%m',time) = 'MONTH'";
    public static final String STRFTIME_W_TIME_WEEK = "strftime('%W',time) = 'WEEK'";
    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    final List<Metric> metrics = new ArrayList<>();
    Iterator<Metric> metricsIterator;
    Metric metric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_metrics);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        metrics.add(new Metric("YEAR", null, "%Y", new ArrayList<>()));
        metrics.add(new Metric("MONTH", null, "%Y %m", Collections.singletonList(STRFTIME_Y_TIME_YEAR)));
        metrics.add(new Metric("WEEK", null, "%Y %m %W", Arrays.asList(STRFTIME_Y_TIME_YEAR, STRFTIME_M_TIME_MONTH)));
        metrics.add(new Metric("DAY", null, "%Y %m %W %j", Arrays.asList(STRFTIME_Y_TIME_YEAR, STRFTIME_M_TIME_MONTH, STRFTIME_W_TIME_WEEK)));
        metricsIterator = metrics.iterator();
        metric = metricsIterator.next();
        createDataTable(metric);
    }

    void createDataTable(Metric metric) {
        // setup our table
        TableLayout metricsView = findViewById(R.id.metricsBodyTable);
        metricsView.removeAllViews();
        // setup our filters
        String overallFilter = "";
        String detailFilter = "";
        if (metric.getFilters() != null && !metric.getFilters().isEmpty()) {
            overallFilter = " WHERE " + String.join(AND, metric.getFilters());
            detailFilter = AND + String.join(AND, metric.getFilters());
        }
        Cursor timeCursor = sqLiteDatabase.rawQuery("SELECT DISTINCT strftime('" + metric.getDateTimePattern() + "',time) FROM " + ACTIVITY_LOG_TABLE + overallFilter, null);
        if (timeCursor != null) {
            if (timeCursor.getCount() > 0) {
                timeCursor.moveToFirst();
                while (!timeCursor.isAfterLast()) {
                    String dateMetric = timeCursor.getString(0);
                    // each date metric needs it's own title row and data
                    metricsView.addView(createTableRow(dateMetric, Collections.singletonList(createHeaderView(dateMetric))));
                    //for each activity in the date metric, tally them all
                    Cursor activityCursor = sqLiteDatabase.rawQuery("SELECT " + ACTIVITIES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit FROM " + ACTIVITY_LOG_TABLE + " LEFT JOIN " + ACTIVITIES_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".activity = " + ACTIVITIES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE strftime('" + metric.getDateTimePattern() + "',time) = '" + dateMetric + "' " + detailFilter + " GROUP BY " + ACTIVITIES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit", null);
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
                                metricsView.addView(createTableRow(dateMetric, Collections.singletonList(createTextView(text))));
                                //for each activity in the date metric, tally them all
                                activityCursor.moveToNext();
                            }
                        }
                        activityCursor.close();
                    }
                    timeCursor.moveToNext();
                }
            }
            timeCursor.close();
        }
    }

    TableRow createTableRow(final String tag, List<TextView> cells) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        row.setTag(tag);
        for (TextView cell : cells) {
            row.addView(cell);
        }
        row.setOnClickListener(v -> {
            if (metricsIterator.hasNext()) {
                String[] bits = tag.split(" ");
                for (Metric m : metrics) {
                    m.updateFilter(metric.getType(), bits[bits.length - 1]);
                }
                metric = metricsIterator.next();
                createDataTable(metric);
            }
        });
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
