package com.fatmax.beerfit.utilities;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data {

    private Map<Activity, List<DataPoint>> dataPoints;

    public Data() {
        dataPoints = new HashMap<>();
    }

    public void addDataPoint(Activity activity) {
        for (Map.Entry<Activity, List<DataPoint>> entry : dataPoints.entrySet()) {
            if (entry.getKey().getExercise().getId() == activity.getExercise().getId() &&
                    entry.getKey().getMeasurement().getId() == activity.getMeasurement().getId()) { // if the same exercise and measurement
                entry.getValue().add(new DataPoint(activity.getBeers(), activity.getAmount()));
                return;
            }
        }
        dataPoints.put(activity, new ArrayList<DataPoint>() {{
            add(new DataPoint(activity.getBeers(), activity.getAmount()));
        }});
    }

    public List<LineGraphSeries<DataPoint>> getSeriesData() {
        zeroOut();
        List<LineGraphSeries<DataPoint>> graphSeries = new ArrayList<>();
        for (Map.Entry<Activity, List<DataPoint>> entry : dataPoints.entrySet()) {
            List<DataPoint> activityDataPoints = entry.getValue();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(activityDataPoints.toArray(new DataPoint[0]));
            series.setTitle(entry.getKey().getExercise().getPast() + " (" + Elements.getProperStringPluralization(entry.getKey().getMeasurement().getUnit(), 2) + ")");
            series.setDrawDataPoints(true);
            series.setThickness(7);
            series.setColor(entry.getKey().getExercise().getColor());
            graphSeries.add(series);
        }
        return graphSeries;
    }

    void zeroOut() {
        List<Double> dates = new ArrayList<>();
        // get all of the dates
        for (Map.Entry<Activity, List<DataPoint>> entry : dataPoints.entrySet()) {
            for (DataPoint datapoint : entry.getValue()) {
                dates.add(datapoint.getX());
            }
        }
        // make them unique
        Set<Double> uniqueDates = new HashSet<>(dates);
        // zero out any that might be missing
        for (Map.Entry<Activity, List<DataPoint>> entry : dataPoints.entrySet()) {
            List<DataPoint> activityDataPoints = entry.getValue();
            for (Double date : uniqueDates) {
                if (!doesDataPointsContainX(date, activityDataPoints)) {
                    activityDataPoints.add(getDataPointSpot(entry.getKey(), date), new DataPoint(date, 0));
                }
            }
        }
    }

    int getDataPointSpot(Activity activity, Double date) {
        List<DataPoint> activityDataPoints = this.dataPoints.get(activity);
        if (activityDataPoints == null) {
            return 0;
        }
        for (int pos = 0; pos < activityDataPoints.size(); pos++) {
            if (activityDataPoints.get(pos).getX() > date) {
                return pos;
            }
        }
        return activityDataPoints.size();
    }

    boolean doesDataPointsContainX(Double x, List<DataPoint> dataPoints) {
        for (DataPoint datapoint : dataPoints) {
            if (x == datapoint.getX()) {
                return true;
            }
        }
        return false;
    }

    Double getMultiplier(String datePattern) {
        String[] bits = datePattern.split(" ");
        switch (bits.length) {
            case 2:
                return 1.0 / 12;
            case 3:
                return 1.0 / 52;
            case 4:
                return 1.0 / 366;
            default:
                return 0.0;
        }
    }

    public Double getXAxis(String datePattern) {
        //get the year, and then divide it out
        String[] bits = datePattern.split(" ");
        double axis = Double.parseDouble(bits[0]);
        return axis + (Double.parseDouble(bits[bits.length - 1]) - 1) * getMultiplier(datePattern);
    }

    public Double getXAxisMin(String tag, Metric metric) {
        //TODO - should limit this due to what is available
        Double multiplier = getMultiplier(tag);
        if (multiplier == 0) {
            return Double.parseDouble(getFirst(tag));
        }
        return Double.parseDouble(getFirst(tag)) + (Double.parseDouble(getLast(tag)) - 1) * multiplier;
    }

    public Double getXAxisMax(String tag, Metric metric) {
        //TODO - should limit this due to what is available
        Double multiplier = getMultiplier(tag);
        if (multiplier == 0) {
            return Double.parseDouble(getFirst(tag)) + 1;
        }
        return Double.parseDouble(getFirst(tag)) + Double.parseDouble(getLast(tag)) * multiplier;
    }

    private String getFirst(String s) {
        return s.split("\\s+")[0];
    }

    private String getLast(String s) {
        return s.split("\\s+")[s.split("\\s+").length - 1];
    }
}
