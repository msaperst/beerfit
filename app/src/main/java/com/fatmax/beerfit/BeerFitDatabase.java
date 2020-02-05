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
//        database.execSQL("DROP TABLE StashedBeers");
//        database.execSQL("DROP TABLE Activities");
//        database.execSQL("DROP TABLE Goals");
//        database.execSQL("DROP TABLE Measurements");

        if (isTableMissing("StashedBeers")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS StashedBeers(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, beers NUMBER);");
        }
        if (isTableMissing("Measurements")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Measurements(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, unit VARCHAR);");
            database.execSQL("INSERT INTO Measurements VALUES(1,'time','minutes');");
            database.execSQL("INSERT INTO Measurements VALUES(2,'distance','kilometers');");
        }
        if (isTableMissing("Activities")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Activities(id INTEGER PRIMARY KEY AUTOINCREMENT, past VARCHAR, current VARCHAR);");
            database.execSQL("INSERT INTO Activities VALUES(1,'Walked','Walk');");
            database.execSQL("INSERT INTO Activities VALUES(2,'Ran','Run');");
            database.execSQL("INSERT INTO Activities VALUES(3,'Cycled','Cycle');");
            database.execSQL("INSERT INTO Activities VALUES(4,'Lifted','Lift');");
            database.execSQL("INSERT INTO Activities VALUES(5,'Played Soccer','Play Soccer');");
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
            String columnType = null;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                columnType = cursor.getString(0);
            }
            cursor.close();
            return columnType;
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
                getOrdinal("Activities", "past", activity) + ", " +
                getOrdinal("Measurements", "unit", units) + ", " + duration + ");");
    }

    void logBeer() {
        logBeer(null, "datetime('now', 'localtime')", 1);
    }

    void logBeer(String id, String time, int amount) {
        database.execSQL("INSERT INTO ActivityLog VALUES(" + id + ", " + time + ", 0, 0, " + amount + ");");
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

    /**
     * When the goals are updated (added, edited, or removed), it will mess with the current beer count.
     * To counter this, before goals are modified, the remaining beers are calculated, and stashed with a timestamp
     * Then recalculating will only pull activities after that timestamp
     */
    void stashBeersRemaining() {
        database.execSQL("INSERT INTO StashedBeers VALUES( null, datetime('now', 'localtime'), " + getBeersRemaining() + ");");
    }

    private String getBeersStashed(String column) {
        String stashedBeer = null;
        Cursor cursor = database.rawQuery("SELECT " + column + " FROM StashedBeers ORDER BY time DESC LIMIT 1;", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                stashedBeer = cursor.getString(0);
            }
            cursor.close();
        }
        return stashedBeer;
    }

    String getBeersStashedTime() {
        String stashedBeersTime = getBeersStashed("time");
        if (stashedBeersTime == null) {
            stashedBeersTime = "1900-01-01 00:00";
        }
        return stashedBeersTime;
    }

    double getBeersStashedCount() {
        String stashedBeersCount = getBeersStashed("beers");
        if (stashedBeersCount == null) {
            stashedBeersCount = "0";
        }
        return Double.valueOf(stashedBeersCount);
    }

    int getBeersRecentlyDrank() {
        String stashedBeerTime = getBeersStashedTime();
        int beersDrank = 0;
        Cursor cursor = database.rawQuery("SELECT SUM(amount) FROM ActivityLog WHERE activity = 0 AND time > Datetime('" + stashedBeerTime + "');", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                beersDrank = cursor.getInt(0);
            }
            cursor.close();
        }
        return beersDrank;
    }

    double getBeersRecentlyEarned() {
        String stashedBeerTime = getBeersStashedTime();
        double beersEarned = 0;
        Cursor goals = database.rawQuery("SELECT * FROM Goals;", null);
        if (goals != null) {
            if (goals.getCount() > 0) {
                goals.moveToFirst();
                while (!goals.isAfterLast()) {
                    int activity = goals.getInt(1);
                    int measurement = goals.getInt(2);
                    Cursor activities = database.rawQuery("SELECT SUM(amount) FROM ActivityLog WHERE activity = " + goals.getInt(1) + " AND measurement = " + goals.getInt(2) + " AND time > Datetime('" + stashedBeerTime + "');", null);
                    activities.moveToFirst();
                    while (!activities.isAfterLast()) {
                        beersEarned += activities.getDouble(0) / goals.getDouble(3);
                        activities.moveToNext();
                    }
                    activities.close();
                    goals.moveToNext();
                }
            }
            goals.close();
        }
        return beersEarned;
    }

    int getBeersRemaining() {
        return (int) getBeersStashedCount() + (int) getBeersRecentlyEarned() - getBeersRecentlyDrank();
    }

    void addGoal(String activity, String units, double duration) {
        addGoal(null, activity, units, duration);
    }

    void addGoal(String id, String activity, String units, double duration) {
        stashBeersRemaining();
        database.execSQL("INSERT INTO Goals VALUES(" + id + ", " +
                getOrdinal("Activities", "current", activity) + ", " +
                getOrdinal("Measurements", "unit", units) + ", " + duration + ");");
    }

    void removeGoal(int id) {
        stashBeersRemaining();
        database.execSQL("DELETE FROM Goals WHERE id = '" + id + "';");
    }
}
