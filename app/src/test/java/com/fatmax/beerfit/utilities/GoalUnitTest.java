package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.junit.Test;
import org.mockito.Mock;

import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoalUnitTest {
    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);

    @Mock
    Cursor mockedCursor = mock(Cursor.class);

    @Test
    public void goalByIdNotFoundTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE id = 1", null)).thenReturn(null);

        Goal goal = new Goal(mockedSQLiteDatabase, 1);
        assertEquals(-1, goal.getId());
        assertNull(goal.getExercise());
        assertNull(goal.getMeasurement());
        assertEquals(0.0, goal.getAmount(), 0.0001);
    }

    @Test
    public void goalByIdExistsTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getInt(1)).thenReturn(1);
        when(mockedCursor.getInt(2)).thenReturn(2);
        when(mockedCursor.getDouble(3)).thenReturn(0.2);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Goal goal = new Goal(mockedSQLiteDatabase, 1);
        assertEquals(1, goal.getId());
        assertEquals(-1, goal.getExercise().getId());
        assertEquals(-1, goal.getMeasurement().getId());
        assertEquals(0.2, goal.getAmount(), 0.0001);
    }

    @Test
    public void goalByIdNotExistsTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Goal goal = new Goal(mockedSQLiteDatabase, 1);
        assertEquals(-1, goal.getId());
        assertNull(goal.getExercise());
        assertNull(goal.getMeasurement());
        assertEquals(0.0, goal.getAmount(), 0.0001);
    }

    @Test
    public void newGoalTest() {
        Goal goal = new Goal(mockedSQLiteDatabase);
        assertEquals(-1, goal.getId());
        assertNull(goal.getExercise());
        assertNull(goal.getMeasurement());
        assertEquals(0.0, goal.getAmount(), 0.0001);
    }

    @Test
    public void getStringEmptyGoal() {
        Goal goal = new Goal(mockedSQLiteDatabase);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> goal.getString());
        assertNull(exception.getMessage());
    }

    @Test
    public void getStringSingular() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked", "distance");
        when(mockedCursor.getString(2)).thenReturn("Walk", "kilometer");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Goal goal = new Goal(mockedSQLiteDatabase);
        goal.setExercise(exercise);
        goal.setMeasurement(measurement);
        goal.setAmount(1);
        assertEquals("Walk for 1.0 kilometer", goal.getString());
    }

    @Test
    public void getStringPlural() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked", "distance");
        when(mockedCursor.getString(2)).thenReturn("Walk", "kilometer");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Goal goal = new Goal(mockedSQLiteDatabase);
        goal.setExercise(exercise);
        goal.setMeasurement(measurement);
        goal.setAmount(10);
        assertEquals("Walk for 10.0 kilometers", goal.getString());
    }
}
