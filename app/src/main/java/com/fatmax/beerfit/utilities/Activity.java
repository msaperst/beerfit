package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.INSERT_INTO;
import static com.fatmax.beerfit.utilities.Database.VALUES;
import static com.fatmax.beerfit.utilities.Database.WHERE_ID;

public class Activity {

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);
    public static final SimpleDateFormat FULL_DATE_TIME_FORMAT = new SimpleDateFormat("EEE, MMM d yyyy, kk:mm", Locale.US);


    private final SQLiteDatabase sqLiteDatabase;
    private int id = -1;
    private Date time = null;
    private Exercise exercise = null;
    private Measurement measurement = null;
    private double amount;
    private double beers;

    public Activity(SQLiteDatabase sqLiteDatabase, int id) {
        this.sqLiteDatabase = sqLiteDatabase;
        if (id == 0) { // beer case
            this.time = new Date();
            this.exercise = new Exercise(sqLiteDatabase, 0);
            this.measurement = new Measurement(sqLiteDatabase, 0);
            this.amount = 1;
            this.beers = -1;
            return;
        }
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

    public Activity(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public int getId() {
        return id;
    }

    public Date getDateTime() {
        return time;
    }

    public void setDateTime(Date time) {
        this.time = time;
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

    public String getStringDateTime() {
        if (time == null) {
            return null;
        } else {
            return FULL_DATE_TIME_FORMAT.format(time);
        }
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBeers() {
        return beers;
    }

    public void setBeers(Double beers) {
        this.beers = beers;
    }

    public boolean isDrankBeer() {
        return exercise.getId() == 0 && measurement.getId() == 0;
    }

    public String getString() {
        String conjunction = " ";
        if( !isDrankBeer() ) {
            conjunction = " for ";
        }
        return exercise.getPast() + conjunction + amount + " " + Elements.getProperStringPluralization(measurement.getUnit(), amount);
    }

    public void calculateBeers() {
        if( isDrankBeer() ) {
            beers = amount * -1;
            return;
        }
        Goal goal = new Database(sqLiteDatabase).getMatchingGoals(exercise, measurement);
        if (goal == null) {
            beers = 0;
        } else {
            beers = amount / goal.getAmount() * goal.getMeasurement().getConversion() / measurement.getConversion();
        }
    }

    public void save() {
        if (id == -1) { // create a new one
            sqLiteDatabase.execSQL(INSERT_INTO + ACTIVITIES_TABLE + VALUES + "null, '" + DATE_TIME_FORMAT.format(time) + "', " +
                    exercise.getId() + ", " + measurement.getId() + ", " + amount + ", " + beers + ");");
        } else {
            sqLiteDatabase.execSQL("UPDATE " + ACTIVITIES_TABLE + " SET time = '" + DATE_TIME_FORMAT.format(time) + "', exercise = " +
                    exercise.getId() + ", measurement = " + measurement.getId() + ", amount = " + amount + ", beers = " + beers + " WHERE id = " + id);
        }
    }

    public void delete() {
        sqLiteDatabase.execSQL("DELETE FROM " + ACTIVITIES_TABLE + WHERE_ID + id);
    }
}
