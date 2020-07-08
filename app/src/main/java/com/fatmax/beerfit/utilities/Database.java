package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Database {

    public static final String MEASUREMENTS_TABLE = "Measurements";
    public static final String EXERCISES_TABLE = "Exercises";
    public static final String GOALS_TABLE = "Goals";
    public static final String ACTIVITY_LOG_TABLE = "ActivityLog";      //TODO - rename to Activities and do migration/upgrade if needed
    static final String INSERT_INTO = "INSERT INTO ";
    static final String WHERE_ID = " WHERE id = '";
    static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    private static final String VALUES = " VALUES(";
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
        if (isTableMissing(EXERCISES_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + EXERCISES_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, past VARCHAR, current VARCHAR, color NUMBER);");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(1,'Walked','Walk'," + Color.GREEN + ");");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(2,'Ran','Run'," + Color.BLUE + ");");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(3,'Cycled','Cycle'," + Color.RED + ");");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(4,'Lifted','Lift'," + Color.MAGENTA + ");");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(5,'Played Soccer','Play Soccer'," + Color.DKGRAY + ");");
        }
        if (isTableMissing(GOALS_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + GOALS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, exercise INTEGER, measurement INTEGER, amount NUMBER);");
        } else if (!doesTableHaveColumn(GOALS_TABLE, "exercise") && doesTableHaveColumn(GOALS_TABLE, "activity")) {
            // migration from old schema that had column activity, now renaming it to exercise
            Map<String, String> newGoalsTable = new LinkedHashMap<>();
            newGoalsTable.put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            newGoalsTable.put("exercise", "INTEGER");
            newGoalsTable.put("measurement", "INTEGER");
            newGoalsTable.put("amount", "NUMBER");
            renameColumn(GOALS_TABLE, newGoalsTable);
//            database.execSQL("BEGIN TRANSACTION;");
//            database.execSQL("CREATE TABLE TMP(id INTEGER PRIMARY KEY AUTOINCREMENT, exercise INTEGER, measurement INTEGER, amount NUMBER);");
//            database.execSQL("INSERT INTO TMP(id,exercise,measurement,amount) SELECT id,activity,measurement,amount FROM " + GOALS_TABLE + ";");
//            database.execSQL("DROP TABLE " + GOALS_TABLE + ";");
//            database.execSQL("ALTER TABLE TMP RENAME TO " + GOALS_TABLE + ";");
//            database.execSQL("COMMIT;");
        }
        if (isTableMissing(ACTIVITY_LOG_TABLE)) {
            //TODO - don't call it an activity, call it an exercise - need to upgrade it properly
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITY_LOG_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, exercise INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        } else if (!doesTableHaveColumn(ACTIVITY_LOG_TABLE, "exercise") && doesTableHaveColumn(ACTIVITY_LOG_TABLE, "activity")) {
            // migration from old schema that had column activity, now renaming it to exercise
            Map<String, String> newGoalsTable = new LinkedHashMap<>();
            newGoalsTable.put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            newGoalsTable.put("time", "TEXT");
            newGoalsTable.put("exercise", "INTEGER");
            newGoalsTable.put("measurement", "INTEGER");
            newGoalsTable.put("amount", "NUMBER");
            newGoalsTable.put("beers", "NUMBER");
            renameColumn(ACTIVITY_LOG_TABLE, newGoalsTable);
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

    boolean doesTableHaveColumn(String tableName, String column) {
        boolean isExist = false;
        Cursor cursor = database.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (column.equals(cursor.getString(1))) {
                        isExist = true;
                        break;
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return isExist;
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

    List<String> getColumns(String table) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = database.rawQuery("PRAGMA table_info(" + table + ")", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    columns.add(cursor.getString(1));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return columns;
    }

    void renameColumn(String table, Map<String, String> newTableColumns) {
        StringBuilder tableStructure = new StringBuilder();
        StringBuilder columns = new StringBuilder();
        String prefix = "";
        for (Map.Entry<String, String> newTableColumn : newTableColumns.entrySet()) {
            tableStructure.append(prefix).append(newTableColumn.getKey()).append(" ").append(newTableColumn.getValue());
            columns.append(prefix).append(newTableColumn.getKey());
            prefix = ", ";
        }
        database.execSQL("BEGIN TRANSACTION;");
        database.execSQL("CREATE TABLE TMP(" + tableStructure.toString() + ");");
        database.execSQL("INSERT INTO TMP(" + columns + ") SELECT " + String.join(",", getColumns(table)) + " FROM " + table + ";");
        database.execSQL("DROP TABLE " + table + ";");
        database.execSQL("ALTER TABLE TMP RENAME TO " + table + ";");
        database.execSQL("COMMIT;");
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

    int getExerciseColor(String exercise) {
        int color = Color.YELLOW;
        int exerciseId = getOrdinal(EXERCISES_TABLE, "past", exercise.split(" \\(")[0]);
        Cursor cursor = database.rawQuery("SELECT color FROM " + EXERCISES_TABLE + WHERE_ID + exerciseId + "';", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                color = cursor.getInt(0);
            }
            cursor.close();
        }
        return color;
    }

    public void logActivity(String time, String exercise, String units, double duration) {
        logActivity(null, time, exercise, units, duration);
    }

    public void logActivity(String id, String time, String exercise, String units, double duration) {
        int exerciseId = getOrdinal(EXERCISES_TABLE, "past", exercise);
        int measurementsId = getOrdinal(MEASUREMENTS_TABLE, "unit", units);
        database.execSQL(INSERT_INTO + ACTIVITY_LOG_TABLE + VALUES + id + ", '" + time + "', " +
                exerciseId + ", " + measurementsId + ", " + duration + ", " + getBeersEarned(exercise, units, duration) + ");");
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
        Cursor cursor = database.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise = 0;", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                beersDrank = cursor.getInt(0);
            }
            cursor.close();
        }
        return beersDrank;
    }

    double getBeersEarned(String exercise, String units, double duration) {
        int exerciseId = getOrdinal(EXERCISES_TABLE, "past", exercise);
        int measurementsId = getOrdinal(MEASUREMENTS_TABLE, "unit", units);
        double goalAmountForBeer = -1;
        Cursor goalResults = database.rawQuery("SELECT amount FROM " + GOALS_TABLE + " WHERE exercise = " + exerciseId + " AND measurement = " + measurementsId + ";", null);
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
        Cursor cursor = database.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise != 0;", null);
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

    public void addGoal(String exercise, String units, double duration) {
        addGoal(null, exercise, units, duration);
    }

    public void addGoal(String id, String exercise, String units, double duration) {
        database.execSQL(INSERT_INTO + GOALS_TABLE + VALUES + id + ", " +
                getOrdinal(EXERCISES_TABLE, "current", exercise) + ", " +
                getOrdinal(MEASUREMENTS_TABLE, "unit", units) + ", " + duration + ");");
    }

    public void removeGoal(int id) {
        database.execSQL("DELETE FROM " + GOALS_TABLE + WHERE_ID + id + "';");
    }
}
