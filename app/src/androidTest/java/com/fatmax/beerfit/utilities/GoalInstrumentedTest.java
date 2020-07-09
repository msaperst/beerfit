package com.fatmax.beerfit.utilities;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.junit.Test;

import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GoalInstrumentedTest {

    @Test
    public void goalExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,1,2,5);");
        Goal goal = new Goal(db, 1);
        assertEquals(1, goal.getId());
        assertEquals(new Exercise(db, 1), goal.getExercise());
        assertEquals(new Measurement(db, 2), goal.getMeasurement());
        assertEquals(5, goal.getAmount(), 0.00001);
    }

    @Test
    public void goalNotExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, 0);
        assertEquals(0, exercise.getId());
        assertNull(exercise.getCurrent());
        assertNull(exercise.getPast());
        // not checking color, as it's random
    }
}
