package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.mockito.Mock;

import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MeasurementUnitTest {
    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);

    @Mock
    Cursor mockedCursor = mock(Cursor.class);

    @Test
    public void measurementByIdNotFoundTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(null);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, 1);
        assertEquals(0, measurement.getId());
        assertNull(measurement.getType());
        assertNull(measurement.getUnit());
    }

    @Test
    public void measurementByIdExistsTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometers");
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, 1);
        assertEquals(1, measurement.getId());
        assertEquals("distance", measurement.getType());
        assertEquals("kilometers", measurement.getUnit());
    }

    @Test
    public void measurementByIdNotExistsTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, 1);
        assertEquals(0, measurement.getId());
        assertNull(measurement.getType());
        assertNull(measurement.getUnit());
    }
}
