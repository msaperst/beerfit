package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.junit.After;
import org.junit.Test;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ActivityInstrumentedTest {
    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void activityExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db, "Walk");
        assertEquals(1, activity.getId());
        assertEquals("Walk", activity.getCurrent());
        assertEquals("Walked", activity.getPast());
        assertEquals(Color.GREEN, activity.getColor());
    }

    @Test
    public void activityNotExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db, "walk");
        assertEquals(0, activity.getId());
        assertNull(activity.getCurrent());
        assertNull(activity.getPast());
        // not checking color, as it's random
    }

    @Test
    public void existingActivityUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db, "Walk");
        assertTrue(activity.isActivityUnique());
    }

    @Test
    public void newActivityUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db);
        activity.setCurrent("Wulk");
        assertTrue(activity.isActivityUnique());
    }

    @Test
    public void newActivityNotUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db);
        activity.setCurrent("Walk");
        assertFalse(activity.isActivityUnique());
    }

    @Test
    public void existingColorUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db, "Walk");
        assertTrue(activity.isColorUnique());
    }

    @Test
    public void newColorUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db);
        activity.setColor(1234);
        assertTrue(activity.isColorUnique());
    }

    @Test
    public void newColorNotUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db);
        activity.setColor(Color.GREEN);
        assertFalse(activity.isColorUnique());
    }

    @Test
    public void saveExistingActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db, "Walk");
        activity.setColor(Color.BLACK);
        activity.saveActivity();
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + ";", null);
        assertEquals(5, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals("Walked", res.getString(1));
        assertEquals("Walk", res.getString(2));
        assertEquals(Color.BLACK, res.getInt(3));
    }

    @Test
    public void saveNewActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db);
        activity.setCurrent("Wulk");
        activity.setPast("Wulked");
        activity.setColor(Color.BLACK);
        activity.saveActivity();
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + ";", null);
        assertEquals(6, res.getCount());
        res.moveToLast();
        assertEquals(6, res.getInt(0));
        assertEquals("Wulked", res.getString(1));
        assertEquals("Wulk", res.getString(2));
        assertEquals(Color.BLACK, res.getInt(3));
    }

    @Test
    public void safeToDeleteExistingActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db, "Walk");
        assertTrue(activity.safeToDelete());
    }

    @Test
    public void safeToDeleteNonExistingActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db);
        activity.setCurrent("Wulk");
        activity.setPast("Wulked");
        activity.setColor(Color.BLACK);
        assertTrue(activity.safeToDelete());
    }

    @Test
    public void notSafeToDeleteExistingActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2020-01-01 00:00", "Walked", "kilometers", 5);
        Activity activity = new Activity(db, "Walk");
        assertFalse(activity.safeToDelete());
    }

    @Test
    public void deleteActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db, "Walk");
        activity.deleteActivity();
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + ";", null);
        assertEquals(4, res.getCount());
        res.moveToFirst();
        assertEquals(2, res.getInt(0));
    }

    @Test
    public void unableToDeleteActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2020-01-01 00:00", "Walked", "kilometers", 5);
        Activity activity = new Activity(db, "Walk");
        activity.deleteActivity();
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + ";", null);
        assertEquals(5, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
    }
}
