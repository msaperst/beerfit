package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;

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
}
