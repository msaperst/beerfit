package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.mockito.Mock;

import java.text.ParseException;

import static com.fatmax.beerfit.utilities.Activity.DATE_TIME_FORMAT;
import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;
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
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE id = 1", null)).thenReturn(null);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(0, activity.getId());
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
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(1, activity.getId());
        assertEquals(DATE_TIME_FORMAT.parse("2020-10-10 10:23"), activity.getDateTime());
        assertEquals("2020-10-10", activity.getDate());
        assertEquals("10:23", activity.getTime());
        assertEquals(0, activity.getExercise().getId());
        assertEquals(0, activity.getMeasurement().getId());
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
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(1, activity.getId());
        assertEquals(DATE_TIME_FORMAT.parse("2020-10-10 10:23"), activity.getDateTime());
        assertEquals("2020-10-10", activity.getDate());
        assertEquals("10:23", activity.getTime());
        assertEquals(0, activity.getExercise().getId());
        assertEquals(0, activity.getMeasurement().getId());
        assertEquals(5.0, activity.getAmount(), 0.0001);
        assertEquals(0.0, activity.getBeers(), 0.0001);
    }

    @Test
    public void activityByIdExistsBadTimeTest() throws ParseException {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("poop");
        when(mockedCursor.getInt(2)).thenReturn(1);
        when(mockedCursor.getInt(3)).thenReturn(2);
        when(mockedCursor.getDouble(4)).thenReturn(5.0);
        when(mockedCursor.getDouble(5)).thenReturn(0.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(1, activity.getId());
        assertNull(activity.getDateTime());
        assertNull(activity.getDate());
        assertNull(activity.getTime());
        assertEquals(0, activity.getExercise().getId());
        assertEquals(0, activity.getMeasurement().getId());
        assertEquals(5.0, activity.getAmount(), 0.0001);
        assertEquals(0.0, activity.getBeers(), 0.0001);
    }

    @Test
    public void activityByIdNotExistsTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, 1);
        assertEquals(0, activity.getId());
        assertNull(activity.getDateTime());
        assertNull(activity.getDate());
        assertNull(activity.getTime());
        assertNull(activity.getExercise());
        assertNull(activity.getMeasurement());
        assertEquals(0, activity.getAmount(), 0.0001);
        assertEquals(0, activity.getBeers(), 0.0001);
    }
}
