package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.mockito.Mock;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MeasurementUnitTest {
    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);

    @Mock
    Cursor mockedCursor = mock(Cursor.class);

    @Test
    public void measurementNotFoundTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(null);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        assertEquals(-1, measurement.getId());
        assertNull(measurement.getType());
        assertNull(measurement.getUnit());
        assertEquals(-1, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementExistsTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        assertEquals(1, measurement.getId());
        assertEquals("distance", measurement.getType());
        assertEquals("kilometer", measurement.getUnit());
        assertEquals(1.0, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementNotExistsTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        assertEquals(-1, measurement.getId());
        assertNull(measurement.getType());
        assertNull(measurement.getUnit());
        assertEquals(-1, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementByIdNotFoundTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(null);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, 1);
        assertEquals(-1, measurement.getId());
        assertNull(measurement.getType());
        assertNull(measurement.getUnit());
        assertEquals(-1, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementByIdExistsTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, 1);
        assertEquals(1, measurement.getId());
        assertEquals("distance", measurement.getType());
        assertEquals("kilometer", measurement.getUnit());
        assertEquals(1.0, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementByIdNotExistsTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, 1);
        assertEquals(-1, measurement.getId());
        assertNull(measurement.getType());
        assertNull(measurement.getUnit());
        assertEquals(-1, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementByIdBeerTest() {
        Measurement measurement = new Measurement(mockedSQLiteDatabase, 0);
        assertEquals(0, measurement.getId());
        assertNull(measurement.getType());
        assertEquals("beer", measurement.getUnit());
        assertEquals(-1, measurement.getConversion(), 0.00001);
    }

    @Test
    public void existingMeasurementCurrentUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        assertTrue(measurement.isUnique());
    }

    @Test
    public void newMeasurementCurrentBadUnique() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer' AND id != -1", null)).thenReturn(null);

        Measurement measurement = new Measurement(mockedSQLiteDatabase);
        measurement.setUnit("kilometer");
        assertTrue(measurement.isUnique());
    }

    @Test
    public void newMeasurementCurrentUnique() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer' AND id != -1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase);
        measurement.setUnit("kilometer");
        assertTrue(measurement.isUnique());
    }

    @Test
    public void newMeasurementCurrentNotUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer' AND id != -1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase);
        measurement.setUnit("kilometer");
        assertFalse(measurement.isUnique());
    }

    @Test
    public void safeToEditNew() {
        Measurement measurement = new Measurement(mockedSQLiteDatabase);
        assertTrue(measurement.safeToEdit());
    }

    @Test
    public void notSafeToEdit() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        assertFalse(measurement.safeToEdit());
    }

    @Test
    public void safeToDeleteNull() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE measurement = 0;", null)).thenReturn(null);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE measurement = 0;", null)).thenReturn(null);

        Measurement measurement = new Measurement(mockedSQLiteDatabase);
        assertTrue(measurement.safeToDelete());
    }

    @Test
    public void safeToDeleteExistingExerciseNotInEither() {
        when(mockedCursor.getCount()).thenReturn(1, 0);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE measurement = 1;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE measurement = 1;", null)).thenReturn(mockedCursor);
        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        assertTrue(measurement.safeToDelete());
    }

    @Test
    public void safeToDeleteExistingExerciseNotInGoalsInActivity() {
        when(mockedCursor.getCount()).thenReturn(1, 0, 1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE measurement = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE measurement = 1", null)).thenReturn(mockedCursor);
        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        assertFalse(measurement.safeToDelete());
    }

    @Test
    public void notSafeToDeleteExistingExerciseInGoals() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE measurement = 1", null)).thenReturn(mockedCursor);
        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        assertFalse(measurement.safeToDelete());
    }

}
