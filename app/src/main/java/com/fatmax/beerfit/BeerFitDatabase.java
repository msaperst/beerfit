package com.fatmax.beerfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class BeerFitDatabase {

    SQLiteDatabase database;

    BeerFitDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    void setupDatabase() {
        // while i'm working on things, we'll need to wipe out all of these
        database.execSQL("DROP TABLE IF EXISTS Measurements");
        database.execSQL("DROP TABLE IF EXISTS Activities");
        database.execSQL("DROP TABLE IF EXISTS Goals");

        if (isTableMissing("Measurements")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Measurements(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, unit VARCHAR);");
            database.execSQL("INSERT INTO Measurements VALUES(1,'time','minutes');");
            database.execSQL("INSERT INTO Measurements VALUES(2,'distance','kilometers');");
        }
        if (isTableMissing("Activities")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Activities(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR);");
            database.execSQL("INSERT INTO Activities VALUES(1,'Walked');");
            database.execSQL("INSERT INTO Activities VALUES(2,'Ran');");
            database.execSQL("INSERT INTO Activities VALUES(3,'Cycled');");
            database.execSQL("INSERT INTO Activities VALUES(4,'Lifted');");
            database.execSQL("INSERT INTO Activities VALUES(5,'Played Soccer');");
        }
        if (isTableMissing("Goals")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Goals(id INTEGER PRIMARY KEY AUTOINCREMENT, activity INTEGER, measurement INTEGER, amount NUMBER);");
            database.execSQL("INSERT INTO Goals VALUES(1,1,2,5);");
            database.execSQL("INSERT INTO Goals VALUES(2,2,2,5);");
            database.execSQL("INSERT INTO Goals VALUES(3,3,2,10);");
            database.execSQL("INSERT INTO Goals VALUES(4,4,1,30);");
            database.execSQL("INSERT INTO Goals VALUES(5,5,1,30);");
        }
        if (isTableMissing("ActivityLog")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS ActivityLog(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, activity INTEGER, measurement INTEGER, amount NUMBER);");
        }
    }

    private boolean isTableMissing(String tableName) {
        boolean isExist = false;
        Cursor cursor = database.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return !isExist;
    }

    ArrayList<String> getFullColumn(String table, String column) {
        ArrayList<String> array_list = new ArrayList<>();

        //hp = new HashMap();
        Cursor res = database.rawQuery("SELECT * FROM " + table, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(column)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    private int getOrdinal(String table, String column, String lookup) {
        int ordinal = -1;
        Cursor res = database.rawQuery("SELECT id FROM " + table + " WHERE " + column + " = " + "'" + lookup + "'", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            ordinal = res.getInt(0);
            res.moveToNext();
        }
        res.close();
        return ordinal;
    }

    void logActivity(String activity, String units, double duration) {
        database.execSQL("INSERT INTO ActivityLog VALUES(null,datetime('now', 'localtime')," +
                getOrdinal("Activities", "type", activity) + ", " +
                getOrdinal("Measurements", "unit", units) + ", " + duration + ");");
    }

    void logBeer() {
        database.execSQL("INSERT INTO ActivityLog VALUES(null,datetime('now', 'localtime'), 0, 0, 1);");
    }

    int getBeersRemaining() {
        return (int) getBeersEarned() - getBeersDrank();
    }

    int getBeersDrank() {
        Cursor cur = database.rawQuery("SELECT SUM(amount) FROM ActivityLog WHERE activity = 0;", null);
        if(cur.moveToFirst())
        {
            return cur.getInt(0);
        }
        return 0;
    }

    double getBeersEarned() {
        //TODO - need to fix this algorithm
        double beersEarned = 0;
        Cursor res = database.rawQuery("SELECT * FROM Goals;", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            Cursor cur = database.rawQuery("SELECT SUM(amount) FROM ActivityLog WHERE activity = " + res.getInt(1) + " AND measurement = " + res.getInt(2), null);
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                beersEarned += cur.getDouble(0);
                cur.moveToNext();
            }
            cur.close();
            res.moveToNext();
        }
        res.close();
        return beersEarned;
    }
}
