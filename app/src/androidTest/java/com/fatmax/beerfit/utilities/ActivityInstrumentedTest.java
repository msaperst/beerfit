package com.fatmax.beerfit.utilities;

import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Test;

import java.text.ParseException;

import static com.fatmax.beerfit.utilities.Activity.DATE_TIME_FORMAT;
import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;
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
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(2,'2020-10-10 10:23',1,2,5,0);");
        Activity activity = new Activity(db, 1);
        assertEquals(1, activity.getId());
        assertEquals(DATE_TIME_FORMAT.parse("2020-10-10 10:23"), activity.getDateTime());
        assertEquals("2020-10-10", activity.getDate());
        assertEquals("10:23", activity.getTime());
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
        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(2,'2020-10-10 10:23',1,2,5,1);");
        Activity activity = new Activity(db, 1);
        assertEquals(1, activity.getId());
        assertEquals(DATE_TIME_FORMAT.parse("2020-10-10 10:23"), activity.getDateTime());
        assertEquals("2020-10-10", activity.getDate());
        assertEquals("10:23", activity.getTime());
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
        Activity activity = new Activity(db, 0);
        assertEquals(-1, activity.getId());
        assertNull(activity.getDateTime());
        assertNull(activity.getDate());
        assertNull(activity.getTime());
        assertNull(activity.getExercise());
        assertNull(activity.getMeasurement());
        assertEquals(0, activity.getAmount(), 0.0001);
        assertEquals(0, activity.getBeers(), 0.0001);
    }

    //TODO - fix these

//    @Test
//    public void getBeersEarnedTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        addGoals(db);
//        assertEquals(1, database.getBeersEarned(new Exercise(db, "Ran"), new Measurement(db, "kilometer"), 5), 0);
//        assertEquals(0, database.getBeersEarned(new Exercise(db, "Ran"), new Measurement(db, "kilometer"), 0), 0);
//        assertEquals(1, database.getBeersEarned(new Exercise(db, "Walked"), new Measurement(db, "kilometer"), 5), 0);
//        assertEquals(0.5, database.getBeersEarned(new Exercise(db, "Played Soccer"), new Measurement(db, "minute"), 15), 0);
//        assertEquals(0, database.getBeersEarned(new Exercise(db, "Played Soccer"), new Measurement(db, "kilometer"), 5), 0);
//        wipeOutDB();
//    }
//
//    @Test
//    public void getBeersEarnedSwapTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,5);");
//        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(2,1,1,10);");
//        assertEquals(1, database.getBeersEarned(new Exercise(db, "Ran"), new Measurement(db, "kilometer"), 5), 0);
//        assertEquals(0.9656063879, database.getBeersEarned(new Exercise(db, "Ran"), new Measurement(db, "mile"), 3), 0.00001);
//        assertEquals(3, database.getBeersEarned(new Exercise(db, "Walk"), new Measurement(db, "minute"), 30), 0);
//        assertEquals(2, database.getBeersEarned(new Exercise(db, "Walk"), new Measurement(db, "second"), 1200), 0);
//        assertEquals(1.5, database.getBeersEarned(new Exercise(db, "Walk"), new Measurement(db, "hour"), 0.25), 0);
//        wipeOutDB();
//    }
}
