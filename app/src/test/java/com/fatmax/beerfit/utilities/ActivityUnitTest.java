package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.mockito.Mock;

import java.text.ParseException;

import static com.fatmax.beerfit.utilities.Activity.DATE_TIME_FORMAT;
import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActivityUnitTest {
    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);

    @Mock
    Cursor mockedCursor = mock(Cursor.class);

    @Test
    public void activityByIdNotFoundTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = 1", null)).thenReturn(null);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(-1, activity.getId());
        assertNull(activity.getDateTime());
        assertNull(activity.getDate());
        assertNull(activity.getTime());
        assertNull(activity.getExercise());
        assertNull(activity.getMeasurement());
        assertEquals(0, activity.getAmount(), 0.0001);
        assertEquals(0, activity.getBeers(), 0.0001);
    }

    @Test
    public void activityByIdExistsTest() throws ParseException {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("2020-10-10 10:23");
        when(mockedCursor.getInt(2)).thenReturn(1);
        when(mockedCursor.getInt(3)).thenReturn(2);
        when(mockedCursor.getDouble(4)).thenReturn(5.0);
        when(mockedCursor.getDouble(5)).thenReturn(0.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(1, activity.getId());
        assertEquals(DATE_TIME_FORMAT.parse("2020-10-10 10:23"), activity.getDateTime());
        assertEquals("2020-10-10", activity.getDate());
        assertEquals("10:23", activity.getTime());
        assertEquals(-1, activity.getExercise().getId());
        assertEquals(-1, activity.getMeasurement().getId());
        assertEquals(5.0, activity.getAmount(), 0.0001);
        assertEquals(0.0, activity.getBeers(), 0.0001);
    }

    @Test
    public void activityByIdExistsWithSecondsTest() throws ParseException {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("2020-10-10 10:23:00");
        when(mockedCursor.getInt(2)).thenReturn(1);
        when(mockedCursor.getInt(3)).thenReturn(2);
        when(mockedCursor.getDouble(4)).thenReturn(5.0);
        when(mockedCursor.getDouble(5)).thenReturn(0.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(1, activity.getId());
        assertEquals(DATE_TIME_FORMAT.parse("2020-10-10 10:23"), activity.getDateTime());
        assertEquals("2020-10-10", activity.getDate());
        assertEquals("10:23", activity.getTime());
        assertEquals(-1, activity.getExercise().getId());
        assertEquals(-1, activity.getMeasurement().getId());
        assertEquals(5.0, activity.getAmount(), 0.0001);
        assertEquals(0.0, activity.getBeers(), 0.0001);
    }

    @Test
    public void activityByIdExistsBadTimeTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("poop");
        when(mockedCursor.getInt(2)).thenReturn(1);
        when(mockedCursor.getInt(3)).thenReturn(2);
        when(mockedCursor.getDouble(4)).thenReturn(5.0);
        when(mockedCursor.getDouble(5)).thenReturn(0.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(1, activity.getId());
        assertNull(activity.getDateTime());
        assertNull(activity.getDate());
        assertNull(activity.getTime());
        assertEquals(-1, activity.getExercise().getId());
        assertEquals(-1, activity.getMeasurement().getId());
        assertEquals(5.0, activity.getAmount(), 0.0001);
        assertEquals(0.0, activity.getBeers(), 0.0001);
    }

    @Test
    public void activityByIdNotExistsTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(-1, activity.getId());
        assertNull(activity.getDateTime());
        assertNull(activity.getDate());
        assertNull(activity.getTime());
        assertNull(activity.getExercise());
        assertNull(activity.getMeasurement());
        assertEquals(0, activity.getAmount(), 0.0001);
        assertEquals(0, activity.getBeers(), 0.0001);
    }

    //TODO - fix the below

//    @Test
//    public void getBeersEarnedNullTest() {
//        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
//        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
//        Database database = new Database(mockedSQLiteDatabase);
//        assertEquals(0.0, database.getBeersEarned(exercise, measurement, 10), 0);
//    }
//
//    @Test
//    public void getBeersEarnedMatchTest() {
//        when(mockedCursor.getCount()).thenReturn(1);
//        when(mockedCursor.getInt(0)).thenReturn(1);
//        when(mockedCursor.getString(1)).thenReturn("distance", "Walked", "distance", "Walked", "distance");
//        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk", "kilometer", "Walk", "kilometer");
//        when(mockedCursor.getDouble(3)).thenReturn(1.0);
//        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
//        when(mockedCursor.getInt(1)).thenReturn(1);
//        when(mockedCursor.getInt(2)).thenReturn(1);
//        when(mockedCursor.isAfterLast()).thenReturn(false, true, false, true);
//        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
//        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);
//        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(mockedCursor);
//        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
//        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE + " WHERE exercise = 1 AND measurement = 1", null)).thenReturn(mockedCursor);
//        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
//        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
//
//        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
//        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
//        Database database = new Database(mockedSQLiteDatabase);
//        assertEquals(10.0, database.getBeersEarned(exercise, measurement, 10), 0);
//    }
}
