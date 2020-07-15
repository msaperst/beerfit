package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.junit.After;
import org.junit.Test;

import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ExerciseInstrumentedTest {

    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void exerciseExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, "Walk");
        assertEquals(1, exercise.getId());
        assertEquals("Walk", exercise.getCurrent());
        assertEquals("Walked", exercise.getPast());
        assertEquals(Color.GREEN, exercise.getColor());
    }

    @Test
    public void exerciseNotExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, "walk");
        assertEquals(-1, exercise.getId());
        assertNull(exercise.getCurrent());
        assertNull(exercise.getPast());
        // not checking color, as it's random
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
        assertEquals(-1, exercise.getId());
        assertNull(exercise.getCurrent());
        assertNull(exercise.getPast());
        // not checking color, as it's random
    }

    @Test
    public void existingExerciseCurrentUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, "Walk");
        assertTrue(exercise.isCurrentUnique());
    }

    @Test
    public void newExerciseCurrentUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db);
        exercise.setCurrent("Wulk");
        assertTrue(exercise.isCurrentUnique());
    }

    @Test
    public void newExerciseCurrentNotUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db);
        exercise.setCurrent("Walk");
        assertFalse(exercise.isCurrentUnique());
    }

    @Test
    public void existingExercisePastUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, "Walk");
        assertTrue(exercise.isPastUnique());
    }

    @Test
    public void newExercisePastUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db);
        exercise.setPast("Wulked");
        assertTrue(exercise.isPastUnique());
    }

    @Test
    public void newExercisePastNotUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db);
        exercise.setPast("Walked");
        assertFalse(exercise.isPastUnique());
    }

    @Test
    public void existingColorUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, "Walk");
        assertTrue(exercise.isColorUnique());
    }

    @Test
    public void newColorUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db);
        exercise.setColor(1234);
        assertTrue(exercise.isColorUnique());
    }

    @Test
    public void newColorNotUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db);
        exercise.setColor(Color.GREEN);
        assertFalse(exercise.isColorUnique());
    }

    @Test
    public void saveExistingExercise() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, "Walk");
        exercise.setColor(Color.BLACK);
        exercise.save();
        Cursor res = db.rawQuery("SELECT * FROM " + EXERCISES_TABLE + ";", null);
        assertEquals(5, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals("Walked", res.getString(1));
        assertEquals("Walk", res.getString(2));
        assertEquals(Color.BLACK, res.getInt(3));
    }

    @Test
    public void saveNewExercise() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db);
        exercise.setCurrent("Wulk");
        exercise.setPast("Wulked");
        exercise.setColor(Color.BLACK);
        exercise.save();
        Cursor res = db.rawQuery("SELECT * FROM " + EXERCISES_TABLE + ";", null);
        assertEquals(6, res.getCount());
        res.moveToLast();
        assertEquals(6, res.getInt(0));
        assertEquals("Wulked", res.getString(1));
        assertEquals("Wulk", res.getString(2));
        assertEquals(Color.BLACK, res.getInt(3));
    }

    @Test
    public void safeToDeleteExistingExercise() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, "Walk");
        assertTrue(exercise.safeToDelete());
    }

    @Test
    public void safeToDeleteNonExistingExercise() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db);
        exercise.setCurrent("Wulk");
        exercise.setPast("Wulked");
        exercise.setColor(Color.BLACK);
        assertTrue(exercise.safeToDelete());
    }

    @Test
    public void notSafeToDeleteExistingActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2020-01-01 00:00", "Walked", "kilometer", 5);
        Exercise exercise = new Exercise(db, "Walk");
        assertFalse(exercise.safeToDelete());
    }

    @Test
    public void notSafeToDeleteExistingGoal() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.addGoal("Walk", "kilometer", 5);
        Exercise exercise = new Exercise(db, "Walk");
        assertFalse(exercise.safeToDelete());
    }

    @Test
    public void deleteExercise() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Exercise exercise = new Exercise(db, "Walk");
        exercise.delete();
        Cursor res = db.rawQuery("SELECT * FROM " + EXERCISES_TABLE + ";", null);
        assertEquals(4, res.getCount());
        res.moveToFirst();
        assertEquals(2, res.getInt(0));
    }

    @Test
    public void unableToDeleteExercise() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2020-01-01 00:00", "Walked", "kilometer", 5);
        Exercise exercise = new Exercise(db, "Walk");
        exercise.delete();
        Cursor res = db.rawQuery("SELECT * FROM " + EXERCISES_TABLE + ";", null);
        assertEquals(5, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
    }
}
