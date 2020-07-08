package com.fatmax.beerfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fatmax.beerfit.utilities.Data;
import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Metric;
import com.fatmax.beerfit.utilities.TableBuilder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class ViewMetricsActivity extends AppCompatActivity {
    public static final String TIME_AS_DATE_FROM = "',time) AS date FROM ";

    //TODO
    // - on scroll, change other to match it

    SQLiteDatabase sqLiteDatabase;
    Database database;
    TableBuilder tableBuilder;

    final List<Metric> metrics = new ArrayList<>();
    Iterator<Metric> metricsIterator;
    Metric metric;

    int rowHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_metrics);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        database = new Database(sqLiteDatabase);
        tableBuilder = new TableBuilder(this);

        //setup our metrics
        metrics.add(new Metric("%Y"));
        metrics.add(new Metric("%Y %m"));
        metrics.add(new Metric("%Y %m %W"));
        metrics.add(new Metric("%Y %m %W %j"));
        metricsIterator = metrics.iterator();
        metric = metricsIterator.next();

        // draw the data
        createDataTable(null, metric);
        createDataGraph(null, metric);
        // if no data
        TableLayout table = findViewById(R.id.metricsBodyTable);
        if (table.getChildCount() == 0) {
            TextView text = tableBuilder.createHeaderView("No Data Present");
            TableRow row = tableBuilder.createTableRow(Collections.singletonList(text));
            table.addView(row);
        }
    }

    void createDataTable(String tag, Metric metric) {
        // setup our table
        TableLayout metricsView = findViewById(R.id.metricsBodyTable);
        metricsView.removeAllViews();
        Cursor timeCursor = sqLiteDatabase.rawQuery("SELECT DISTINCT strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITY_LOG_TABLE + " ORDER BY date DESC", null);
        if (timeCursor != null) {
            if (timeCursor.getCount() > 0) {
                timeCursor.moveToFirst();
                while (!timeCursor.isAfterLast()) {
                    loopThroughActivitiesData(metric, metricsView, timeCursor);
                    timeCursor.moveToNext();
                }
            }
            timeCursor.close();
        }
        if (tag != null) {
            scrollTo(tag);
        }
    }

    private void loopThroughActivitiesData(Metric metric, TableLayout metricsView, Cursor timeCursor) {
        String dateMetric = timeCursor.getString(0);
        // each date metric needs it's own title row and data
        List<TableRow> periodRows = new ArrayList<>();
        // determine the beers drank
        int beersDrank = 0;
        int beersEarned = 0;
        Cursor beersCursor = sqLiteDatabase.rawQuery("SELECT SUM(amount), strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITY_LOG_TABLE + " WHERE date = '" + dateMetric + "' AND " + ACTIVITY_LOG_TABLE + ".activity = 0 GROUP BY date", null);
        if (beersCursor != null) {
            if (beersCursor.getCount() > 0) {
                beersCursor.moveToFirst();
                while (!beersCursor.isAfterLast()) {
                    beersDrank = beersCursor.getInt(0);
                    //for each activity in the date metric, tally them all
                    beersCursor.moveToNext();
                }
            }
            beersCursor.close();
        }
        //for each activity in the date metric, tally them all
        Cursor activityCursor = sqLiteDatabase.rawQuery("SELECT " + EXERCISES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit, SUM(beers), strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITY_LOG_TABLE + " LEFT JOIN " + EXERCISES_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".activity = " + EXERCISES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE date = '" + dateMetric + "' AND " + ACTIVITY_LOG_TABLE + ".activity != 0 GROUP BY " + EXERCISES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit, date", null);
        if (activityCursor != null) {
            if (activityCursor.getCount() > 0) {
                activityCursor.moveToFirst();
                while (!activityCursor.isAfterLast()) {
                    beersEarned += activityCursor.getInt(3);
                    String text = getActivityText(activityCursor);
                    periodRows.add(createMetricsRow(dateMetric, Collections.singletonList(tableBuilder.createTextView(text))));
                    //for each activity in the date metric, tally them all
                    activityCursor.moveToNext();
                }
            }
            activityCursor.close();
        }
        // finally, add each of our rows
        metricsView.addView(createMetricsRow(dateMetric, Collections.singletonList(tableBuilder.createHeaderView(metric.getTitle(dateMetric) + " (" + beersDrank + " drank / " + beersEarned + " earned beers)"))));
        for (TableRow row : periodRows) {
            metricsView.addView(row);
        }
    }

    private String getActivityText(Cursor activityCursor) {
        // each distinct activity needs it's own row and data
        String text;
        if (activityCursor.getString(0) == null) {
            text = "Drank " + activityCursor.getInt(1) + " beer";
            if (activityCursor.getInt(1) > 1) {
                text += "s";
            }
        } else {
            text = activityCursor.getString(0) + " for " + activityCursor.getDouble(1) + " " + activityCursor.getString(2);
        }
        return text;
    }

    void createDataGraph(String tag, Metric metric) {
        GraphView graph = findViewById(R.id.metricsGraph);
        graph.removeAllSeries();
        Data data = new Data(database);
        Cursor timeCursor = sqLiteDatabase.rawQuery("SELECT DISTINCT strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITY_LOG_TABLE + " ORDER BY date ASC", null);
        if (timeCursor != null) {
            if (timeCursor.getCount() > 0) {
                timeCursor.moveToFirst();
                while (!timeCursor.isAfterLast()) {
                    loopThroughActivitiesGraph(metric, data, timeCursor);
                    timeCursor.moveToNext();
                }
            }
            timeCursor.close();
        }
        // setup the metrics
        for (LineGraphSeries<DataPoint> series : data.getSeriesData()) {
            graph.addSeries(series);
        }
        if (tag != null) {
            setupGraph(graph, data, tag);
        }
    }

    private void loopThroughActivitiesGraph(Metric metric, Data data, Cursor timeCursor) {
        String dateMetric = timeCursor.getString(0);
        //for each activity in the date metric, tally them all
        Cursor activityCursor = sqLiteDatabase.rawQuery("SELECT " + EXERCISES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit, strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITY_LOG_TABLE + " LEFT JOIN " + EXERCISES_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".activity = " + EXERCISES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE date = '" + dateMetric + "' GROUP BY " + EXERCISES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit, date", null);
        if (activityCursor != null) {
            if (activityCursor.getCount() > 0) {
                activityCursor.moveToFirst();
                while (!activityCursor.isAfterLast()) {
                    // determine the unique activity string
                    String activity;
                    if (activityCursor.getString(0) == null) {
                        activity = "Drank (beers)";
                    } else {
                        activity = activityCursor.getString(0) + " (" + activityCursor.getString(2) + ")";
                    }
                    // if the activity doesn't have a list, create one
                    data.addDataPoint(activity, data.getXAxis(dateMetric), activityCursor.getDouble(1));
                    //for each activity in the date metric, tally them all
                    activityCursor.moveToNext();
                }
            }
            activityCursor.close();
        }
    }

    TableRow createMetricsRow(final String tag, List<View> cells) {
        TableRow row = tableBuilder.createTableRow(tag, cells);
        row.setOnClickListener(v -> {
            if (metricsIterator.hasNext()) {
                metric = metricsIterator.next();
                createDataTable(tag, metric);
                createDataGraph(tag, metric);
            }
        });
        return row;
    }

    void setupGraph(GraphView graph, Data data, String tag) {
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(data.getXAxisMin(tag, metric));
        graph.getViewport().setMaxX(data.getXAxisMax(tag, metric));
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
    }

    void scrollTo(String tag) {
        //loop through the rows, find the one with the tag we want
        TableLayout table = findViewById(R.id.metricsBodyTable);
        int row = 0;
        for (int i = 0; i < table.getChildCount(); i++) {
            if (table.getChildAt(i).getTag().toString().startsWith(tag)) {
                row = i;
                break;
            }
        }
        if (row == 0) {
            return;
        }
        ScrollView scroller = findViewById(R.id.metricsScroller);
        int finalRow = row;
        scroller.post(() -> scroller.smoothScrollTo(0, rowHeight * finalRow));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        TableLayout table = findViewById(R.id.metricsBodyTable);
        TableRow row = (TableRow) table.getChildAt(0);
        rowHeight = row.getHeight();
    }
}
