package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;

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

    public int getId() {
        return id;
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
}
