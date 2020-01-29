package com.fatmax.beerfit;

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

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BeerFitDatabaseTest {

    private static final String DATABASE_NAME = "testDB";
    private static final String DATETIME_FORMAT = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

    @After
    public void cleanupDB() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void setupDatabaseTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertFalse(beerFitDatabase.isTableMissing("Measurements"));
        assertFalse(beerFitDatabase.isTableMissing("Activities"));
        assertFalse(beerFitDatabase.isTableMissing("Goals"));
        assertFalse(beerFitDatabase.isTableMissing("ActivityLog"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void isTableMissingTrueTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertTrue(beerFitDatabase.isTableMissing("myTable"));
    }

    @Test
    public void isTableMissingFalseTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS someTable(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR);");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertFalse(beerFitDatabase.isTableMissing("someTable"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoTableTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        // no table exists
        try {
            beerFitDatabase.getColumnType("columnTypes", "t");
            assertTrue(false);
        } finally {
            SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
        }
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoColumnTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        // column doesn't exist
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        try {
            beerFitDatabase.getColumnType("columnTypes", "x");
            assertTrue(false);
        } finally {
            SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
        }
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoDataTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        try {
            beerFitDatabase.getColumnType("columnTypes", "t");
            assertTrue(false);
        } finally {
            SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
        }
    }

    @Test
    public void getColumnTypeStringTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES('500.0', '500.0', '500.0', '500.0', '500.0');");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        assertEquals("text", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("real", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("text", beerFitDatabase.getColumnType("columnTypes", "b"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getColumnTypeDoubleTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES(500.0, 500.0, 500.0, 500.0, 500.0);");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        assertEquals("text", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("real", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("real", beerFitDatabase.getColumnType("columnTypes", "b"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getColumnTypeIntegerTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES(500, 500, 500, 500, 500);");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        assertEquals("text", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("real", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("integer", beerFitDatabase.getColumnType("columnTypes", "b"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getColumnTypeBlobTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES(x'0500', x'0500', x'0500', x'0500', x'0500');");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("blob", beerFitDatabase.getColumnType("columnTypes", "b"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getColumnTypeNullTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE columnTypes(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO columnTypes VALUES(null, null, null, null, null);");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "t"));
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "n"));
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "i"));
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "r"));
        assertEquals("null", beerFitDatabase.getColumnType("columnTypes", "b"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getTableValue() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE value(t  TEXT, n NUMERIC, i  INTEGER, r  REAL, b  BLOB);");
        db.execSQL("INSERT INTO value VALUES('minutes', null, 1,4.5,x'0500');");
        Cursor res = db.rawQuery("SELECT * FROM value", null);
        res.moveToFirst();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        assertEquals(1, beerFitDatabase.getTableValue(res, "value", "i"));
        assertEquals("minutes", beerFitDatabase.getTableValue(res, "value", "t"));
        assertEquals(Arrays.toString(new byte[]{(byte) 0x05, 0x00}), Arrays.toString((byte[]) beerFitDatabase.getTableValue(res, "value", "b")));
        assertEquals(4.5, beerFitDatabase.getTableValue(res, "value", "r"));
        assertNull(beerFitDatabase.getTableValue(res, "value", "n"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test(expected = SQLiteException.class)
    public void getFullColumnNoTableTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        try {
            beerFitDatabase.getFullColumn("someTable", "something");
            assertTrue(false);
        } finally {
            SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
        }
    }

    @Test()
    public void getFullColumnNoColumnTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        assertEquals(new ArrayList<>(), beerFitDatabase.getFullColumn("fullColumn", "something"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getFullColumnNoDataTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.getFullColumn("fullColumn", "type");
        assertEquals(new ArrayList<>(), beerFitDatabase.getFullColumn("fullColumn", "type"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getFullColumnCustomTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        db.execSQL("INSERT INTO fullColumn VALUES(null,1,'minutes');");
        db.execSQL("INSERT INTO fullColumn VALUES(null,2,'seconds');");
        db.execSQL("INSERT INTO fullColumn VALUES(null,3,'hours');");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        assertEquals(new ArrayList<Integer>(Arrays.asList(1, 2, 3)), beerFitDatabase.getFullColumn("fullColumn", "type"));
        assertEquals(new ArrayList<String>(Arrays.asList("minutes", "seconds", "hours")), beerFitDatabase.getFullColumn("fullColumn", "unit"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test(expected = SQLiteException.class)
    public void getOrdinalNoTableTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        try {
            beerFitDatabase.getOrdinal("Measurements", "unit", "kilometer");
            assertFalse(true);
        } finally {
            SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
        }
    }

    @Test(expected = SQLiteException.class)
    public void getOrdinalNoColumnTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        try {
            beerFitDatabase.getOrdinal("fullColumn", "u", "kilometer");
            assertFalse(true);
        } finally {
            SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
        }
    }

    @Test
    public void getOrdinalNoMatchTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS fullColumn(id INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, unit VARCHAR);");
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        assertEquals(-1, beerFitDatabase.getOrdinal("fullColumn", "unit", "kilometer"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getOrdinalTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertEquals(1, beerFitDatabase.getOrdinal("Measurements", "type", "time"));
        assertEquals(2, beerFitDatabase.getOrdinal("Measurements", "unit", "kilometers"));
        db.execSQL("INSERT INTO Measurements VALUES(5,'time','hours')");
        assertEquals(5, beerFitDatabase.getOrdinal("Measurements", "unit", "hours"));
        db.execSQL("INSERT INTO Measurements VALUES(null,'time','seconds')");
        assertEquals(6, beerFitDatabase.getOrdinal("Measurements", "unit", "seconds"));
        // new data lookup
        beerFitDatabase.logBeer();
        assertEquals(1, beerFitDatabase.getOrdinal("ActivityLog", "amount", "1"));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));

    }

    @Test
    public void logActivityTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logActivity("Running", "seconds", 12.2);
        beerFitDatabase.logActivity("Ran", "minutes", 30);
        Cursor res = db.rawQuery("SELECT * FROM ActivityLog;", null);
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertTrue(res.getString(1).matches(DATETIME_FORMAT));
        assertEquals(-1, res.getInt(2));
        assertEquals(-1, res.getInt(3));
        assertEquals(12.2, res.getDouble(4), 0);
        res.moveToNext();
        assertEquals(2, res.getInt(0));
        assertTrue(res.getString(1).matches(DATETIME_FORMAT));
        assertEquals(2, res.getInt(2));
        assertEquals(1, res.getInt(3));
        assertEquals(30, res.getDouble(4), 0);
        res.moveToNext();
        assertTrue(res.isAfterLast());
        res.close();
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void logBeerTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logBeer();
        Cursor res = db.rawQuery("SELECT * FROM ActivityLog;", null);
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertTrue(res.getString(1).matches(DATETIME_FORMAT));
        assertEquals(0, res.getInt(2));
        assertEquals(0, res.getInt(3));
        assertEquals(1, res.getDouble(4), 0);
        res.moveToNext();
        assertTrue(res.isAfterLast());
        res.close();
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getBeersDrankTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
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
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getBeersEarnedTest() {
        //TODO - this will change (and need to) once goals become dynamic
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertEquals(0, beerFitDatabase.getBeersEarned(), 0);
        beerFitDatabase.logActivity("Ran", "kilometers", 5);
        assertEquals(1, beerFitDatabase.getBeersEarned(), 0);
        beerFitDatabase.logActivity("Walked", "kilometers", 32);
        assertEquals(7.4, beerFitDatabase.getBeersEarned(), 0);
        beerFitDatabase.logActivity("Played Soccer", "kilometers", 32);
        assertEquals(7.4, beerFitDatabase.getBeersEarned(), 0);
        beerFitDatabase.logActivity("Soccered", "minutes", 30);
        assertEquals(7.4, beerFitDatabase.getBeersEarned(), 0);
        beerFitDatabase.logActivity("Played Soccer", "minutes", 33);
        assertEquals(8.5, beerFitDatabase.getBeersEarned(), 0);
        beerFitDatabase.logActivity("Lifted", "minutes", 15);
        assertEquals(9.0, beerFitDatabase.getBeersEarned(), 0);
        beerFitDatabase.logActivity("Cycled", "minutes", 15);
        assertEquals(9.0, beerFitDatabase.getBeersEarned(), 0);
        beerFitDatabase.logActivity("Cycled", "kilometers", 15);
        assertEquals(10.5, beerFitDatabase.getBeersEarned(), 0);
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getBeersRemainingTest() {
        //TODO - this will change (and need to) once goals become dynamic
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        assertEquals(0, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity("Ran", "kilometers", 5);
        assertEquals(1, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity("Walked", "kilometers", 32);
        assertEquals(7, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity("Played Soccer", "kilometers", 32);
        assertEquals(7, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity("Soccered", "minutes", 30);
        assertEquals(7, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity("Played Soccer", "minutes", 33);
        beerFitDatabase.logBeer();
        assertEquals(7, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity("Lifted", "minutes", 15);
        beerFitDatabase.logBeer();
        beerFitDatabase.logBeer();
        assertEquals(6.0, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity("Cycled", "minutes", 15);
        assertEquals(6.0, beerFitDatabase.getBeersRemaining(), 0);
        beerFitDatabase.logActivity("Cycled", "kilometers", 15);
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
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void removeActivityTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logActivity("Running", "seconds", 12.2);
        beerFitDatabase.logActivity("Ran", "minutes", 30);
        beerFitDatabase.removeActivity(1);
        Cursor cursor = db.rawQuery("SELECT * FROM ActivityLog", null);
        assertEquals(1, cursor.getCount());
        cursor.close();
        beerFitDatabase.removeActivity(1);
        cursor = db.rawQuery("SELECT * FROM ActivityLog", null);
        assertEquals(1, cursor.getCount());
        cursor.close();
        beerFitDatabase.removeActivity(2);
        cursor = db.rawQuery("SELECT * FROM ActivityLog", null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }

    @Test
    public void getActivityTimeTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        SQLiteDatabase db = helper.getWritableDatabase();
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(db);
        beerFitDatabase.setupDatabase();
        beerFitDatabase.logActivity("Running", "seconds", 12.2);
        assertTrue(beerFitDatabase.getActivityTime(1).matches(DATETIME_FORMAT));
        assertEquals("Unknown", beerFitDatabase.getActivityTime(0));
        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
    }
}
