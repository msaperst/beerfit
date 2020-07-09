package com.fatmax.beerfit.utilities;

import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Test;

import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MeasurementInstrumentedTest {

    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void measurementExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, 2);
        assertEquals(2, measurement.getId());
        assertEquals("distance", measurement.getType());
        assertEquals("kilometers", measurement.getUnit());
    }

    @Test
    public void measurementNotExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, 0);
        assertEquals(0, measurement.getId());
        assertNull(measurement.getUnit());
        assertNull(measurement.getType());
    }
}
