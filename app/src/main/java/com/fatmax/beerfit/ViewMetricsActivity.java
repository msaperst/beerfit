package com.fatmax.beerfit;

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
import com.fatmax.beerfit.utilities.Elements;
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
import java.util.Map;

public class ViewMetricsActivity extends AppCompatActivity {
    public static final String TIME_AS_DATE_FROM = "',time) AS date FROM ";

    //TODO
    // - on scroll, change other to match it
    final List<Metric> metrics = new ArrayList<>();
    SQLiteDatabase sqLiteDatabase;
    Database database;
    TableBuilder tableBuilder;
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

        List<String> activityTimes = Elements.getAllActivityTimes(sqLiteDatabase, metric, "DESC");
        for (String activityTime : activityTimes) {
            // each date metric needs it's own title row and data
            List<TableRow> periodRows = new ArrayList<>();
            // determine the beers drank
            int beersDrank = Elements.getBeersDrank(sqLiteDatabase, metric, activityTime);
            int beersEarned = 0;
            Map<String, Integer> activityGroups = Elements.getActivitiesPerformed(sqLiteDatabase, metric, activityTime);
            for (Map.Entry<String, Integer> activityGroup : activityGroups.entrySet()) {
                periodRows.add(createMetricsRow(activityTime, Collections.singletonList(tableBuilder.createTextView(activityGroup.getKey()))));
                beersEarned += activityGroup.getValue();
            }
            // finally, add each of our rows
            metricsView.addView(createMetricsRow(activityTime, Collections.singletonList(tableBuilder.createHeaderView(metric.getTitle(activityTime) + " (" + beersDrank + " drank / " + beersEarned + " earned beers)"))));
            for (TableRow row : periodRows) {
                metricsView.addView(row);
            }
        }
        if (tag != null) {
            scrollTo(tag);
        }
    }

    void createDataGraph(String tag, Metric metric) {
        GraphView graph = findViewById(R.id.metricsGraph);
        graph.removeAllSeries();
        Data data = new Data(database);

        List<String> activityTimes = Elements.getAllActivityTimes(sqLiteDatabase, metric, "ASC");
        for (String activityTime : activityTimes) {
            //for each activity in the date metric, tally them all
            Map<String, DataPoint> activityGroups = Elements.getActivitiesGroupedByExerciseAndTimeFrame(sqLiteDatabase, metric, data, activityTime);
            for (Map.Entry<String, DataPoint> activityGroup : activityGroups.entrySet()) {
                data.addDataPoint(activityGroup.getKey(), activityGroup.getValue());
            }
        }
        // setup the metrics
        for (LineGraphSeries<DataPoint> series : data.getSeriesData()) {
            graph.addSeries(series);
        }
        if (tag != null) {
            setupGraph(graph, data, tag);
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
