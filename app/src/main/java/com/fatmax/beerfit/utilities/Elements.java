package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.fatmax.beerfit.ViewMetricsActivity.TIME_AS_DATE_FROM;
import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class Elements {

    public static List<Activity> getAllActivities(SQLiteDatabase sqLiteDatabase) {
        List<Activity> activities = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT id FROM " + ACTIVITIES_TABLE + " ORDER BY time DESC", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    activities.add(new Activity(sqLiteDatabase, cursor.getInt(0)));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return activities;
    }

    public static List<Goal> getAllGoals(SQLiteDatabase sqLiteDatabase) {
        List<Goal> goals = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    goals.add(new Goal(sqLiteDatabase, cursor.getInt(0)));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return goals;
    }

    public static List<String> getAllActivityTimes(SQLiteDatabase sqLiteDatabase, Metric metric, String orderBy) {
        List<String> activityTimes = new ArrayList<>();
        Cursor timeCursor = sqLiteDatabase.rawQuery("SELECT DISTINCT strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " ORDER BY date " + orderBy, null);
        if (timeCursor != null) {
            if (timeCursor.getCount() > 0) {
                timeCursor.moveToFirst();
                while (!timeCursor.isAfterLast()) {
                    activityTimes.add(timeCursor.getString(0));
                    timeCursor.moveToNext();
                }
            }
            timeCursor.close();
        }
        return activityTimes;
    }

    public static Map<String, DataPoint> getActivitiesGroupedByExerciseAndTimeFrame(SQLiteDatabase sqLiteDatabase, Metric metric, Data data, String dateMetric) {
        Map<String, DataPoint> activityGroups = new TreeMap<>();
        Cursor activityCursor = sqLiteDatabase.rawQuery("SELECT " + EXERCISES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit, strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " LEFT JOIN " + EXERCISES_TABLE + " ON " + ACTIVITIES_TABLE + ".exercise = " + EXERCISES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITIES_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE date = '" + dateMetric + "' GROUP BY " + EXERCISES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit, date", null);
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
                    activityGroups.put(activity, new DataPoint(data.getXAxis(dateMetric), activityCursor.getDouble(1)));
                    activityCursor.moveToNext();
                }
            }
            activityCursor.close();
        }
        return activityGroups;
    }

    public static int getBeersDrank(SQLiteDatabase sqLiteDatabase, Metric metric, String dateMetric) {
        int beersDrank = 0;
        Cursor beersCursor = sqLiteDatabase.rawQuery("SELECT SUM(amount), strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " WHERE date = '" + dateMetric + "' AND " + ACTIVITIES_TABLE + ".exercise = 0 GROUP BY date", null);
        if (beersCursor != null) {
            if (beersCursor.getCount() > 0) {
                beersCursor.moveToFirst();
                beersDrank = beersCursor.getInt(0);
            }
            beersCursor.close();
        }
        return beersDrank;
    }

    public static Map<String, Integer> getActivitiesPerformed(SQLiteDatabase sqLiteDatabase, Metric metric, String dateMetric) {
        Map<String, Integer> activities = new HashMap<>();
        Cursor activityCursor = sqLiteDatabase.rawQuery("SELECT " + EXERCISES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit, SUM(beers), strftime('" + metric.getDateTimePattern() + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " LEFT JOIN " + EXERCISES_TABLE + " ON " + ACTIVITIES_TABLE + ".exercise = " + EXERCISES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITIES_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE date = '" + dateMetric + "' AND " + ACTIVITIES_TABLE + ".exercise != 0 GROUP BY " + EXERCISES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit, date", null);
        if (activityCursor != null) {
            if (activityCursor.getCount() > 0) {
                activityCursor.moveToFirst();
                while (!activityCursor.isAfterLast()) {
                    activities.put(activityCursor.getString(0) + " for " + activityCursor.getDouble(1) + " " + activityCursor.getString(2), activityCursor.getInt(3));
                    activityCursor.moveToNext();
                }
            }
            activityCursor.close();
        }
        return activities;
    }
}
