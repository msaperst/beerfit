package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.Random;

import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.INSERT_INTO;
import static com.fatmax.beerfit.utilities.Database.WHERE_ID;

public class Exercise {
    private final SQLiteDatabase sqLiteDatabase;
    private Random rnd = new Random();
    private int id = 0;
    private String past = null;
    private String current = null;
    private int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

    public Exercise(SQLiteDatabase sqLiteDatabase, int id) {
        this.sqLiteDatabase = sqLiteDatabase;
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

    public int getId() {
        return id;
    }

    public String getPast() {
        return past;
    }

    public String getCurrent() {
        return current;
    }

    public int getColor() {
        return color;
    }
}
