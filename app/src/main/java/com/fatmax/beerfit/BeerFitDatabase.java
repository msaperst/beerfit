package com.fatmax.beerfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

class BeerFitDatabase {

    private static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String VALUES = " VALUES(";
    private static final String WHERE_ID = " WHERE id = '";

    static final String STASHED_BEERS_TABLE = "StashedBeers";
    static final String MEASUREMENTS_TABLE = "Measurements";
    static final String ACTIVITIES_TABLE = "Activities";
    static final String GOALS_TABLE = "Goals";
    static final String ACTIVITY_LOG_TABLE = "ActivityLog";

    private SQLiteDatabase database;

    BeerFitDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    void setupDatabase() {
//        database.execSQL("DROP TABLE " + STASHED_BEERS_TABLE );
//        database.execSQL("DROP TABLE " + ACTIVITIES_TABLE );
//        database.execSQL("DROP TABLE " + GOALS_TABLE );
//        database.execSQL("DROP TABLE " + MEASUREMENTS_TABLE );
//        database.execSQL("DROP TABLE " + ACTIVITY_LOG_TABLE );

        if (isTableMissing(STASHED_BEERS_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + STASHED_BEERS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, beers NUMBER);");
        }
        if (isTableMissing(MEASUREMENTS_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + MEASUREMENTS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, unit VARCHAR);");
            database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(1,'time','minutes');");
            database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(2,'distance','kilometers');");
        }
        if (isTableMissing(ACTIVITIES_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITIES_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, past VARCHAR, current VARCHAR);");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(1,'Walked','Walk');");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(2,'Ran','Run');");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(3,'Cycled','Cycle');");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(4,'Lifted','Lift');");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(5,'Played Soccer','Play Soccer');");
        }
        if (isTableMissing(GOALS_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + GOALS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, activity INTEGER, measurement INTEGER, amount NUMBER);");
            database.execSQL(INSERT_INTO + GOALS_TABLE + " VALUES(1,1,2,5);");
            database.execSQL(INSERT_INTO + GOALS_TABLE + " VALUES(2,2,2,5);");
            database.execSQL(INSERT_INTO + GOALS_TABLE + " VALUES(3,3,2,10);");
            database.execSQL(INSERT_INTO + GOALS_TABLE + " VALUES(4,4,1,30);");
            database.execSQL(INSERT_INTO + GOALS_TABLE + " VALUES(5,5,1,30);");
        }
        if (isTableMissing(ACTIVITY_LOG_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITY_LOG_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, activity INTEGER, measurement INTEGER, amount NUMBER);");
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
        ArrayList<Object> columnList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + table, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    columnList.add(getTableValue(cursor, table, column));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return columnList;
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
        database.execSQL(INSERT_INTO + ACTIVITY_LOG_TABLE + VALUES + id + ", '" + time + "', " +
                getOrdinal(ACTIVITIES_TABLE, "past", activity) + ", " +
                getOrdinal(MEASUREMENTS_TABLE, "unit", units) + ", " + duration + ");");
    }

    void logBeer() {
        logBeer(null, "datetime('now', 'localtime')", 1);
    }

    void logBeer(String id, String time, int amount) {
        database.execSQL(INSERT_INTO + ACTIVITY_LOG_TABLE + VALUES + id + ", " + time + ", 0, 0, " + amount + ");");
    }

    void removeActivity(int id) {
        database.execSQL("DELETE FROM " + ACTIVITY_LOG_TABLE + WHERE_ID + id + "';");
    }

    String getActivityTime(int id) {
        String time = "Unknown";
        Cursor cursor = database.rawQuery("SELECT time FROM " + ACTIVITY_LOG_TABLE + WHERE_ID + id + "';", null);
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
        database.execSQL(INSERT_INTO + STASHED_BEERS_TABLE + " VALUES( null, datetime('now', 'localtime'), " + getBeersRemaining() + ");");
    }

    private String getBeersStashed(String column) {
        String stashedBeer = null;
        Cursor cursor = database.rawQuery("SELECT " + column + " FROM " + STASHED_BEERS_TABLE + " ORDER BY time DESC LIMIT 1;", null);
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
        Cursor cursor = database.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 0 AND time > Datetime('" + stashedBeerTime + "');", null);
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
        Cursor goals = database.rawQuery("SELECT * FROM " + GOALS_TABLE + ";", null);
        if (goals != null) {
            if (goals.getCount() > 0) {
                goals.moveToFirst();
                while (!goals.isAfterLast()) {
                    Cursor activities = database.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = " + goals.getInt(1) + " AND measurement = " + goals.getInt(2) + " AND time > Datetime('" + stashedBeerTime + "');", null);
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
        database.execSQL(INSERT_INTO + GOALS_TABLE + VALUES + id + ", " +
                getOrdinal(ACTIVITIES_TABLE, "current", activity) + ", " +
                getOrdinal(MEASUREMENTS_TABLE, "unit", units) + ", " + duration + ");");
    }

    void removeGoal(int id) {
        stashBeersRemaining();
        database.execSQL("DELETE FROM " + GOALS_TABLE + WHERE_ID + id + "';");
    }
}
