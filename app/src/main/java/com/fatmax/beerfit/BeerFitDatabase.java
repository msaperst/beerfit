package com.fatmax.beerfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

class BeerFitDatabase {

    private SQLiteDatabase database;

    BeerFitDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    void setupDatabase() {
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

    boolean isTableMissing(String tableName) {
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

    String getColumnType(String table, String column) {
        Cursor cursor = database.rawQuery("SELECT typeof(" + column + ") FROM " + table, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String columnType = cursor.getString(0);
                cursor.close();
                return columnType;
            }
        }
        throw new SQLiteException("No data in the column to check");
    }

    Object getTableValue(Cursor cursor, String table, String column) {
        switch (getColumnType(table, column).toLowerCase()) {
            case "integer":
                return cursor.getInt(cursor.getColumnIndex(column));
            case "real":
                return cursor.getDouble(cursor.getColumnIndex(column));
            case "blob":
                return cursor.getBlob(cursor.getColumnIndex(column));
            case "text":
            default:
                return cursor.getString(cursor.getColumnIndex(column));
        }
    }

    ArrayList<Object> getFullColumn(String table, String column) {
        ArrayList<Object> array_list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + table, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    array_list.add(getTableValue(cursor, table, column));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return array_list;
    }

    int getOrdinal(String table, String column, String lookup) {
        int ordinal = -1;
        Cursor cursor = database.rawQuery("SELECT id FROM " + table + " WHERE " + column + " = " + "'" + lookup + "';", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    ordinal = cursor.getInt(0);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return ordinal;
    }

    void logActivity(String time, String activity, String units, double duration) {
        logActivity(null, time, activity, units, duration);
    }

    void logActivity(String id, String time, String activity, String units, double duration) {
        database.execSQL("INSERT INTO ActivityLog VALUES(" + id + ", '" + time + "', " +
                getOrdinal("Activities", "type", activity) + ", " +
                getOrdinal("Measurements", "unit", units) + ", " + duration + ");");
    }

    void logBeer() {
        database.execSQL("INSERT INTO ActivityLog VALUES(null,datetime('now', 'localtime'), 0, 0, 1);");
    }

    void removeActivity(int id) {
        database.execSQL("DELETE FROM ActivityLog WHERE id = '" + id + "';");
    }

    String getActivityTime(int id) {
        String time = "Unknown";
        Cursor cursor = database.rawQuery("SELECT time FROM ActivityLog WHERE id = '" + id + "';", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    time = cursor.getString(0);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return time;
    }

    int getBeersDrank() {
        Cursor cursor = database.rawQuery("SELECT amount FROM ActivityLog WHERE activity = 0;", null);
        int beers = cursor.getCount();
        cursor.close();
        return beers;
    }

    double getBeersEarned() {
        double beersEarned = 0;
        Cursor res = database.rawQuery("SELECT * FROM Goals;", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            Cursor cur = database.rawQuery("SELECT SUM(amount) FROM ActivityLog WHERE activity = " + res.getInt(1) + " AND measurement = " + res.getInt(2), null);
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                beersEarned += cur.getDouble(0) / res.getDouble(3);
                cur.moveToNext();
            }
            cur.close();
            res.moveToNext();
        }
        res.close();
        return beersEarned;
    }

    int getBeersRemaining() {
        return (int) getBeersEarned() - getBeersDrank();
    }
}
