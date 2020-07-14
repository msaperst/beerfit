package com.fatmax.beerfit.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.fatmax.beerfit.AddActivityActivity.DATE_FORMAT;
import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.CREATE_TABLE_IF_NOT_EXISTS;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.INSERT_INTO;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class DatabaseInstrumentedTest {

    private static final String DATABASE_NAME = "testDB";
    private static final String DATETIME_FORMAT = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}";

    static void wipeOutDB() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    static SQLiteDatabase getDB() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        return helper.getWritableDatabase();
    }

    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void setupDatabaseTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertFalse(database.isTableMissing(MEASUREMENTS_TABLE));
        assertFalse(database.isTableMissing(EXERCISES_TABLE));
        assertFalse(database.isTableMissing(GOALS_TABLE));
        assertFalse(database.isTableMissing(ACTIVITIES_TABLE));
        wipeOutDB();
    }

    @Test
    public void isTableMissingTrueTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertTrue(database.isTableMissing("myTable"));
    }

    @Test
    public void isTableMissingFalseTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS someTable(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR);");
        database.setupDatabase();
        assertFalse(database.isTableMissing("someTable"));
        wipeOutDB();
    }

    @Test
    public void doesTableHaveColumnTrueTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(EXERCISES_TABLE, "id"));
    }

    @Test
    public void doesTableHaveColumnFalseTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertFalse(database.doesTableHaveColumn(EXERCISES_TABLE, "ID"));
    }

    @Test
    public void doesTableHaveColumnNoTableTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertFalse(database.doesTableHaveColumn("someTable", "ID"));
    }

    @Test
    public void doesExistTrueTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertTrue(database.doesDataExist(EXERCISES_TABLE, 1));
    }

    @Test
    public void doesExistNoDBTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertFalse(database.doesDataExist("someTable", 1));
    }

    @Test
    public void doesExistFalseTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertFalse(database.doesDataExist(EXERCISES_TABLE, 6));
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoTableTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        // no table exists
        try {
            database.getColumnType("columnTypes", "t");
            fail();
        } finally {
            wipeOutDB();
        }
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoColumnTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        // column doesn't exist
        db.execSQL("CREATE TABLE columnTypes(t TEXT, v VARCHAR, i INTEGER, n NUMBER);");
        try {
            database.getColumnType("columnTypes", "x");
            fail();
        } finally {
            wipeOutDB();
        }
    }

    @Test
    public void getColumnTypeStringTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL("CREATE TABLE columnTypes(t TEXT, v VARCHAR, i INTEGER, n NUMBER);");
        db.execSQL("INSERT INTO columnTypes VALUES('500.0', '500.0', '500.0', '500.0');");
        assertEquals("TEXT", database.getColumnType("columnTypes", "t"));
        assertEquals("VARCHAR", database.getColumnType("columnTypes", "v"));
        assertEquals("INTEGER", database.getColumnType("columnTypes", "i"));
        assertEquals("NUMBER", database.getColumnType("columnTypes", "n"));
        wipeOutDB();
    }

    @Test
    public void getColumnTypeDoubleTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL("CREATE TABLE columnTypes(t TEXT, v VARCHAR, i INTEGER, n NUMBER);");
        db.execSQL("INSERT INTO columnTypes VALUES(500.0, 500.0, 500.0, 500.0);");
        assertEquals("TEXT", database.getColumnType("columnTypes", "t"));
        assertEquals("VARCHAR", database.getColumnType("columnTypes", "v"));
        assertEquals("INTEGER", database.getColumnType("columnTypes", "i"));
        assertEquals("NUMBER", database.getColumnType("columnTypes", "n"));
        wipeOutDB();
    }

    @Test
    public void getColumnTypeIntegerTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL("CREATE TABLE columnTypes(t TEXT, v VARCHAR, i INTEGER, n NUMBER);");
        db.execSQL("INSERT INTO columnTypes VALUES(500, 500, 500, 500);");
        assertEquals("TEXT", database.getColumnType("columnTypes", "t"));
        assertEquals("VARCHAR", database.getColumnType("columnTypes", "v"));
        assertEquals("INTEGER", database.getColumnType("columnTypes", "i"));
        assertEquals("NUMBER", database.getColumnType("columnTypes", "n"));
        wipeOutDB();
    }

    @Test
    public void getColumnTypeNullTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL("CREATE TABLE columnTypes(t TEXT, v VARCHAR, i INTEGER, n NUMBER);");
        db.execSQL("INSERT INTO columnTypes VALUES(null, null, null, null);");
        assertEquals("TEXT", database.getColumnType("columnTypes", "t"));
        assertEquals("VARCHAR", database.getColumnType("columnTypes", "v"));
        assertEquals("INTEGER", database.getColumnType("columnTypes", "i"));
        assertEquals("NUMBER", database.getColumnType("columnTypes", "n"));
        wipeOutDB();
    }

    @Test
    public void getTableValue() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL("CREATE TABLE columnTypes(t TEXT, v VARCHAR, i INTEGER, n NUMBER);");
        db.execSQL("INSERT INTO columnTypes VALUES('minute', null, 1,4.5);");
        Cursor res = db.rawQuery("SELECT * FROM columnTypes", null);
        res.moveToFirst();
        assertEquals("minute", database.getTableValue(res, "columnTypes", "t"));
        assertNull(database.getTableValue(res, "columnTypes", "v"));
        assertEquals(1, database.getTableValue(res, "columnTypes", "i"));
        assertEquals(4.5, database.getTableValue(res, "columnTypes", "n"));
        wipeOutDB();
    }

    @Test(expected = SQLiteException.class)
    public void getFullColumnNoTableTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        try {
            database.getFullColumn("someTable", "something");
            fail();
        } finally {
            wipeOutDB();
        }
    }

    @Test()
    public void getFullColumnNoColumnTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        assertEquals(new ArrayList<>(), database.getFullColumn("fullColumn", "something"));
        wipeOutDB();
    }

    @Test
    public void getFullColumnNoDataTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        database.getFullColumn("fullColumn", "type");
        assertEquals(new ArrayList<>(), database.getFullColumn("fullColumn", "type"));
        wipeOutDB();
    }

    @Test
    public void getFullColumnCustomTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        db.execSQL("INSERT INTO fullColumn VALUES(null,1,'minute');");
        db.execSQL("INSERT INTO fullColumn VALUES(null,2,'seconds');");
        db.execSQL("INSERT INTO fullColumn VALUES(null,3,'hours');");
        assertEquals(new ArrayList<>(Arrays.asList(1, 2, 3)), database.getFullColumn("fullColumn", "type"));
        assertEquals(new ArrayList<>(Arrays.asList("minute", "seconds", "hours")), database.getFullColumn("fullColumn", "unit"));
        wipeOutDB();
    }

    @Test
    public void getColumnsNoTable() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        assertEquals(0, database.getColumns(GOALS_TABLE).size());
    }

    @Test
    public void getColumns() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertEquals(4, database.getColumns(EXERCISES_TABLE).size());
        assertEquals(Arrays.asList("id", "past", "current", "color"), database.getColumns(EXERCISES_TABLE));
    }

    @Test
    public void renameColumnNoData() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Map<String, String> newGoalsTable = new LinkedHashMap<>();
        newGoalsTable.put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        newGoalsTable.put("past", "INTEGER");
        newGoalsTable.put("currently", "INTEGER");
        newGoalsTable.put("color", "NUMBER");
        database.renameColumn(GOALS_TABLE, newGoalsTable);
        assertEquals(Arrays.asList("id", "past", "currently", "color"), database.getColumns(GOALS_TABLE));
        assertEquals(0, db.rawQuery("SELECT * FROM " + GOALS_TABLE, null).getCount());
    }

    @Test
    public void renameColumnWithData() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Map<String, String> newGoalsTable = new LinkedHashMap<>();
        newGoalsTable.put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        newGoalsTable.put("unit", "VARCHAR");
        newGoalsTable.put("type", "VARCHAR");
        newGoalsTable.put("awesome", "VARCHAR");
        database.renameColumn(MEASUREMENTS_TABLE, newGoalsTable);
        assertEquals(Arrays.asList("id", "unit", "type", "awesome"), database.getColumns(MEASUREMENTS_TABLE));
        assertEquals(7, db.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE, null).getCount());
        Cursor res = db.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + ";", null);
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals("time", res.getString(1));
        assertEquals("minute", res.getString(2));
        assertEquals("60", res.getString(3));
        res.moveToNext();
        assertEquals(2, res.getInt(0));
        assertEquals("distance", res.getString(1));
        assertEquals("kilometer", res.getString(2));
        assertEquals("1", res.getString(3));
        res.close();
    }

    @Test
    public void getActivityColorTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertEquals(Color.BLUE, database.getExerciseColor("Ran"));
        assertEquals(Color.BLUE, database.getExerciseColor("Ran (kilometer)"));
        assertEquals(Color.BLUE, database.getExerciseColor("Ran (minute)"));
        assertEquals(Color.YELLOW, database.getExerciseColor("Running"));
        assertEquals(Color.YELLOW, database.getExerciseColor("Drank (beers)"));
        assertEquals(Color.DKGRAY, database.getExerciseColor("Played Soccer (minute)"));
    }

    @Test
    public void logActivityTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2000-01-01 10:10", "Running", "seconds", 12.2);
        database.logActivity("2000-01-01 10:10", "Ran", "minute", 30);
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + ";", null);
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals("2000-01-01 10:10", res.getString(1));
        assertEquals(-1, res.getInt(2));
        assertEquals(-1, res.getInt(3));
        assertEquals(12.2, res.getDouble(4), 0);
        res.moveToNext();
        assertEquals(2, res.getInt(0));
        assertEquals("2000-01-01 10:10", res.getString(1));
        assertEquals(2, res.getInt(2));
        assertEquals(1, res.getInt(3));
        assertEquals(30, res.getDouble(4), 0);
        res.moveToNext();
        assertTrue(res.isAfterLast());
        res.close();
        wipeOutDB();
    }

    @Test
    public void logActivityFullTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("3", "2000-01-01 10:10", "Running", "seconds", 12.2);
        database.logActivity("2000-01-01 10:10", "Ran", "minute", 30);
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + ";", null);
        res.moveToFirst();
        assertEquals(3, res.getInt(0));
        assertEquals("2000-01-01 10:10", res.getString(1));
        assertEquals(-1, res.getInt(2));
        assertEquals(-1, res.getInt(3));
        assertEquals(12.2, res.getDouble(4), 0);
        res.moveToNext();
        assertEquals(4, res.getInt(0));
        assertEquals("2000-01-01 10:10", res.getString(1));
        assertEquals(2, res.getInt(2));
        assertEquals(1, res.getInt(3));
        assertEquals(30, res.getDouble(4), 0);
        res.moveToNext();
        assertTrue(res.isAfterLast());
        res.close();
        wipeOutDB();
    }

    @Test
    public void logBeerTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logBeer();
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + ";", null);
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertTrue(res.getString(1).matches(DATETIME_FORMAT + ":\\d{2}"));
        assertEquals(0, res.getInt(2));
        assertEquals(0, res.getInt(3));
        assertEquals(1, res.getDouble(4), 0);
        res.moveToNext();
        assertTrue(res.isAfterLast());
        res.close();
        wipeOutDB();
    }

    @Test
    public void logBeersTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logBeer("1", "'2000-01-01 10:10'", 2);
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + ";", null);
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals("2000-01-01 10:10", res.getString(1));
        assertEquals(0, res.getInt(2));
        assertEquals(0, res.getInt(3));
        assertEquals(2, res.getDouble(4), 0);
        res.moveToNext();
        assertTrue(res.isAfterLast());
        res.close();
        wipeOutDB();
    }

    @Test
    public void getBeersDrankTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertEquals(0, database.getBeersDrank());
        database.logBeer();
        assertEquals(1, database.getBeersDrank());
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        assertEquals(10, database.getBeersDrank());
        wipeOutDB();
    }

    @Test
    public void getMatchingMeasurementsNoMatch() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertEquals(1, database.getMatchingMeasurements(new Measurement(db, "new one")).size());
    }

    @Test
    public void getMatchingMeasurementsNullMatch() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        List<Measurement> measurements = database.getMatchingMeasurements(new Measurement(db, "class"));
        assertEquals(1, measurements.size());
        assertEquals(6, measurements.get(0).getId());
    }

    @Test
    public void getMatchingMeasurementsTimeMatch() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        List<Measurement> measurements = database.getMatchingMeasurements(new Measurement(db, "minute"));
        assertEquals(3, measurements.size());
        assertEquals(1, measurements.get(0).getId());
        assertEquals(3, measurements.get(1).getId());
        assertEquals(4, measurements.get(2).getId());
    }

    @Test
    public void getMatchingGoalsNoMatches() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertNull(database.getMatchingGoals(new Exercise(db, "Ran"), new Measurement(db, "new one")));
    }

    @Test
    public void getMatchingGoalsExerciseSingleMatch() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,1);");
        assertEquals(1, database.getMatchingGoals(new Exercise(db, "Ran"), new Measurement(db, "kilometer")).getId());
    }

    @Test
    public void getMatchingGoalsExerciseOppositeMatch() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,1);");
        assertEquals(1, database.getMatchingGoals(new Exercise(db, "Ran"), new Measurement(db, "mile")).getId());
    }

    @Test
    public void getBeersEarnedTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        addGoals(db);
        assertEquals(1, database.getBeersEarned(new Exercise(db, "Ran"), new Measurement(db, "kilometer"), 5), 0);
        assertEquals(0, database.getBeersEarned(new Exercise(db, "Ran"), new Measurement(db, "kilometer"), 0), 0);
        assertEquals(1, database.getBeersEarned(new Exercise(db, "Walked"), new Measurement(db, "kilometer"), 5), 0);
        assertEquals(0.5, database.getBeersEarned(new Exercise(db, "Played Soccer"), new Measurement(db, "minute"), 15), 0);
        assertEquals(0, database.getBeersEarned(new Exercise(db, "Played Soccer"), new Measurement(db, "kilometer"), 5), 0);
        wipeOutDB();
    }

    @Test
    public void getBeersEarnedSwapTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,5);");
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(2,1,1,10);");
        assertEquals(1, database.getBeersEarned(new Exercise(db, "Ran"), new Measurement(db, "kilometer"), 5), 0);
        assertEquals(0.9656063879, database.getBeersEarned(new Exercise(db, "Ran"), new Measurement(db, "mile"), 3), 0.00001);
        assertEquals(3, database.getBeersEarned(new Exercise(db, "Walk"), new Measurement(db, "minute"), 30), 0);
        assertEquals(2, database.getBeersEarned(new Exercise(db, "Walk"), new Measurement(db, "second"), 1200), 0);
        assertEquals(1.5, database.getBeersEarned(new Exercise(db, "Walk"), new Measurement(db, "hour"), 0.25), 0);
        wipeOutDB();
    }

    @Test
    public void getTotalBeersEarnedTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        addGoals(db);
        assertEquals(0, database.getTotalBeersEarned(), 0);
        database.logActivity(getDateTime(), "Ran", "kilometer", 5);
        assertEquals(1, database.getTotalBeersEarned(), 0);
        database.logActivity(getDateTime(), "Walked", "kilometer", 32);
        assertEquals(7.4, database.getTotalBeersEarned(), 0);
        database.logActivity(getDateTime(), "Played Soccer", "kilometer", 32);
        assertEquals(7.4, database.getTotalBeersEarned(), 0);
        database.logActivity(getDateTime(), "Soccered", "minute", 30);
        assertEquals(7.4, database.getTotalBeersEarned(), 0);
        database.logActivity(getDateTime(), "Played Soccer", "minute", 33);
        assertEquals(8.5, database.getTotalBeersEarned(), 0);
        database.logActivity(getDateTime(), "Lifted", "minute", 15);
        assertEquals(9.0, database.getTotalBeersEarned(), 0);
        database.logActivity(getDateTime(), "Cycled", "minute", 15);
        assertEquals(9.0, database.getTotalBeersEarned(), 0);
        database.logActivity(getDateTime(), "Cycled", "kilometer", 15);
        assertEquals(10.5, database.getTotalBeersEarned(), 0);
        wipeOutDB();
    }

    @Test
    public void getBeersRemainingTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        addGoals(db);
        assertEquals(0, database.getBeersRemaining(), 0);
        database.logActivity(getDateTime(), "Ran", "kilometer", 5);
        assertEquals(1, database.getBeersRemaining(), 0);
        database.logActivity(getDateTime(), "Walked", "kilometer", 32);
        assertEquals(7, database.getBeersRemaining(), 0);
        database.logActivity(getDateTime(), "Played Soccer", "kilometer", 32);
        assertEquals(7, database.getBeersRemaining(), 0);
        database.logActivity(getDateTime(), "Soccered", "minute", 30);
        assertEquals(7, database.getBeersRemaining(), 0);
        database.logActivity(getDateTime(), "Played Soccer", "minute", 33);
        database.logBeer();
        assertEquals(7, database.getBeersRemaining(), 0);
        database.logActivity(getDateTime(), "Lifted", "minute", 15);
        database.logBeer();
        database.logBeer();
        assertEquals(6.0, database.getBeersRemaining(), 0);
        database.logActivity(getDateTime(), "Cycled", "minute", 15);
        assertEquals(6.0, database.getBeersRemaining(), 0);
        database.logActivity(getDateTime(), "Cycled", "kilometer", 15);
        assertEquals(7, database.getBeersRemaining(), 0);
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        database.logBeer();
        assertEquals(-1, database.getBeersRemaining(), 0);
        wipeOutDB();
    }

    @Test
    public void removeActivityTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity(getDateTime(), "Running", "seconds", 12.2);
        database.logActivity(getDateTime(), "Ran", "minute", 30);
        database.removeActivity(1);
        Cursor cursor = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null);
        assertEquals(1, cursor.getCount());
        cursor.close();
        database.removeActivity(1);
        cursor = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null);
        assertEquals(1, cursor.getCount());
        cursor.close();
        database.removeActivity(2);
        cursor = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        wipeOutDB();
    }

    @Test
    public void getActivityTimeTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2000-01-01 10:10", "Running", "seconds", 12.2);
        assertEquals("2000-01-01 10:10", database.getActivityTime(1));
        assertEquals("Unknown", database.getActivityTime(0));
        wipeOutDB();
    }

    @Test
    public void addGoalTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.addGoal("Running", "seconds", 12.2);
        database.addGoal("Run", "minute", 30);
        Cursor res = db.rawQuery("SELECT * FROM " + GOALS_TABLE + ";", null);
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals(-1, res.getInt(1));
        assertEquals(-1, res.getInt(2));
        assertEquals(12.2, res.getDouble(3), 0);
        res.moveToNext();
        assertEquals(2, res.getInt(0));
        assertEquals(2, res.getInt(1));
        assertEquals(1, res.getInt(2));
        assertEquals(30, res.getDouble(3), 0);
        res.moveToNext();
        assertTrue(res.isAfterLast());
        res.close();
        wipeOutDB();
    }

    @Test
    public void addGoalFullTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        addGoals(db);
        db.execSQL("DELETE FROM " + GOALS_TABLE + ";");
        database.addGoal("3", "Running", "seconds", 12.2);
        database.addGoal("Run", "minute", 30);
        Cursor res = db.rawQuery("SELECT * FROM " + GOALS_TABLE + ";", null);
        res.moveToFirst();
        assertEquals(3, res.getInt(0));
        assertEquals(-1, res.getInt(1));
        assertEquals(-1, res.getInt(2));
        assertEquals(12.2, res.getDouble(3), 0);
        res.moveToNext();
        assertEquals(6, res.getInt(0));
        assertEquals(2, res.getInt(1));
        assertEquals(1, res.getInt(2));
        assertEquals(30, res.getDouble(3), 0);
        res.moveToNext();
        assertTrue(res.isAfterLast());
        res.close();
        wipeOutDB();
    }

    private void addGoals(SQLiteDatabase database) {
        database.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,1,2,5);");
        database.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(2,2,2,5);");
        database.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(3,3,2,10);");
        database.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(4,4,1,30);");
        database.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(5,5,1,30);");
    }

    @Test
    public void removeGoalTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        addGoals(db);
        database.removeGoal(1);
        Cursor cursor = db.rawQuery("SELECT * FROM " + GOALS_TABLE, null);
        assertEquals(4, cursor.getCount());
        cursor.close();
        database.removeGoal(1);
        cursor = db.rawQuery("SELECT * FROM " + GOALS_TABLE, null);
        assertEquals(4, cursor.getCount());
        cursor.close();
        database.removeGoal(2);
        cursor = db.rawQuery("SELECT * FROM " + GOALS_TABLE, null);
        assertEquals(3, cursor.getCount());
        cursor.close();
        wipeOutDB();
    }

    @Test
    public void changeGoalsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        addGoals(db);
        database.logActivity(getDateTime(), "Ran", "kilometer", 5);
        database.logActivity(getDateTime(), "Walked", "kilometer", 10);
        assertEquals(3, database.getBeersRemaining(), 0);
        database.removeGoal(1);
        database.removeGoal(2);
        assertEquals(3, database.getBeersRemaining(), 0);
        String dateTime = getDateTime();
        database.logActivity(dateTime, "Cycled", "kilometer", 20);
        assertEquals(5, database.getBeersRemaining(), 0);
        database.removeGoal(3);
        database.addGoal("Cycle", "kilometer", 20);
        database.logActivity(dateTime, "Cycled", "kilometer", 20);
        database.logBeer();
        assertEquals(5, database.getBeersRemaining(), 0);
        database.logBeer();
        database.removeGoal(6);
        database.addGoal("Run", "kilometer", 1);
        assertEquals(4, database.getBeersRemaining(), 0);
        wipeOutDB();
    }

    private String getDateTime() {
        Date date = new Date();
        // purposefully adding in seconds, when the app doesn't provide it, as this speeds up testing.
        // otherwise, i'd have to wait a minute for accurate results
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        return DATE_FORMAT.format(date) + " " + timeFormat.format(date);
    }

    //////////////////////////////////////////////////////////////
    /////////////////////// SCHEMA CHANGES ///////////////////////
    //////////////////////////////////////////////////////////////

    @Test
    public void oldGoalsTableEmpty() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + GOALS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, activity INTEGER, measurement INTEGER, amount NUMBER);");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(GOALS_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(GOALS_TABLE, "activity"));
        assertEquals(0, db.rawQuery("SELECT * FROM " + GOALS_TABLE, null).getCount());
    }

    @Test
    public void oldGoalsTableFull() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + GOALS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, activity INTEGER, measurement INTEGER, amount NUMBER);");
        addGoals(db);
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(GOALS_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(GOALS_TABLE, "activity"));
        assertEquals(5, db.rawQuery("SELECT * FROM " + GOALS_TABLE, null).getCount());
    }

    @Test
    public void oldActivitiesTableEmpty() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + "ActivityLog(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, activity INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(ACTIVITIES_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(ACTIVITIES_TABLE, "activity"));
        assertEquals(0, db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null).getCount());
    }

    @Test
    public void oldActivitiesTableFull() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + "ActivityLog(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, activity INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        db.execSQL("INSERT INTO ActivityLog VALUES(1,'2020-10-10 00:00',2,5,1,1);");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(ACTIVITIES_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(ACTIVITIES_TABLE, "activity"));
        assertEquals(1, db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null).getCount());
    }

    @Test
    public void newActivitiesTableOldColumn() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITIES_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, activity INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(ACTIVITIES_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(ACTIVITIES_TABLE, "activity"));
        assertEquals(0, db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null).getCount());
    }

    @Test
    public void newActivitiesTableFullOldColumn() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITIES_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, activity INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-10-10 00:00',2,5,1,1);");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(ACTIVITIES_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(ACTIVITIES_TABLE, "activity"));
        assertEquals(1, db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null).getCount());
    }

    @Test
    public void oldActivitiesTableNewColumn() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + "ActivityLog(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, exercise INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(ACTIVITIES_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(ACTIVITIES_TABLE, "activity"));
        assertEquals(0, db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null).getCount());
    }

    @Test
    public void oldActivitiesTableFullNewColumn() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + "ActivityLog(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, exercise INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        db.execSQL("INSERT INTO ActivityLog VALUES(1,'2020-10-10 00:00',2,5,1,1);");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(ACTIVITIES_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(ACTIVITIES_TABLE, "activity"));
        assertEquals(1, db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null).getCount());
    }

    @Test
    public void newActivitiesTableNewColumn() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITIES_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, exercise INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(ACTIVITIES_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(ACTIVITIES_TABLE, "activity"));
        assertEquals(0, db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null).getCount());
    }

    @Test
    public void newActivitiesTableFullNewColumn() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + ACTIVITIES_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, exercise INTEGER, measurement INTEGER, amount NUMBER, beers NUMBER);");
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-10-10 00:00',2,5,1,1);");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(ACTIVITIES_TABLE, "exercise"));
        assertFalse(database.doesTableHaveColumn(ACTIVITIES_TABLE, "activity"));
        assertEquals(1, db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null).getCount());
    }

    @Test
    public void existingMeasurementsTableSetup() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        db.execSQL(CREATE_TABLE_IF_NOT_EXISTS + MEASUREMENTS_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, unit VARCHAR);");
        db.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(1,'time','minute');");
        db.execSQL(INSERT_INTO + MEASUREMENTS_TABLE + " VALUES(2,'distance','kilometer');");
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(MEASUREMENTS_TABLE, "conversion"));
        assertEquals(7, db.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE, null).getCount());
    }

    @Test
    public void newMeasurementsTableSetup() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertTrue(database.doesTableHaveColumn(MEASUREMENTS_TABLE, "conversion"));
        assertEquals(7, db.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE, null).getCount());
    }
}
