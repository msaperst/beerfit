package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Test;

import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,1,2,5);");
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
        assertEquals(-1, goal.getId());
        assertNull(goal.getExercise());
        assertNull(goal.getMeasurement());
        assertEquals(0.0, goal.getAmount(), 0.00001);
    }

    @Test
    public void saveExistingGoal() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,5);");
        Goal goal = new Goal(db, 1);
        goal.setAmount(10);
        goal.save();
        Cursor res = db.rawQuery("SELECT * FROM " + GOALS_TABLE, null);
        assertEquals(1, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals(2, res.getInt(1));
        assertEquals(2, res.getInt(2));
        assertEquals(10.0, res.getDouble(3), 0.0);
    }

    @Test
    public void saveNewGoal() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Goal goal = new Goal(db);
        goal.setExercise(new Exercise(db, 1));
        goal.setMeasurement(new Measurement(db, 2));
        goal.setAmount(10);
        goal.save();
        Cursor res = db.rawQuery("SELECT * FROM " + GOALS_TABLE, null);
        assertEquals(1, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals(1, res.getInt(1));
        assertEquals(2, res.getInt(2));
        assertEquals(10.0, res.getDouble(3), 0.0);
    }

    @Test
    public void deleteGoal() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,5);");
        Goal goal = new Goal(db, 1);
        goal.delete();
        Cursor res = db.rawQuery("SELECT * FROM " + GOALS_TABLE, null);
        assertEquals(0, res.getCount());
        assertFalse(res.moveToFirst());
    }
}
