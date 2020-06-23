package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Database {

    public static final String MEASUREMENTS_TABLE = "Measurements";
    public static final String ACTIVITIES_TABLE = "Activities";
    public static final String GOALS_TABLE = "Goals";
    public static final String ACTIVITY_LOG_TABLE = "ActivityLog";
    private static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String VALUES = " VALUES(";
    private static final String WHERE_ID = " WHERE id = '";
    private SQLiteDatabase database;

    public Database(SQLiteDatabase database) {
        this.database = database;
    }

    public void setupDatabase() {
        if (isTableMissing(MEASUREMENTS_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + MEASUREMENTS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, unit VARCHAR);");
            database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(1,'time','minutes');");
            database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(2,'distance','kilometers');");
        }
        if (isTableMissing(ACTIVITIES_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITIES_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, past VARCHAR, current VARCHAR, color NUMBER);");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(1,'Walked','Walk'," + Color.GREEN + ");");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(2,'Ran','Run'," + Color.BLUE + ");");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(3,'Cycled','Cycle'," + Color.RED + ");");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(4,'Lifted','Lift'," + Color.MAGENTA + ");");
            database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + " VALUES(5,'Played Soccer','Play Soccer'," + Color.DKGRAY + ");");
        }
        if (isTableMissing(GOALS_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + GOALS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, activity INTEGER, measurement INTEGER, amount NUMBER);");
        }
        if (isTableMissing(ACTIVITY_LOG_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITY_LOG_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, activity INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
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

    boolean doesDataExist(String tableName, int id) {
        boolean isExist = false;
        if (isTableMissing(tableName)) {
            return false;
        }
        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE id = " + id, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    String getColumnType(String table, String column) {
        Cursor cursor = database.rawQuery("PRAGMA table_info(" + table + ")", null);
        if (cursor != null) {
            String columnType = null;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (column.equalsIgnoreCase(cursor.getString(1))) {
                        columnType = cursor.getString(2);
                        break;
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
            if (columnType != null) {
                return columnType;
            }
            throw new SQLiteException("No column exists");
        }
        throw new SQLiteException("No table to check");
    }

    Object getTableValue(Cursor cursor, String table, String column) {
        switch (getColumnType(table, column)) {
            case "INTEGER":
                return cursor.getInt(cursor.getColumnIndex(column));
            case "NUMBER":
                return cursor.getDouble(cursor.getColumnIndex(column));
            default:
                return cursor.getString(cursor.getColumnIndex(column));
        }
    }

    public List<Object> getFullColumn(String table, String column) {
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
        Cursor cursor = database.rawQuery("SELECT id FROM " + table + " WHERE " + column + " = '" + lookup + "';", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                ordinal = cursor.getInt(0);
            }
            cursor.close();
        }
        return ordinal;
    }

    int getActivityColor(String activity) {
        int color = Color.YELLOW;
        int activityId = getOrdinal(ACTIVITIES_TABLE, "past", activity.split(" \\(")[0]);
        Cursor cursor = database.rawQuery("SELECT color FROM " + ACTIVITIES_TABLE + WHERE_ID + activityId + "';", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                color = cursor.getInt(0);
            }
            cursor.close();
        }
        return color;
    }

    public void logActivity(String time, String activity, String units, double duration) {
        logActivity(null, time, activity, units, duration);
    }

    public void logActivity(String id, String time, String activity, String units, double duration) {
        int activityId = getOrdinal(ACTIVITIES_TABLE, "past", activity);
        int measurementsId = getOrdinal(MEASUREMENTS_TABLE, "unit", units);
        database.execSQL(INSERT_INTO + ACTIVITY_LOG_TABLE + VALUES + id + ", '" + time + "', " +
                activityId + ", " + measurementsId + ", " + duration + ", " + getBeersEarned(activity, units, duration) + ");");
    }

    public void logBeer() {
        logBeer(null, "datetime('now', 'localtime')", 1);
    }

    public void logBeer(String id, String time, int beers) {
        database.execSQL(INSERT_INTO + ACTIVITY_LOG_TABLE + VALUES + id + ", " + time + ", 0, 0, " + beers + ", -" + beers + ");");
    }

    public void removeActivity(int id) {
        database.execSQL("DELETE FROM " + ACTIVITY_LOG_TABLE + WHERE_ID + id + "';");
    }

    public String getActivityTime(int id) {
        String time = "Unknown";
        Cursor cursor = database.rawQuery("SELECT time FROM " + ACTIVITY_LOG_TABLE + WHERE_ID + id + "';", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                time = cursor.getString(0);
            }
            cursor.close();
        }
        return time;
    }

    int getBeersDrank() {
        int beersDrank = 0;
        Cursor cursor = database.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 0;", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                beersDrank = cursor.getInt(0);
            }
            cursor.close();
        }
        return beersDrank;
    }

    double getBeersEarned(String activity, String units, double duration) {
        int activityId = getOrdinal(ACTIVITIES_TABLE, "past", activity);
        int measurementsId = getOrdinal(MEASUREMENTS_TABLE, "unit", units);
        double goalAmountForBeer = -1;
        Cursor goalResults = database.rawQuery("SELECT amount FROM " + GOALS_TABLE + " WHERE activity = " + activityId + " AND measurement = " + measurementsId + ";", null);
        if (goalResults != null) {
            if (goalResults.getCount() > 0) {
                goalResults.moveToFirst();
                goalAmountForBeer = goalResults.getDouble(0);
            }
            goalResults.close();
        }
        if (goalAmountForBeer == -1) {
            return 0;
        }
        return duration / goalAmountForBeer;
    }

    double getTotalBeersEarned() {
        double beersEarned = 0;
        Cursor cursor = database.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity != 0;", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                beersEarned = cursor.getDouble(0);
            }
            cursor.close();
        }
        return beersEarned;
    }

    public int getBeersRemaining() {
        return (int) getTotalBeersEarned() - getBeersDrank();
    }

    public void addGoal(String activity, String units, double duration) {
        addGoal(null, activity, units, duration);
    }

    public void addGoal(String id, String activity, String units, double duration) {
        database.execSQL(INSERT_INTO + GOALS_TABLE + VALUES + id + ", " +
                getOrdinal(ACTIVITIES_TABLE, "current", activity) + ", " +
                getOrdinal(MEASUREMENTS_TABLE, "unit", units) + ", " + duration + ");");
    }

    public void removeGoal(int id) {
        database.execSQL("DELETE FROM " + GOALS_TABLE + WHERE_ID + id + "';");
    }
}
