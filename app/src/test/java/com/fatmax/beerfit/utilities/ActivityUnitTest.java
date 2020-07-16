package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.junit.Test;
import org.mockito.Mock;

import java.text.ParseException;
import java.util.Date;

import static com.fatmax.beerfit.utilities.Activity.DATE_TIME_FORMAT;
import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void isBeerDrankBeers() {
        Measurement measurement = new Measurement(mockedSQLiteDatabase, 0);
        Exercise exercise = new Exercise(mockedSQLiteDatabase, 0);
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setMeasurement(measurement);
        activity.setExercise(exercise);
        assertTrue(activity.isDrankBeer());
    }

    @Test
    public void isBeerDrankExerciseMeasurement() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance", "Walked");
        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk");
        when(mockedCursor.isAfterLast()).thenReturn(false, true, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setMeasurement(measurement);
        activity.setExercise(exercise);
        assertFalse(activity.isDrankBeer());
    }

    @Test
    public void isBeerDrankMeasurement() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, 0);
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setMeasurement(measurement);
        activity.setExercise(exercise);
        assertFalse(activity.isDrankBeer());
    }

    @Test
    public void isBeerDrankExercise() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, 0);
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setMeasurement(measurement);
        activity.setExercise(exercise);
        assertFalse(activity.isDrankBeer());
    }

    @Test
    public void getStringBeerTest() {
        Measurement measurement = new Measurement(mockedSQLiteDatabase, 0);
        Exercise exercise = new Exercise(mockedSQLiteDatabase, 0);
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setMeasurement(measurement);
        activity.setExercise(exercise);
        activity.setAmount(1);
        assertEquals("Drank 1.0 beer", activity.getString());
    }

    @Test
    public void getStringBeersTest() {
        Measurement measurement = new Measurement(mockedSQLiteDatabase, 0);
        Exercise exercise = new Exercise(mockedSQLiteDatabase, 0);
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setMeasurement(measurement);
        activity.setExercise(exercise);
        activity.setAmount(10);
        assertEquals("Drank 10.0 beers", activity.getString());
    }

    @Test
    public void getStringActivityTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance", "Walked");
        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk");
        when(mockedCursor.isAfterLast()).thenReturn(false, true, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setMeasurement(measurement);
        activity.setExercise(exercise);
        activity.setAmount(1);
        assertEquals("Walked for 1.0 kilometer", activity.getString());
    }

    @Test
    public void getStringActivityPluralTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance", "Walked");
        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk");
        when(mockedCursor.isAfterLast()).thenReturn(false, true, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setMeasurement(measurement);
        activity.setExercise(exercise);
        activity.setAmount(1.02);
        assertEquals("Walked for 1.02 kilometers", activity.getString());
    }

    @Test
    public void getBeersEarnedNullTest() {
        Activity activity = new Activity(mockedSQLiteDatabase, 0);
        assertEquals(-1, activity.getBeers(), 0);
        activity.calculateBeers();
        assertEquals(0, activity.getBeers(), 0);
    }

    @Test
    public void getBeersEarnedOverrideBeersTest() {
        Activity activity = new Activity(mockedSQLiteDatabase, 0);
        activity.setBeers(10.0);
        activity.calculateBeers();
        assertEquals(0, activity.getBeers(), 0);
    }

    @Test
    public void getBeersEarnedMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance", "Walked", "distance", "Walked", "distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk", "kilometer", "Walk", "kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedCursor.getInt(1)).thenReturn(1);
        when(mockedCursor.getInt(2)).thenReturn(1);
        when(mockedCursor.isAfterLast()).thenReturn(false, true, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE + " WHERE exercise = 1 AND measurement = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setMeasurement(measurement);
        activity.setExercise(exercise);
        activity.setAmount(10);
        activity.calculateBeers();
        assertEquals(10.0, activity.getBeers(), 0);
    }

    @Test
    public void getDateNullTest() {
        Activity activity = new Activity(mockedSQLiteDatabase);
        assertNull(activity.getDate());
    }

    @Test
    public void getDateTest() {
        Activity activity = new Activity(mockedSQLiteDatabase);
        Date now = new Date();
        now.setTime(1234567890);
        activity.setDateTime(now);
        assertEquals("1970-01-15", activity.getDate());
    }

    @Test
    public void getTimeNullTest() {
        Activity activity = new Activity(mockedSQLiteDatabase);
        assertNull(activity.getTime());
    }

    @Test
    public void getTimeTest() {
        Activity activity = new Activity(mockedSQLiteDatabase);
        Date now = new Date();
        activity.setDateTime(now);
        now.setTime(1234567890);
        assertEquals("01:56", activity.getTime());
    }
}
