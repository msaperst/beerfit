package com.fatmax.beerfit.utilities;

import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Test;

import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GoalInstrumentedTest {

    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void goalExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.addGoal("Walk", "kilometers", 5.0);
        Goal goal = new Goal(db, 1);
        assertEquals(1, goal.getId());
        assertEquals(new Exercise(db, 1).getId(), goal.getExercise().getId());
        assertEquals(new Measurement(db, 2).getId(), goal.getMeasurement().getId());
        assertEquals(5, goal.getAmount(), 0.00001);
    }

    @Test
    public void goalNotExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Goal goal = new Goal(db, 1);
        assertEquals(0, goal.getId());
        assertNull(goal.getExercise());
        assertNull(goal.getMeasurement());
        assertEquals(0.0, goal.getAmount(), 0.00001);
    }
}
