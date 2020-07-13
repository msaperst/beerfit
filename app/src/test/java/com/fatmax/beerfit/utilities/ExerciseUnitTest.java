package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.junit.Test;
import org.mockito.Mock;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExerciseUnitTest {

    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);

    @Mock
    Cursor mockedCursor = mock(Cursor.class);

    @Test
    public void exerciseNotFoundTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(null);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        assertEquals(0, exercise.getId());
        assertNull(exercise.getCurrent());
        assertNull(exercise.getPast());
        // not checking color, as it's random
    }

    @Test
    public void exerciseExistsTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        assertEquals(1, exercise.getId());
        assertEquals("Walk", exercise.getCurrent());
        assertEquals("Walked", exercise.getPast());
        assertEquals(Color.GREEN, exercise.getColor());
    }

    @Test
    public void exerciseNotExistsTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'walk';", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "walk");
        assertEquals(0, exercise.getId());
        assertNull(exercise.getCurrent());
        assertNull(exercise.getPast());
        // not checking color, as it's random
    }

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

    @Test
    public void exerciseNotExistsSetPastTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "walk");
        exercise.setPast("wulked");
        assertEquals("wulked", exercise.getPast());
    }

    @Test
    public void existingExerciseCurrentUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        assertTrue(exercise.isCurrentUnique());
    }

    @Test
    public void newExerciseCurrentBadUnique() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Wulk' AND id != 0;", null)).thenReturn(null);

        Exercise exercise = new Exercise(mockedSQLiteDatabase);
        exercise.setCurrent("Wulk");
        assertTrue(exercise.isCurrentUnique());
    }

    @Test
    public void newExerciseCurrentUnique() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Wulk' AND id != 0;", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase);
        exercise.setCurrent("Wulk");
        assertTrue(exercise.isCurrentUnique());
    }

    @Test
    public void newExerciseCurrentNotUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' AND id != 0;", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase);
        exercise.setCurrent("Walk");
        assertFalse(exercise.isCurrentUnique());
    }

    @Test
    public void existingExercisePastUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE past = 'Walked';", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        assertTrue(exercise.isPastUnique());
    }

    @Test
    public void newExercisePastBadUnique() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE past = 'Wulked' AND id != 0;", null)).thenReturn(null);

        Exercise exercise = new Exercise(mockedSQLiteDatabase);
        exercise.setPast("Wulked");
        assertTrue(exercise.isPastUnique());
    }

    @Test
    public void newExercisePastUnique() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE past = 'Wulked' AND id != 0;", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase);
        exercise.setPast("Wulked");
        assertTrue(exercise.isPastUnique());
    }

    @Test
    public void newExercisePastNotUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE past = 'Walked' AND id != 0;", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase);
        exercise.setPast("Walked");
        assertFalse(exercise.isPastUnique());
    }

    @Test
    public void colorBadUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE color = '" + Color.GREEN + "' AND id != 1;", null)).thenReturn(null);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        assertTrue(exercise.isColorUnique());
    }

    @Test
    public void existingColorUnique() {
        when(mockedCursor.getCount()).thenReturn(1, 0);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE color = '" + Color.GREEN + "' AND id != 1;", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        assertTrue(exercise.isColorUnique());
    }

    @Test
    public void newColorUnique() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE color = '1234' AND id != 0;", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase);
        exercise.setColor(1234);
        assertTrue(exercise.isColorUnique());
    }

    @Test
    public void newColorNotUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE color = '" + Color.GREEN + "' AND id != 0;", null)).thenReturn(mockedCursor);

        Exercise exercise = new Exercise(mockedSQLiteDatabase);
        exercise.setColor(Color.GREEN);
        assertFalse(exercise.isColorUnique());
    }

    @Test
    public void safeToDeleteNull() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE exercise = 0;", null)).thenReturn(null);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE exercise = 0;", null)).thenReturn(null);

        Exercise exercise = new Exercise(mockedSQLiteDatabase);
        assertTrue(exercise.safeToDelete());
    }

    @Test
    public void safeToDeleteExistingExerciseNotInEither() {
        when(mockedCursor.getCount()).thenReturn(1, 0);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE exercise = 1;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE exercise = 1;", null)).thenReturn(mockedCursor);
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        assertTrue(exercise.safeToDelete());
    }

    @Test
    public void safeToDeleteExistingExerciseNotInGoalsInActivity() {
        when(mockedCursor.getCount()).thenReturn(1, 0, 1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE exercise = 1;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE exercise = 1;", null)).thenReturn(mockedCursor);
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        assertFalse(exercise.safeToDelete());
    }

    @Test
    public void notSafeToDeleteExistingExerciseInGoals() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE exercise = 1;", null)).thenReturn(mockedCursor);
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        assertFalse(exercise.safeToDelete());
    }
}
