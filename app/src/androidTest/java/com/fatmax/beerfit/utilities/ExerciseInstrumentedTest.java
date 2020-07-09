package com.fatmax.beerfit.utilities;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.junit.After;
import org.junit.Test;

import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExerciseInstrumentedTest {

    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void exerciseByIdExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, 1);
        assertEquals(1, exercise.getId());
        assertEquals("Walk", exercise.getCurrent());
        assertEquals("Walked", exercise.getPast());
        assertEquals(Color.GREEN, exercise.getColor());
    }

    @Test
    public void exerciseByIdNotExistsTest() {
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
