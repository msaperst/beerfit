package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.Random;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.INSERT_INTO;
import static com.fatmax.beerfit.utilities.Database.WHERE_ID;

public class Activity {
    private final SQLiteDatabase sqLiteDatabase;
    private Random rnd = new Random();
    private int id = 0;
    private String past = null;
    private String current = null;
    private int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

    public Activity(SQLiteDatabase sqLiteDatabase, String current) {
        this.sqLiteDatabase = sqLiteDatabase;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = '" + current + "';", null);
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

    public Activity(SQLiteDatabase sqLiteDatabase) {
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

    public boolean isActivityUnique() {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = '" + current + "' AND id != " + id + ";", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                return false;
            }
            cursor.close();
        }
        return true;
    }

    public boolean isColorUnique() {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE color = '" + color + "' AND id != " + id + ";", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                return false;
            }
            cursor.close();
        }
        return true;
    }

    public void saveActivity() {
        if (id == 0) { // create a new one
            sqLiteDatabase.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(null,'" + past + "','" + current + "'," + color + ");");
        } else {
            sqLiteDatabase.execSQL("UPDATE " + ACTIVITIES_TABLE + " SET past = '" + past + "', current = '" + current + "', color = '" + color + "' WHERE id = " + id + ";");
        }
    }

    public boolean safeToDelete() {
        Cursor goalsCheck = sqLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE activity = " + id + ";", null);
        if (goalsCheck != null) {
            if (goalsCheck.getCount() > 0) {
                return false;
            }
            goalsCheck.close();
        }
        Cursor activitiesCheck = sqLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = " + id + ";", null);
        if (activitiesCheck != null) {
            if (activitiesCheck.getCount() > 0) {
                return false;
            }
            activitiesCheck.close();
        }
        return true;
    }

    public void deleteActivity() {
        if( !safeToDelete()) {
            return;
        }
        sqLiteDatabase.execSQL("DELETE FROM " + ACTIVITIES_TABLE + WHERE_ID + id + "';");
    }
}
