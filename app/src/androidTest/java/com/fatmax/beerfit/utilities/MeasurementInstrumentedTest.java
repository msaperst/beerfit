package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Test;

import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        Measurement measurement = new Measurement(db, "kilometer");
        assertEquals(2, measurement.getId());
        assertEquals("distance", measurement.getType());
        assertEquals("kilometer", measurement.getUnit());
        assertEquals(1, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementNotExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, "walk");
        assertEquals(-1, measurement.getId());
        assertNull(measurement.getType());
        assertNull(measurement.getUnit());
        assertEquals(-1.0, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementByIdExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, 2);
        assertEquals(2, measurement.getId());
        assertEquals("distance", measurement.getType());
        assertEquals("kilometer", measurement.getUnit());
        assertEquals(1, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementByIdNotExistsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, 99);
        assertEquals(-1, measurement.getId());
        assertNull(measurement.getUnit());
        assertNull(measurement.getType());
        assertEquals(-1.0, measurement.getConversion(), 0.00001);
    }

    @Test
    public void measurementByIdBeerTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, 0);
        assertEquals(0, measurement.getId());
        assertEquals("beer", measurement.getUnit());
        assertNull(measurement.getType());
        assertEquals(-1.0, measurement.getConversion(), 0.00001);
    }

    @Test
    public void existingMeasurementCurrentUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, "kilometer");
        assertTrue(measurement.isUnique());
    }

    @Test
    public void newMeasurementCurrentUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db);
        measurement.setUnit("clazz");
        assertTrue(measurement.isUnique());
    }

    @Test
    public void newMeasurementCurrentNotUnique() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db);
        measurement.setUnit("kilometer");
        assertFalse(measurement.isUnique());
    }

    @Test
    public void safeToEditExistingMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, "repetition");
        assertTrue(measurement.safeToEdit());
    }

    @Test
    public void unsafeToEditExistingMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, "minute");
        assertFalse(measurement.safeToEdit());
    }

    @Test
    public void safeToEditNewMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, "game");
        assertTrue(measurement.safeToEdit());
    }

    @Test
    public void unableToSaveExistingMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, "minute");
        measurement.setUnit("ssseccconnnnd");
        measurement.save();
        Cursor res = db.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + ";", null);
        assertEquals(7, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
        assertEquals("time", res.getString(1));
        assertEquals("minute", res.getString(2));
        assertEquals(60, res.getDouble(3), 0.00001);
    }

    @Test
    public void saveExistingMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, "repetition");
        measurement.setUnit("reppp");
        measurement.save();
        Cursor res = db.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + ";", null);
        assertEquals(7, res.getCount());
        res.moveToLast();
        assertEquals(7, res.getInt(0));
        assertNull(res.getString(1));
        assertEquals("reppp", res.getString(2));
        assertEquals(-1.0, res.getDouble(3), 0.00001);
    }

    @Test
    public void saveNewMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db);
        measurement.setUnit("repppp");
        measurement.save();
        Cursor res = db.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + ";", null);
        assertEquals(8, res.getCount());
        res.moveToLast();
        assertEquals(8, res.getInt(0));
        assertNull(res.getString(1));
        assertEquals("repppp", res.getString(2));
        assertEquals(-1.0, res.getDouble(3), 0.00001);
    }

    @Test
    public void safeToDeleteExistingMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, "repetition");
        assertTrue(measurement.safeToDelete());
    }

    @Test
    public void safeToDeleteNonExistingMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db);
        measurement.setUnit("repppp");
        assertTrue(measurement.safeToDelete());
    }

    @Test
    public void notSafeToDeleteExistingActivity() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2020-01-01 00:00", "Walked", "kilometer", 5);
        Measurement measurement = new Measurement(db, "kilometer");
        assertFalse(measurement.safeToDelete());
    }

    @Test
    public void notSafeToDeleteExistingGoal() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.addGoal("Walk", "kilometer", 5);
        Measurement measurement = new Measurement(db, "kilometer");
        assertFalse(measurement.safeToDelete());
    }

    @Test
    public void deleteMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Measurement measurement = new Measurement(db, "minute");
        measurement.delete();
        Cursor res = db.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + ";", null);
        assertEquals(6, res.getCount());
        res.moveToFirst();
        assertEquals(2, res.getInt(0));
    }

    @Test
    public void unableToDeleteMeasurement() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2020-01-01 00:00", "Walked", "second", 5);
        Measurement measurement = new Measurement(db, "second");
        measurement.delete();
        Cursor res = db.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + ";", null);
        assertEquals(7, res.getCount());
        res.moveToFirst();
        assertEquals(1, res.getInt(0));
    }
}
