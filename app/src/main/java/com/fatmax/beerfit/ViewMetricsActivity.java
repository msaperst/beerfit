package com.fatmax.beerfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fatmax.beerfit.utilities.Data;
import com.fatmax.beerfit.utilities.Metric;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.MEASUREMENTS_TABLE;

public class ViewMetricsActivity extends AppCompatActivity {
    public static final String TIME_AS_DATE_FROM = "',time) AS date FROM ";

    //TODO
    // - cleanup CC of createDataTable
    // - on scroll, change other to match it

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

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
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        //setup our metrics
        metrics.add(new Metric("Year", "%Y", null, null));
        metrics.add(new Metric("Month", "%Y %m", "01", "12"));
        metrics.add(new Metric("Week", "%Y %m %W", "01", "52"));
        metrics.add(new Metric("Day", "%Y %m %W %j", "001", "366"));
        metricsIterator = metrics.iterator();
        metric = metricsIterator.next();

        // draw the data
        createDataTable(null, metric);
        createDataGraph(null, metric);
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
                    String dateMetric = timeCursor.getString(0);
                    // each date metric needs it's own title row and data
                    metricsView.addView(createTableRow(dateMetric, Collections.singletonList(createHeaderView(metric.getTitle(dateMetric)))));
                    //for each activity in the date metric, tally them all
                    Cursor activityCursor = sqLiteDatabase.rawQuery("SELECT " + ACTIVITIES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit, strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITY_LOG_TABLE + " LEFT JOIN " + ACTIVITIES_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".activity = " + ACTIVITIES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE date = '" + dateMetric + "' GROUP BY " + ACTIVITIES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit, date", null);
                    if (activityCursor != null) {
                        if (activityCursor.getCount() > 0) {
                            activityCursor.moveToFirst();
                            while (!activityCursor.isAfterLast()) {
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
        if (tag != null) {
            scrollTo(tag);
        }
    }

    void createDataGraph(String tag, Metric metric) {
        GraphView graph = findViewById(R.id.metricsGraph);
        graph.removeAllSeries();
        Data data = new Data();
        Cursor timeCursor = sqLiteDatabase.rawQuery("SELECT DISTINCT strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITY_LOG_TABLE + " ORDER BY date ASC", null);
        if (timeCursor != null) {
            if (timeCursor.getCount() > 0) {
                timeCursor.moveToFirst();
                while (!timeCursor.isAfterLast()) {
                    String dateMetric = timeCursor.getString(0);
                    //for each activity in the date metric, tally them all
                    Cursor activityCursor = sqLiteDatabase.rawQuery("SELECT " + ACTIVITIES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit, strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITY_LOG_TABLE + " LEFT JOIN " + ACTIVITIES_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".activity = " + ACTIVITIES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITY_LOG_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE date = '" + dateMetric + "' GROUP BY " + ACTIVITIES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit, date", null);
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
                                data.createActivity(activity);
                                data.addDataPoint(activity, data.getXAxis(dateMetric), activityCursor.getDouble(1));
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
        // setup the metrics
        for (LineGraphSeries<DataPoint> series : data.getSeriesData()) {
            graph.addSeries(series);
        }
        if (tag != null) {
            setupGraph(graph, data, tag);
        }
    }

    TableRow createTableRow(final String tag, List<TextView> cells) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        row.setTag(tag);
        row.setWeightSum(1);
        for (TextView cell : cells) {
            row.addView(cell);
        }
        row.setOnClickListener(v -> {
            if (metricsIterator.hasNext()) {
                metric = metricsIterator.next();
                createDataTable(tag, metric);
                createDataGraph(tag, metric);
            }
        });
        return row;
    }

    TextView createHeaderView(String text) {
        TextView view = createTextView(text);
        view.setTextAppearance(R.style.HeaderText);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    TextView createTextView(String text) {
        TextView view = new TextView(this);
        view.setTextAppearance(R.style.BodyText);
        view.setText(text);
        return view;
    }

    void setupGraph(GraphView graph, Data data, String tag) {
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
//        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getViewport().setMinX(data.getXAxisMin(tag, metric));
//        graph.getViewport().setMaxX(data.getXAxisMax(tag, metric));
//        graph.getViewport().setScalable(true);
//        graph.getViewport().setScrollable(true);
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
