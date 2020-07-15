package com.fatmax.beerfit.utilities;

import android.database.sqlite.SQLiteDatabase;

import com.jjoe64.graphview.series.DataPoint;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DataUnitTest {

    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);
    private Data data = new Data();

    @Test
    public void addDataPointNewDataSet() {
        Activity activity = new Activity(mockedSQLiteDatabase);
        data.addDataPoint(activity);
        assertEquals(1, data.getDataPointSpot(activity, 1.0));
    }

    @Test
    public void addDataPointExistingNullSet() {
        Activity activity = new Activity(mockedSQLiteDatabase);
        data.addDataPoint(activity);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> data.addDataPoint(activity));
        assertNull(exception.getMessage());
    }

    @Test
    public void addDataPointExistingMatchingSet() {
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setExercise(new Exercise(mockedSQLiteDatabase, 0));
        activity.setMeasurement(new Measurement(mockedSQLiteDatabase, 0));
        data.addDataPoint(activity);
        data.addDataPoint(activity);
        assertEquals(2, data.getDataPointSpot(activity, 1.0));
    }

    @Test
    public void addDataPointExistingNeitherMatchSet() {
        Activity activityOne = new Activity(mockedSQLiteDatabase);
        activityOne.setExercise(new Exercise(mockedSQLiteDatabase, 0));
        activityOne.setMeasurement(new Measurement(mockedSQLiteDatabase, 0));
        Activity activityTwo = new Activity(mockedSQLiteDatabase);
        activityTwo.setExercise(new Exercise(mockedSQLiteDatabase, 1));
        activityTwo.setMeasurement(new Measurement(mockedSQLiteDatabase, 1));
        data.addDataPoint(activityOne);
        data.addDataPoint(activityTwo);
        assertEquals(1, data.getDataPointSpot(activityOne, 1.0));
    }

    @Test
    public void addDataPointExistingExerciseMatchSet() {
        Activity activityOne = new Activity(mockedSQLiteDatabase);
        activityOne.setExercise(new Exercise(mockedSQLiteDatabase, 0));
        activityOne.setMeasurement(new Measurement(mockedSQLiteDatabase, 0));
        Activity activityTwo = new Activity(mockedSQLiteDatabase);
        activityTwo.setExercise(new Exercise(mockedSQLiteDatabase, 0));
        activityTwo.setMeasurement(new Measurement(mockedSQLiteDatabase, 1));
        data.addDataPoint(activityOne);
        data.addDataPoint(activityTwo);
        assertEquals(1, data.getDataPointSpot(activityOne, 1.0));
    }

    @Test
    public void addDataPointExistingMeasurementMatchSet() {
        Activity activityOne = new Activity(mockedSQLiteDatabase);
        activityOne.setExercise(new Exercise(mockedSQLiteDatabase, 0));
        activityOne.setMeasurement(new Measurement(mockedSQLiteDatabase, 0));
        Activity activityTwo = new Activity(mockedSQLiteDatabase);
        activityTwo.setExercise(new Exercise(mockedSQLiteDatabase, 1));
        activityTwo.setMeasurement(new Measurement(mockedSQLiteDatabase, 0));
        data.addDataPoint(activityOne);
        data.addDataPoint(activityTwo);
        assertEquals(1, data.getDataPointSpot(activityOne, 1.0));
    }

    @Test
    public void getSeriesDataEmptySet() {
        assertEquals(new ArrayList<>(), data.getSeriesData());
    }

    @Test
    public void getSeriesDataSingleNullSet() {
        data.addDataPoint(new Activity(mockedSQLiteDatabase));
        NullPointerException exception = assertThrows(NullPointerException.class, () -> data.getSeriesData().size());
        assertNull(exception.getMessage());
    }

    @Test
    public void getSeriesDataSingleSet() {
        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setExercise(new Exercise(mockedSQLiteDatabase, 0));
        activity.setMeasurement(new Measurement(mockedSQLiteDatabase, 0));
        data.addDataPoint(activity);
        assertEquals(1, data.getSeriesData().size());
    }

    @Test
    public void zeroOutEmptySet() {
        data.zeroOut();
        assertEquals(0, data.getDataPointSpot(new Activity(mockedSQLiteDatabase), 0.0));
    }

    @Test
    public void zeroOutSet() {
        Activity activityOne = new Activity(mockedSQLiteDatabase);
        activityOne.setExercise(new Exercise(mockedSQLiteDatabase, 0));
        activityOne.setMeasurement(new Measurement(mockedSQLiteDatabase, 0));
        activityOne.setBeers(2020.0);
        Activity activityTwo = new Activity(mockedSQLiteDatabase);
        activityTwo.setExercise(new Exercise(mockedSQLiteDatabase, 1));
        activityTwo.setMeasurement(new Measurement(mockedSQLiteDatabase, 0));
        activityTwo.setBeers(2021.0);
        data.addDataPoint(activityOne);
        assertEquals(1, data.getDataPointSpot(activityOne, 2021.0));
        data.addDataPoint(activityTwo);
        assertEquals(1, data.getDataPointSpot(activityTwo, 2021.0));
        data.zeroOut();
        assertEquals(2, data.getDataPointSpot(activityOne, 2021.0));
        assertEquals(2, data.getDataPointSpot(activityTwo, 2021.0));
    }

    @Test
    public void addDataPointSpotTest() {
        Activity activity = new Activity(mockedSQLiteDatabase);
        assertEquals(0, data.getDataPointSpot(activity, 5.0));
        data.addDataPoint(activity);
        assertEquals(0, data.getDataPointSpot(activity, -0.1));
        assertEquals(1, data.getDataPointSpot(activity, 0.0));
    }

    @Test
    public void doesDataPointsContainsXTest() {
        List<DataPoint> dataPoints = new ArrayList<>();
        dataPoints.add(new DataPoint(0.0, 0.0));
        assertTrue(data.doesDataPointsContainX(0.0, dataPoints));
        assertFalse(data.doesDataPointsContainX(1.0, dataPoints));
        assertFalse(data.doesDataPointsContainX(0.0, new ArrayList<>()));
    }

    @Test
    public void getMultiplierTest() {
        assertEquals(0.0, data.getMultiplier("d"), 0);
        assertEquals(0.0, data.getMultiplier("2000"), 0);
        assertEquals(1.0 / 12, data.getMultiplier("0 0"), 0);
        assertEquals(1.0 / 52, data.getMultiplier("0 0 0"), 0);
        assertEquals(1.0 / 366, data.getMultiplier("0 0 0 0"), 0);
    }

    @Test
    public void getXAxisTestBad() {
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> data.getXAxis("d"));
        assertEquals("For input string: \"d\"", exception.getMessage());
    }

    @Test
    public void getXAxisTest() {
        assertEquals(2000.0, data.getXAxis("2000"), 0);
        assertEquals(2000.0, data.getXAxis("2000 1"), 0);
        assertEquals(2000.5, data.getXAxis("2000 7"), 0);
        assertEquals(2001, data.getXAxis("2000 13"), 0);
        assertEquals(2000, data.getXAxis("2000 1 1"), 0);
        assertEquals(2000.5, data.getXAxis("2000 1 27"), 0);
        assertEquals(2001, data.getXAxis("2000 1 53"), 0);
        assertEquals(2000, data.getXAxis("2000 1 1 1"), 0);
        assertEquals(2000.5, data.getXAxis("2000 1 1 184"), 0);
        assertEquals(2001, data.getXAxis("2000 1 0 367"), 0);
    }

    @Test
    public void getXAxisMinBadTest() {
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> data.getXAxisMin("d", null));
        assertEquals("For input string: \"d\"", exception.getMessage());
    }

    @Test
    public void getXAxisMaxBadTest() {
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> data.getXAxisMax("d", null));
        assertEquals("For input string: \"d\"", exception.getMessage());
    }

    @Test
    public void getXAxisMinMaxTest() {
        assertEquals(2000.0, data.getXAxisMin("2000", null), 0);
        assertEquals(2001.0, data.getXAxisMax("2000", null), 0);

        assertEquals(2000.5, data.getXAxisMin("2000 7", null), 0);
        assertEquals(2000.58333, data.getXAxisMax("2000 7", null), 0.00001);

        assertEquals(2000.5, data.getXAxisMin("2000 7 27", null), 0);
        assertEquals(2000.5192, data.getXAxisMax("2000 7 27", null), 0.0001);

        assertEquals(2000.5, data.getXAxisMin("2000 7 27 184", null), 0);
        assertEquals(2000.5027, data.getXAxisMax("2000 7 27 184", null), 0.0001);
    }
}
