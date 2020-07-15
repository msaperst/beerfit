package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.Random;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.INSERT_INTO;
import static com.fatmax.beerfit.utilities.Database.WHERE_ID;

public class Exercise {
    private final SQLiteDatabase sqLiteDatabase;
    private Random rnd = new Random();
    private int id = -1;
    private String past = null;
    private String current = null;
    private int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

    public Exercise(SQLiteDatabase sqLiteDatabase, String action) {
        this.sqLiteDatabase = sqLiteDatabase;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = '" + action + "' OR past = '" + action + "';", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                this.id = cursor.getInt(0);
                this.past = cursor.getString(1);
                this.current = cursor.getString(2);
                this.color = cursor.getInt(3);
            }
            cursor.close();
        }
    }

    public Exercise(SQLiteDatabase sqLiteDatabase, int id) {
        this.sqLiteDatabase = sqLiteDatabase;
        if (id == 0) { // beer case
            this.id = 0;
            this.past = "Drank";
            this.current = "Drink";
            this.color = Color.YELLOW;
            return;
        }
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = " + id, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                this.id = cursor.getInt(0);
                this.past = cursor.getString(1);
                this.current = cursor.getString(2);
                this.color = cursor.getInt(3);
            }
            cursor.close();
        }
    }

    public Exercise(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public int getId() {
        return id;
    }

    public String getPast() {
        return past;
    }

    public void setPast(String past) {
        this.past = past;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isCurrentUnique() {
        return isUnique("current", current);
    }

    public boolean isPastUnique() {
        return isUnique("past", past);
    }

    public boolean isColorUnique() {
        return isUnique("color", color);
    }

    private boolean isUnique(String query, Object object) {
        boolean isUnique = true;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE " + query + " = '" + object + "' AND id != " + id + ";", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isUnique = false;
            }
            cursor.close();
        }
        return isUnique;
    }

    public void save() {
        if (id == -1) { // create a new one
            sqLiteDatabase.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(null,'" + past + "','" + current + "'," + color + ");");
        } else {
            sqLiteDatabase.execSQL("UPDATE " + EXERCISES_TABLE + " SET past = '" + past + "', current = '" + current + "', color = '" + color + "' WHERE id = " + id + ";");
        }
    }

    public boolean safeToDelete() {
        boolean isSafe = true;
        Cursor goalsCheck = sqLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE exercise = " + id + ";", null);
        if (goalsCheck != null) {
            if (goalsCheck.getCount() > 0) {
                isSafe = false;
            }
            goalsCheck.close();
        }
        Cursor activitiesCheck = sqLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE exercise = " + id + ";", null);
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
        sqLiteDatabase.execSQL("DELETE FROM " + EXERCISES_TABLE + WHERE_ID + id + "';");
    }
}
