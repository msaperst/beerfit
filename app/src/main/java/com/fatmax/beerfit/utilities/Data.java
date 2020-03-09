package com.fatmax.beerfit.utilities;

import android.graphics.Color;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Data {

    Map<String, List<DataPoint>> dataPoints;

    public Data() {
        dataPoints = new HashMap<>();
    }

    public List<LineGraphSeries<DataPoint>> getSeriesData() {
        zeroOut();
        List<LineGraphSeries<DataPoint>> graphSeries = new ArrayList<>();
        Random rnd = new Random();
        for (Map.Entry<String, List<DataPoint>> entry : dataPoints.entrySet()) {
            List<DataPoint> dataPoints = entry.getValue();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints.toArray(new DataPoint[dataPoints.size()]));
            series.setTitle(entry.getKey());
            series.setDrawDataPoints(true);
            series.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
            graphSeries.add(series);
        }
        return graphSeries;
    }

    public Map<String, List<DataPoint>> getDataPoints() {
        return dataPoints;
    }

    public void createActivity(String activity) {
        if (!dataPoints.containsKey(activity)) {
            dataPoints.put(activity, new ArrayList<>());
        }
    }

    public void addDataPoint(String activity, Double xValue, Double yValue) {
        List<DataPoint> activities = dataPoints.get(activity);
        activities.add(new DataPoint(xValue, yValue));
        dataPoints.put(activity, activities);
    }

    public void zeroOut() {
        List<Double> dates = new ArrayList<>();
        // get all of the dates
        for (Map.Entry<String, List<DataPoint>> entry : dataPoints.entrySet()) {
            for (DataPoint datapoint : entry.getValue()) {
                dates.add(datapoint.getX());
            }
        }
        // make them unique
        Set<Double> uniqueDates = new HashSet<>(dates);
        // zero out any that might be missing
        for (Map.Entry<String, List<DataPoint>> entry : dataPoints.entrySet()) {
            List<DataPoint> dataPoints = entry.getValue();
            for (Double date : uniqueDates) {
                if (!doesDataPointsContainX(date, dataPoints)) {
                    dataPoints.add(getDataPointSpot(entry.getKey(), date), new DataPoint(date, 0));
                }
            }
        }
    }

    public int getDataPointSpot(String activity, Double date) {
        List<DataPoint> dataPoints = this.dataPoints.get(activity);
        for (int pos = 0; pos < dataPoints.size(); pos++) {
            if (dataPoints.get(pos).getX() > date) {
                return pos;
            }
        }
        return dataPoints.size();
    }

    public Boolean doesDataPointsContainX(Double x, List<DataPoint> dataPoints) {
        boolean doesContain = false;
        for (DataPoint datapoint : dataPoints) {
            if (x == datapoint.getX()) {
                return true;
            }
        }
        return doesContain;
    }

    public Double getXAxis(String datePattern) {
        return Double.parseDouble(removeInsides(datePattern));
    }

    public Double getXAxisMin(String tag, Metric metric) {
        //TODO - should limit this due to what is available
        if (metric.getPatternMin() == null) {
            return Double.parseDouble(getFirst(tag));
        }
        return Double.parseDouble(getFirst(tag) + metric.getPatternMin());
    }

    public Double getXAxisMax(String tag, Metric metric) {
        //TODO - should limit this due to what is available
        if (metric.getPatternMax() == null) {
            return Double.parseDouble(getFirst(tag)) + 1;
        }
        return Double.parseDouble(getFirst(tag) + metric.getPatternMax());
    }

    private String removeInsides(String s) {
        return s.replaceAll("\\s.*\\s", "").replaceAll("\\s+", "");
    }

    private String getFirst(String s) {
        return s.split("\\s+")[0];
    }
}
