package com.fatmax.beerfit.utilities;

import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Test;

import java.text.ParseException;

import static com.fatmax.beerfit.utilities.Activity.DATE_TIME_FORMAT;
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
        database.logActivity("2020-10-10 10:23", "Walked", "kilometers", 5.0);
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
        database.addGoal("Walk", "kilometers", 5.0);
        database.logActivity("2020-10-10 10:23", "Walked", "kilometers", 5.0);
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
        assertEquals(0, activity.getId());
        assertNull(activity.getDateTime());
        assertNull(activity.getDate());
        assertNull(activity.getTime());
        assertNull(activity.getExercise());
        assertNull(activity.getMeasurement());
        assertEquals(0, activity.getAmount(), 0.0001);
        assertEquals(0, activity.getBeers(), 0.0001);
    }
}
