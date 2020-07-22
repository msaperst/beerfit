package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static com.fatmax.beerfit.utilities.Activity.DATE_TIME_FORMAT;
import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ActivityInstrumentedTest {

    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void activityByIdExistsTest() throws ParseException {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-10-10 10:23',1,2,5,0);");
        Activity activity = new Activity(db, 1);
        assertEquals(1, activity.getId());
        assertEquals(DATE_TIME_FORMAT.parse("2020-10-10 10:23"), activity.getDateTime());
        assertEquals("2020-10-10", activity.getDate());
        assertEquals("10:23", activity.getTime());
        assertEquals("Sat, Oct 10 2020, 10:23", activity.getStringDateTime());
        assertEquals(new Exercise(db, 1).getId(), activity.getExercise().getId());
        assertEquals(new Measurement(db, 2).getId(), activity.getMeasurement().getId());
        assertEquals(5, activity.getAmount(), 0.0001);
        assertEquals(0, activity.getBeers(), 0.0001);
    }

    @Test
    public void activityByIdExistsWithGoalTest() throws ParseException {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,1,2,5);");
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-10-10 10:23',1,2,5,1);");
        Activity activity = new Activity(db, 1);
        assertEquals(1, activity.getId());
        assertEquals(DATE_TIME_FORMAT.parse("2020-10-10 10:23"), activity.getDateTime());
        assertEquals("2020-10-10", activity.getDate());
        assertEquals("10:23", activity.getTime());
        assertEquals("Sat, Oct 10 2020, 10:23", activity.getStringDateTime());
        assertEquals(new Exercise(db, 1).getId(), activity.getExercise().getId());
        assertEquals(new Measurement(db, 2).getId(), activity.getMeasurement().getId());
        assertEquals(5, activity.getAmount(), 0.0001);
        assertEquals(1, activity.getBeers(), 0.0001);
    }

    @Test
    public void activityByIdNotExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db, 99);
        assertEquals(-1, activity.getId());
        assertNull(activity.getDateTime());
        assertNull(activity.getDate());
        assertNull(activity.getTime());
        assertNull(activity.getStringDateTime());
        assertNull(activity.getExercise());
        assertNull(activity.getMeasurement());
        assertEquals(0, activity.getAmount(), 0.0001);
        assertEquals(0, activity.getBeers(), 0.0001);
    }

    @Test
    public void activityByBeer() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Date date = new Date();
        Activity activity = new Activity(db, 0);
        assertEquals(-1, activity.getId());
        assertEquals(date.toString(), activity.getDateTime().toString());
        assertEquals(0, activity.getExercise().getId());
        assertEquals(0, activity.getMeasurement().getId());
        assertEquals(1, activity.getAmount(), 0.0001);
        assertEquals(-1, activity.getBeers(), 0.0001);
    }

    @Test
    public void getStringBeer() {
        SQLiteDatabase db = getDB();
        Activity activity = new Activity(db, 0);
        assertEquals("Drank 1 beer", activity.getString());
    }

    @Test
    public void getStringBeers() {
        SQLiteDatabase db = getDB();
        Activity activity = new Activity(db, 0);
        activity.setAmount(0.9999);
        assertEquals("Drank 0 beers", activity.getString());
    }

    @Test
    public void getStringBeerBarely() {
        SQLiteDatabase db = getDB();
        Activity activity = new Activity(db, 0);
        activity.setAmount(1.000001);
        assertEquals("Drank 1 beer", activity.getString());
    }

    @Test
    public void getStringKilometer() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-01-01 00:00',1,2,1,2);");
        Activity activity = new Activity(db, 1);
        assertEquals("Walked for 1.0 kilometer", activity.getString());
    }

    @Test
    public void getStringHours() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-01-01 00:00',1,4,5,2);");
        Activity activity = new Activity(db, 1);
        assertEquals("Walked for 5.0 hours", activity.getString());
    }

    @Test
    public void calculateBeersNoGoals() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-01-01 00:00',2,2,5,2);");
        Activity activity = new Activity(db, 1);
        activity.calculateBeers();
        assertEquals(0, activity.getBeers(), 0);
    }

    @Test
    public void getMatchingGoalsExerciseSingleMatch() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,1);");
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-01-01 00:00',2,2,5,2);");
        Activity activity = new Activity(db, 1);
        activity.calculateBeers();
        assertEquals(5, activity.getBeers(), 0);
    }

    @Test
    public void getMatchingGoalsExerciseOppositeMatch() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,1);");
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-01-01 00:00',2,5,5,2);");
        Activity activity = new Activity(db, 1);
        activity.calculateBeers();
        assertEquals(8.046719899473937, activity.getBeers(), 0);
    }

    @Test
    public void saveExistingActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-01-01 00:00',1,2,5,2);");
        Activity activity = new Activity(db, 1);
        activity.setAmount(10);
        activity.save();
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null);
        assertEquals(1, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals("2020-01-01 00:00", res.getString(1));
        assertEquals(1, res.getInt(2));
        assertEquals(2, res.getInt(3));
        assertEquals(10.0, res.getDouble(4), 0.0);
        assertEquals(2.0, res.getDouble(5), 0.0);
    }

    @Test
    public void saveNewActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity activity = new Activity(db);
        activity.setExercise(new Exercise(db, 1));
        activity.setMeasurement(new Measurement(db, 2));
        activity.setDateTime(new Date());
        activity.setAmount(10);
        activity.save();
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null);
        assertEquals(1, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals(1, res.getInt(2));
        assertEquals(2, res.getInt(3));
        assertEquals(10.0, res.getDouble(4), 0.0);
        assertEquals(0.0, res.getDouble(5), 0.0);
    }

    @Test
    public void deleteActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-01-01 00:00',1,2,5,2);");
        Activity activity = new Activity(db, 1);
        activity.delete();
        Cursor res = db.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE, null);
        assertEquals(0, res.getCount());
        assertFalse(res.moveToFirst());
    }
}
