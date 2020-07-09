package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.fatmax.beerfit.AddActivityActivity.DATE_FORMAT;
import static com.fatmax.beerfit.AddActivityActivity.TIME_FORMAT;
import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;

public class Activity {

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    private final SQLiteDatabase sqLiteDatabase;
    private int id = 0;
    private Date time = null;
    private Exercise exercise = null;
    private Measurement measurement = null;
    private double amount;
    private double beers;

    public Activity(SQLiteDatabase sqLiteDatabase, int id) {
        this.sqLiteDatabase = sqLiteDatabase;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = " + id, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                this.id = cursor.getInt(0);
                try {
                    this.time = DATE_TIME_FORMAT.parse(cursor.getString(1));
                } catch (ParseException e) {
                    this.time = null;
                }

                this.exercise = new Exercise(sqLiteDatabase, cursor.getInt(2));
                this.measurement = new Measurement(sqLiteDatabase, cursor.getInt(3));
                this.amount = cursor.getDouble(4);
                this.beers = cursor.getDouble(5);
            }
            cursor.close();
        }
    }

    public int getId() {
        return id;
    }

    public Date getDateTime() {
        return time;
    }

    public String getDate() {
        if (time == null) {
            return null;
        } else {
            return DATE_FORMAT.format(time);
        }
    }

    public String getTime() {
        if (time == null) {
            return null;
        } else {
            return TIME_FORMAT.format(time);
        }
    }

    public Exercise getExercise() {
        return exercise;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public double getAmount() {
        return amount;
    }

    public double getBeers() {
        return beers;
    }
}
