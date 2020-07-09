package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.junit.Test;
import org.mockito.Mock;

import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExerciseUnitTest {

    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);

    @Mock
    Cursor mockedCursor = mock(Cursor.class);

    @Test
    public void exerciseByIdNotFoundTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(null);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, 1);
        assertEquals(0, exercise.getId());
        assertNull(exercise.getCurrent());
        assertNull(exercise.getPast());
        // not checking color, as it's random
    }

    @Test
    public void exerciseByIdExistsTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, 1);
        assertEquals(1, exercise.getId());
        assertEquals("Walk", exercise.getCurrent());
        assertEquals("Walked", exercise.getPast());
        assertEquals(Color.GREEN, exercise.getColor());
    }

    @Test
    public void exerciseByIdNotExistsTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 0", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, 0);
        assertEquals(0, exercise.getId());
        assertNull(exercise.getCurrent());
        assertNull(exercise.getPast());
        // not checking color, as it's random
    }
}
