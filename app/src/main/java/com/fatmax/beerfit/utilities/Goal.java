package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.INSERT_INTO;
import static com.fatmax.beerfit.utilities.Database.WHERE_ID;

public class Goal {

    private final SQLiteDatabase sqLiteDatabase;
    private int id = -1;
    private Exercise exercise = null;
    private Measurement measurement = null;
    private double amount;

    public Goal(SQLiteDatabase sqLiteDatabase, int id) {
        this.sqLiteDatabase = sqLiteDatabase;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE id = " + id, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                this.id = cursor.getInt(0);
                this.exercise = new Exercise(sqLiteDatabase, cursor.getInt(1));
                this.measurement = new Measurement(sqLiteDatabase, cursor.getInt(2));
                this.amount = cursor.getDouble(3);
            }
            cursor.close();
        }
    }

    public Goal(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public int getId() {
        return id;
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

    public String getString() {
        return exercise.getCurrent() + " for " + amount + " " + Elements.getProperStringPluralization(measurement.getUnit(), amount);
    }

    public void save() {
        if (id == -1) { // create a new one
            sqLiteDatabase.execSQL(INSERT_INTO + GOALS_TABLE + " VALUES(null," + exercise.getId() + "," + measurement.getId() + "," + amount + ");");
        } else {
            sqLiteDatabase.execSQL("UPDATE " + GOALS_TABLE + " SET exercise = " + exercise.getId() + ", measurement = " + measurement.getId() + ", amount = " + amount + " WHERE id = " + id);
        }
    }

    public void delete() {
        sqLiteDatabase.execSQL("DELETE FROM " + GOALS_TABLE + WHERE_ID + id);
    }
}
