package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.INSERT_INTO;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static com.fatmax.beerfit.utilities.Database.WHERE_ID;

public class Measurement {

    private final SQLiteDatabase sqLiteDatabase;
    private int id = -1;
    private String type = null;
    private String unit = null;
    private double conversion = -1;

    public Measurement(SQLiteDatabase sqLiteDatabase, String unit) {
        this.sqLiteDatabase = sqLiteDatabase;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = '" + unit + "';", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                this.id = cursor.getInt(0);
                this.type = cursor.getString(1);
                this.unit = cursor.getString(2);
                this.conversion = cursor.getDouble(3);
            }
            cursor.close();
        }
    }

    public Measurement(SQLiteDatabase sqLiteDatabase, int id) {
        this.sqLiteDatabase = sqLiteDatabase;
        if (id == 0) { // beer case
            this.id = 0;
            this.unit = "beer";
            return;
        }
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = " + id, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                this.id = cursor.getInt(0);
                this.type = cursor.getString(1);
                this.unit = cursor.getString(2);
                this.conversion = cursor.getDouble(3);
            }
            cursor.close();
        }
    }

    public Measurement(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getConversion() {
        return conversion;
    }

    boolean isUnique() {
        boolean isUnique = true;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = '" + unit + "' AND id != " + id, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isUnique = false;
            }
            cursor.close();
        }
        return isUnique;
    }

    public boolean safeToEdit() {
        return (type == null);
    }

    public void save() {
        if (id == -1) { // create a new one
            sqLiteDatabase.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(null, null,'" + unit + "', -1);");
        } else if (safeToEdit()) {
            sqLiteDatabase.execSQL("UPDATE " + MEASUREMENTS_TABLE + " SET unit = '" + unit + "' WHERE id = " + id);
        }
    }

    public boolean safeToDelete() {
        boolean isSafe = true;
        Cursor goalsCheck = sqLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE measurement = " + id, null);
        if (goalsCheck != null) {
            if (goalsCheck.getCount() > 0) {
                isSafe = false;
            }
            goalsCheck.close();
        }
        Cursor activitiesCheck = sqLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE measurement = " + id, null);
        if (activitiesCheck != null) {
            if (activitiesCheck.getCount() > 0) {
                isSafe = false;
            }
            activitiesCheck.close();
        }
        return isSafe;
    }

    public void delete() {
        if (!safeToDelete()) {
            return;
        }
        sqLiteDatabase.execSQL("DELETE FROM " + MEASUREMENTS_TABLE + WHERE_ID + id);
    }
}
