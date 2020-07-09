package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class Measurement {

    private final SQLiteDatabase sqLiteDatabase;
    private int id = 0;
    private String type = null;
    private String unit = null;

    public Measurement(SQLiteDatabase sqLiteDatabase, int id) {
        this.sqLiteDatabase = sqLiteDatabase;
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = " + id, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                this.id = cursor.getInt(0);
                this.type = cursor.getString(1);
                this.unit = cursor.getString(2);
            }
            cursor.close();
        }
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


}
