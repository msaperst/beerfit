package com.fatmax.beerfit.utilities;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;

public class DataInstrumentedTest {

    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void addDataPointTest() {
        Data data = new Data();
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity walking = new Activity(db);
        walking.setExercise(new Exercise(db, 1));  // walking
        walking.setMeasurement(new Measurement(db, 2));    // kilometers
        walking.setAmount(5);
        walking.setBeers(2020.0);   //kludge to hold xaxis data
        data.addDataPoint(walking);
        assertEquals(1, data.getDataPointSpot(walking, 2020.0));
    }

    @Test
    public void addDataPointMultipleTest() {
        Data data = new Data();
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity walking = new Activity(db);
        walking.setExercise(new Exercise(db, 1));  // walking
        walking.setMeasurement(new Measurement(db, 2));    // kilometers
        walking.setAmount(5);
        walking.setBeers(2020.0);   //kludge to hold xaxis data
        data.addDataPoint(walking);
        data.addDataPoint(walking);
        assertEquals(2, data.getDataPointSpot(walking, 2020.0));
    }

    @Test
    public void getSeriesDataTest() {
        Data data = new Data();
        assertEquals(new ArrayList<>(), data.getSeriesData());
    }

    @Test
    public void getSeriesDataSingleTest() {
        Data data = new Data();
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity walking = new Activity(db);
        walking.setExercise(new Exercise(db, 1));  // walking
        walking.setMeasurement(new Measurement(db, 2));    // kilometers
        walking.setAmount(5);
        walking.setBeers(2020.0);   //kludge to hold xaxis data
        data.addDataPoint(walking);
        List<LineGraphSeries<DataPoint>> seriesData = data.getSeriesData();
        assertEquals(1, seriesData.size());
        assertEquals("Walked (kilometers)", seriesData.get(0).getTitle());
        assertEquals(Color.GREEN, seriesData.get(0).getColor());
    }

    @Test
    public void getSeriesDataMultipleTest() {
        Data data = new Data();
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity walking = new Activity(db);
        walking.setExercise(new Exercise(db, 1));  // walking
        walking.setMeasurement(new Measurement(db, 2));    // kilometers
        walking.setAmount(5);
        walking.setBeers(2020.0);   //kludge to hold xaxis data
        Activity lifting = new Activity(db);
        lifting.setExercise(new Exercise(db, 4));  // lifting
        lifting.setMeasurement(new Measurement(db, 1));    // minutes
        lifting.setAmount(30);
        lifting.setBeers(2020.0);   //kludge to hold xaxis data
        data.addDataPoint(walking);
        data.addDataPoint(walking);
        data.addDataPoint(lifting);
        List<LineGraphSeries<DataPoint>> seriesData = data.getSeriesData();
        assertEquals(2, seriesData.size());
    }

    @Test
    public void zeroOutTest() {
        Data data = new Data();
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity walking = new Activity(db);
        walking.setExercise(new Exercise(db, 1));  // walking
        walking.setMeasurement(new Measurement(db, 2));    // kilometers
        walking.setAmount(5);
        walking.setBeers(2020.0);   //kludge to hold xaxis data
        Activity lifting = new Activity(db);
        lifting.setExercise(new Exercise(db, 4));  // lifting
        lifting.setMeasurement(new Measurement(db, 1));    // minutes
        lifting.setAmount(30);
        lifting.setBeers(2021.0);   //kludge to hold xaxis data
        data.addDataPoint(walking);
        assertEquals(1, data.getDataPointSpot(walking, 2021.0));
        data.addDataPoint(lifting);
        assertEquals(1, data.getDataPointSpot(lifting, 2021.0));
        data.zeroOut();
        assertEquals(2, data.getDataPointSpot(walking, 2021.0));
        assertEquals(2, data.getDataPointSpot(lifting, 2021.0));
    }

    @Test
    public void addDataPointSpotTest() {
        Data data = new Data();
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        Activity walking = new Activity(db);
        walking.setExercise(new Exercise(db, 1));  // walking
        walking.setMeasurement(new Measurement(db, 2));    // kilometers
        walking.setAmount(5);
        walking.setBeers(2020.0);   //kludge to hold xaxis data
        assertEquals(0, data.getDataPointSpot(walking, 5.0));
        data.addDataPoint(walking);
        assertEquals(0, data.getDataPointSpot(walking, 2019.0));
        assertEquals(1, data.getDataPointSpot(walking, 2020.0));
    }
}
