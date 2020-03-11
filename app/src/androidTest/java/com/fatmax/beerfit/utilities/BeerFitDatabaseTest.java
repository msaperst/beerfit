package com.fatmax.beerfit.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.fatmax.beerfit.AddActivityActivity.DATE_FORMAT;
import static com.fatmax.beerfit.utilities.BeerFitDatabase.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.BeerFitDatabase.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.utilities.BeerFitDatabase.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.BeerFitDatabase.MEASUREMENTS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class BeerFitDatabaseTest {

    private static final String DATABASE_NAME = "testDB";
    private static final String DATETIME_FORMAT = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}";

    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void setupDatabaseTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertFalse(beerFitDatabase.isTableMissing(MEASUREMENTS_TABLE));
        assertFalse(beerFitDatabase.isTableMissing(ACTIVITIES_TABLE));
        assertFalse(beerFitDatabase.isTableMissing(GOALS_TABLE));
        assertFalse(beerFitDatabase.isTableMissing(ACTIVITY_LOG_TABLE));
        wipeOutDB();
    }

    @Test
    public void isTableMissingTrueTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertTrue(beerFitDatabase.isTableMissing("myTable"));
    }

    @Test
    public void isTableMissingFalseTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS someTable(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR);");
        beerFitDatabase.setupDatabase();
        assertFalse(beerFitDatabase.isTableMissing("someTable"));
        wipeOutDB();
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoTableTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        // no table exists
        try {
            beerFitDatabase.getColumnType("columnTypes", "t");
            fail();
        } finally {
            wipeOutDB();
        }
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoColumnTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        // column doesn't exist
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        try {
            beerFitDatabase.getColumnType("columnTypes", "x");
            fail();
        } finally {
            wipeOutDB();
        }
    }

    @Test
    public void getColumnTypeNoDataTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        assertNull(beerFitDatabase.getColumnType("columnTypes", "t"));
        wipeOutDB();
    }

    @Test
    public void getColumnTypeStringTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES('500.0', '500.0', '500.0', '500.0', '500.0');");
        assertEquals("text", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("real", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("text", beerFitDatabase.getColumnType("columnTypes", "b"));
        wipeOutDB();
    }

    @Test
    public void getColumnTypeDoubleTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES(500.0, 500.0, 500.0, 500.0, 500.0);");
        assertEquals("text", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("real", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("real", beerFitDatabase.getColumnType("columnTypes", "b"));
        wipeOutDB();
    }

    @Test
    public void getColumnTypeIntegerTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES(500, 500, 500, 500, 500);");
        assertEquals("text", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("real", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "b"));
        wipeOutDB();
    }

    @Test
    public void getColumnTypeBlobTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES(x'0500', x'0500', x'0500', x'0500', x'0500');");
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "b"));
        wipeOutDB();
    }

    @Test
    public void getColumnTypeNullTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES(null, null, null, null, null);");
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "b"));
        wipeOutDB();
    }

    @Test
    public void getTableValue() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE value(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO value VALUES('minutes', null, 1,4.5,x'0500');");
        Cursor res = db.rawQuery("SELECT * FROM value", null);
        res.moveToFirst();
        assertEquals(1, beerFitDatabase.getTableValue(res, "value", "i"));
        assertEquals("minutes", beerFitDatabase.getTableValue(res, "value", "t"));
        assertEquals(Arrays.toString(new byte[]{(byte) 0x05, 0x00}), Arrays.toString((byte[]) beerFitDatabase.getTableValue(res, "value", "b")));
        assertEquals(4.5, beerFitDatabase.getTableValue(res, "value", "r"));
        assertNull(beerFitDatabase.getTableValue(res, "value", "n"));
        wipeOutDB();
    }

    @Test(expected = SQLiteException.class)
    public void getFullColumnNoTableTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        try {
            beerFitDatabase.getFullColumn("someTable", "something");
            fail();
        } finally {
            wipeOutDB();
        }
    }

    @Test()
    public void getFullColumnNoColumnTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        assertEquals(new ArrayList<>(), beerFitDatabase.getFullColumn("fullColumn", "something"));
        wipeOutDB();
    }

    @Test
    public void getFullColumnNoDataTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        beerFitDatabase.getFullColumn("fullColumn", "type");
        assertEquals(new ArrayList<>(), beerFitDatabase.getFullColumn("fullColumn", "type"));
        wipeOutDB();
    }

    @Test
    public void getFullColumnCustomTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        db.execSQL("INSERT INTO fullColumn VALUES(null,1,'minutes');");
        db.execSQL("INSERT INTO fullColumn VALUES(null,2,'seconds');");
        db.execSQL("INSERT INTO fullColumn VALUES(null,3,'hours');");
        assertEquals(new ArrayList<>(Arrays.asList(1, 2, 3)), beerFitDatabase.getFullColumn("fullColumn", "type"));
        assertEquals(new ArrayList<>(Arrays.asList("minutes", "seconds", "hours")), beerFitDatabase.getFullColumn("fullColumn", "unit"));
        wipeOutDB();
    }

    @Test(expected = SQLiteException.class)
    public void getOrdinalNoTableTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        try {
            beerFitDatabase.getOrdinal(MEASUREMENTS_TABLE, "unit", "kilometer");
            fail();
        } finally {
            wipeOutDB();
        }
    }

    @Test(expected = SQLiteException.class)
    public void getOrdinalNoColumnTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        try {
            beerFitDatabase.getOrdinal("fullColumn", "u", "kilometer");
            fail();
        } finally {
            wipeOutDB();
        }
    }

    @Test
    public void getOrdinalNoMatchTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        assertEquals(-1, beerFitDatabase.getOrdinal("fullColumn", "unit", "kilometer"));
        wipeOutDB();
    }

    @Test
    public void getOrdinalTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertEquals(1, beerFitDatabase.getOrdinal(MEASUREMENTS_TABLE, "type", "time"));
        assertEquals(2, beerFitDatabase.getOrdinal(MEASUREMENTS_TABLE, "unit", "kilometers"));
        db.execSQL("INSERT INTO " + MEASUREMENTS_TABLE + " VALUES(5,'time','hours')");
        assertEquals(5, beerFitDatabase.getOrdinal(MEASUREMENTS_TABLE, "unit", "hours"));
        db.execSQL("INSERT INTO " + MEASUREMENTS_TABLE + " VALUES(null,'time','seconds')");
        assertEquals(6, beerFitDatabase.getOrdinal(MEASUREMENTS_TABLE, "unit", "seconds"));
        // new data lookup
        beerFitDatabase.logBeer();
        assertEquals(1, beerFitDatabase.getOrdinal(ACTIVITY_LOG_TABLE, "amount", "1"));
        wipeOutDB();

    }

    @Test
    public void logActivityTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logActivity("2000-01-01 10:10", "Running", "seconds", 12.2);
        beerFitDatabase.logActivity("2000-01-01 10:10", "Ran", "minutes", 30);
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + ";", null);
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
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logActivity("3", "2000-01-01 10:10", "Running", "seconds", 12.2);
        beerFitDatabase.logActivity("2000-01-01 10:10", "Ran", "minutes", 30);
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + ";", null);
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
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logBeer();
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + ";", null);
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
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logBeer("1", "'2000-01-01 10:10'", 2);
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + ";", null);
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
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertEquals(0, beerFitDatabase.getBeersDrank());
        beerFitDatabase.logBeer();
        assertEquals(1, beerFitDatabase.getBeersDrank());
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        assertEquals(10, beerFitDatabase.getBeersDrank());
        wipeOutDB();
    }

    @Test
    public void getBeersEarnedTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        addGoals(db);
        assertEquals(1, beerFitDatabase.getBeersEarned("Ran", "kilometers", 5), 0);
        assertEquals(0, beerFitDatabase.getBeersEarned("Ran", "kilometers", 0), 0);
        assertEquals(1, beerFitDatabase.getBeersEarned("Walked", "kilometers", 5), 0);
        assertEquals(0.5, beerFitDatabase.getBeersEarned("Played Soccer", "minutes", 15), 0);
        assertEquals(0, beerFitDatabase.getBeersEarned("Played Soccer", "kilometers", 5), 0);
        wipeOutDB();
    }

    @Test
    public void getTotalBeersEarnedTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        addGoals(db);
        assertEquals(0, beerFitDatabase.getTotalBeersEarned(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Ran", "kilometers", 5);
        assertEquals(1, beerFitDatabase.getTotalBeersEarned(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Walked", "kilometers", 32);
        assertEquals(7.4, beerFitDatabase.getTotalBeersEarned(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Played Soccer", "kilometers", 32);
        assertEquals(7.4, beerFitDatabase.getTotalBeersEarned(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Soccered", "minutes", 30);
        assertEquals(7.4, beerFitDatabase.getTotalBeersEarned(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Played Soccer", "minutes", 33);
        assertEquals(8.5, beerFitDatabase.getTotalBeersEarned(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Lifted", "minutes", 15);
        assertEquals(9.0, beerFitDatabase.getTotalBeersEarned(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Cycled", "minutes", 15);
        assertEquals(9.0, beerFitDatabase.getTotalBeersEarned(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Cycled", "kilometers", 15);
        assertEquals(10.5, beerFitDatabase.getTotalBeersEarned(), 0);
        wipeOutDB();
    }

    @Test
    public void getBeersRemainingTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        addGoals(db);
        assertEquals(0, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Ran", "kilometers", 5);
        assertEquals(1, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Walked", "kilometers", 32);
        assertEquals(7, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Played Soccer", "kilometers", 32);
        assertEquals(7, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Soccered", "minutes", 30);
        assertEquals(7, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Played Soccer", "minutes", 33);
        beerFitDatabase.logBeer();
        assertEquals(7, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Lifted", "minutes", 15);
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        assertEquals(6.0, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Cycled", "minutes", 15);
        assertEquals(6.0, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity(getDateTime(), "Cycled", "kilometers", 15);
        assertEquals(7, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        assertEquals(-1, beerFitDatabase.getBeersRemaining(), 0);
        wipeOutDB();
    }

    @Test
    public void removeActivityTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logActivity(getDateTime(), "Running", "seconds", 12.2);
        beerFitDatabase.logActivity(getDateTime(), "Ran", "minutes", 30);
        beerFitDatabase.removeActivity(1);
        Cursor cursor = db.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE, null);
        assertEquals(1, cursor.getCount());
        cursor.close();
        beerFitDatabase.removeActivity(1);
        cursor = db.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE, null);
        assertEquals(1, cursor.getCount());
        cursor.close();
        beerFitDatabase.removeActivity(2);
        cursor = db.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        wipeOutDB();
    }

    @Test
    public void getActivityTimeTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logActivity("2000-01-01 10:10", "Running", "seconds", 12.2);
        assertEquals("2000-01-01 10:10", beerFitDatabase.getActivityTime(1));
        assertEquals("Unknown", beerFitDatabase.getActivityTime(0));
        wipeOutDB();
    }

    @Test
    public void addGoalTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.addGoal("Running", "seconds", 12.2);
        beerFitDatabase.addGoal("Run", "minutes", 30);
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
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        addGoals(db);
        db.execSQL("DELETE FROM " + GOALS_TABLE + ";");
        beerFitDatabase.addGoal("3", "Running", "seconds", 12.2);
        beerFitDatabase.addGoal("Run", "minutes", 30);
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
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        addGoals(db);
        beerFitDatabase.removeGoal(1);
        Cursor cursor = db.rawQuery("SELECT * FROM " + GOALS_TABLE, null);
        assertEquals(4, cursor.getCount());
        cursor.close();
        beerFitDatabase.removeGoal(1);
        cursor = db.rawQuery("SELECT * FROM " + GOALS_TABLE, null);
        assertEquals(4, cursor.getCount());
        cursor.close();
        beerFitDatabase.removeGoal(2);
        cursor = db.rawQuery("SELECT * FROM " + GOALS_TABLE, null);
        assertEquals(3, cursor.getCount());
        cursor.close();
        wipeOutDB();
    }

    @Test
    public void changeGoalsTest() {
        SQLiteDatabase db = getDB();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        addGoals(db);
        beerFitDatabase.logActivity(getDateTime(), "Ran", "kilometers", 5);
        beerFitDatabase.logActivity(getDateTime(), "Walked", "kilometers", 10);
        assertEquals(3, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.removeGoal(1);
        beerFitDatabase.removeGoal(2);
        assertEquals(3, beerFitDatabase.getBeersRemaining(), 0);
        String dateTime = getDateTime();
        beerFitDatabase.logActivity(dateTime, "Cycled", "kilometers", 20);
        assertEquals(5, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.removeGoal(3);
        beerFitDatabase.addGoal("Cycle", "kilometers", 20);
        beerFitDatabase.logActivity(dateTime, "Cycled", "kilometers", 20);
        beerFitDatabase.logBeer();
        assertEquals(5, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logBeer();
        beerFitDatabase.removeGoal(6);
        beerFitDatabase.addGoal("Run", "kilometers", 1);
        assertEquals(4, beerFitDatabase.getBeersRemaining(), 0);
        wipeOutDB();
    }

    private void wipeOutDB() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    private SQLiteDatabase getDB() {
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

    private String getDateTime() {
        Date date = new Date();
        // purposefully adding in seconds, when the app doesn't provide it, as this speeds up testing.
        // otherwise, i'd have to wait a minute for accurate results
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        return DATE_FORMAT.format(date) + " " + timeFormat.format(date);
    }
}
