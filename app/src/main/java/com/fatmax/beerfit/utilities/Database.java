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
    public static final String ACTIVITIES_TABLE = "Activities";
    static final String INSERT_INTO = "INSERT INTO ";
    static final String WHERE_ID = " WHERE id = ";
    static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    private static final String VALUES = " VALUES(";
    private SQLiteDatabase database;

    public Database(SQLiteDatabase database) {
        this.database = database;
    }

    public void setupDatabase() {
        ////// MEASUREMENTS TABLE
        if (isTableMissing(MEASUREMENTS_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + MEASUREMENTS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, unit VARCHAR, conversion NUMBER);");
            populateMeasurementsTable();
        }
        // migration from old schema that didn't have conversion column
        if (!doesTableHaveColumn(MEASUREMENTS_TABLE, "conversion")) {
            // add our missing column
            database.execSQL("ALTER TABLE " + MEASUREMENTS_TABLE + " ADD conversion NUMBER;");
            // update our table to match
            database.execSQL("DELETE FROM " + MEASUREMENTS_TABLE);
            database.execSQL("VACUUM");
            populateMeasurementsTable();
        }

        ////// EXERCISES TABLE
        if (isTableMissing(EXERCISES_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + EXERCISES_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, past VARCHAR, current VARCHAR, color NUMBER);");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(1,'Walked','Walk'," + Color.GREEN + ");");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(2,'Ran','Run'," + Color.BLUE + ");");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(3,'Cycled','Cycle'," + Color.RED + ");");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(4,'Lifted','Lift'," + Color.MAGENTA + ");");
            database.execSQL(INSERT_INTO + EXERCISES_TABLE + " VALUES(5,'Played Soccer','Play Soccer'," + Color.DKGRAY + ");");
        }

        ////// GOALS TABLE
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
        }

        ////// ACTIVITIES TABLE
        // migration from old table name to new one
        if (isTableMissing(ACTIVITIES_TABLE) && !isTableMissing("ActivityLog")) {
            database.execSQL("ALTER TABLE ActivityLog RENAME TO " + ACTIVITIES_TABLE);
        }
        if (isTableMissing(ACTIVITIES_TABLE)) {
            database.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITIES_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, exercise INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        } else if (!doesTableHaveColumn(ACTIVITIES_TABLE, "exercise") && doesTableHaveColumn(ACTIVITIES_TABLE, "activity")) {
            // migration from old schema that had column activity, now renaming it to exercise
            Map<String, String> newGoalsTable = new LinkedHashMap<>();
            newGoalsTable.put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
            newGoalsTable.put("time", "TEXT");
            newGoalsTable.put("exercise", "INTEGER");
            newGoalsTable.put("measurement", "INTEGER");
            newGoalsTable.put("amount", "NUMBER");
            newGoalsTable.put("beers", "NUMBER");
            renameColumn(ACTIVITIES_TABLE, newGoalsTable);
        }
    }

    private void populateMeasurementsTable() {
        database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(3,'time','second', 3600);");
        database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(1,'time','minute', 60);");
        database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(4,'time','hour', 1);");
        database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(2,'distance','kilometer', 1);");
        database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(5,'distance','mile', 0.6213712);");
        database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(6,null,'class', -1);");
        database.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(7,null,'repetition', -1);");
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
        database.execSQL("INSERT INTO TMP(" + columns + ") SELECT " + String.join(",", getColumns(table)) + " FROM " + table);
        database.execSQL("DROP TABLE " + table);
        database.execSQL("ALTER TABLE TMP RENAME TO " + table);
        database.execSQL("COMMIT;");
    }

    public void logActivity(String time, String exercise, String unit, double duration) {
        logActivity(null, time, exercise, unit, duration);
    }

    public void logActivity(String id, String time, String past, String unit, double duration) {
        Exercise exercise = new Exercise(database, past);
        Measurement measurement = new Measurement(database, unit);
        database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + VALUES + id + ", '" + time + "', " +
                exercise.getId() + ", " + measurement.getId() + ", " + duration + ", " + getBeersEarned(exercise, measurement, duration) + ");");
    }

    public void logBeer() {
        logBeer(null, "datetime('now', 'localtime')", 1);
    }

    public void logBeer(String id, String time, int beers) {
        database.execSQL(INSERT_INTO + ACTIVITIES_TABLE + VALUES + id + ", " + time + ", 0, 0, " + beers + ", -" + beers + ");");
    }

    public void removeActivity(int id) {
        database.execSQL("DELETE FROM " + ACTIVITIES_TABLE + WHERE_ID + id);
    }

    public String getActivityTime(int id) {
        String time = "Unknown";
        Cursor cursor = database.rawQuery("SELECT time FROM " + ACTIVITIES_TABLE + WHERE_ID + id, null);
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
        Cursor cursor = database.rawQuery("SELECT SUM(amount) FROM " + ACTIVITIES_TABLE + " WHERE exercise = 0;", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                beersDrank = cursor.getInt(0);
            }
            cursor.close();
        }
        return beersDrank;
    }

    List<Measurement> getMatchingMeasurements(Measurement measurement) {
        List<Measurement> measurements = new ArrayList<>();
        if (measurement.getType() == null) {
            measurements.add(measurement);
        } else {
            Cursor cursor = database.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = '" + measurement.getType() + "';", null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        measurements.add(new Measurement(database, cursor.getInt(0)));
                        cursor.moveToNext();
                    }
                }
                cursor.close();
            }
        }
        return measurements;
    }

    Goal getMatchingGoals(Exercise exercise, Measurement measurement) {
        Goal goal = null;
        // get all matching measurements
        List<Measurement> measurements = getMatchingMeasurements(measurement);
        for (Measurement meas : measurements) {
            Cursor cursor = database.rawQuery("SELECT id FROM " + GOALS_TABLE + " WHERE exercise = " + exercise.getId() + " AND measurement = " + meas.getId(), null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        goal = new Goal(database, cursor.getInt(0));
                        cursor.moveToNext();
                    }
                }
                cursor.close();
            }
        }
        return goal;
    }

    double getBeersEarned(Exercise exercise, Measurement measurement, double duration) {
        Goal goal = getMatchingGoals(exercise, measurement);
        if (goal == null) {
            return 0;
        }
        return duration / goal.getAmount() * goal.getMeasurement().getConversion() / measurement.getConversion();
    }

    double getTotalBeersEarned() {
        double beersEarned = 0;
        Cursor cursor = database.rawQuery("SELECT SUM(beers) FROM " + ACTIVITIES_TABLE + " WHERE exercise != 0;", null);
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
}
