package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.mockito.Mock;

import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
}
