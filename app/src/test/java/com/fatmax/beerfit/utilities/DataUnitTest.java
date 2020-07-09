package com.fatmax.beerfit.utilities;

import com.jjoe64.graphview.series.DataPoint;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataUnitTest {

    @Mock
    Database mockedDatabase = Mockito.mock(Database.class);
    private Data data = new Data(mockedDatabase);

    @Test
    public void addDataPointTest() {
        Data data = new Data(mockedDatabase);
        data.addDataPoint("activity", new DataPoint(0.0, 0.0));
        assertEquals(1, data.getDataPointSpot("activity", 0.0));
    }

    @Test
    public void addDataPointMultipleTest() {
        Data data = new Data(mockedDatabase);
        data.addDataPoint("activity", new DataPoint(0.0, 0.0));
        data.addDataPoint("activity", new DataPoint(0.0, 0.0));
        assertEquals(2, data.getDataPointSpot("activity", 0.0));
    }

    @Test
    public void getSeriesDataTest() {
        assertEquals(new ArrayList<>(), data.getSeriesData());
    }

    @Test
    public void getSeriesDataSingleTest() {
        Data data = new Data(mockedDatabase);
        data.addDataPoint("activity", new DataPoint(0.0, 0.0));
        assertEquals(1, data.getSeriesData().size());
    }

    @Test
    public void zeroOutTest() {
        Data data = new Data(mockedDatabase);
        data.addDataPoint("activity one", new DataPoint(0.0, 0.0));
        assertEquals(1, data.getDataPointSpot("activity one", 1.0));
        data.addDataPoint("activity two", new DataPoint(1.0, 0.0));
        assertEquals(1, data.getDataPointSpot("activity two", 1.0));
        data.zeroOut();
        assertEquals(2, data.getDataPointSpot("activity one", 1.0));
        assertEquals(2, data.getDataPointSpot("activity two", 1.0));
    }
    //TODO - zeroOutTest

    @Test
    public void addDataPointSpotTest() {
        Data data = new Data(mockedDatabase);
        assertEquals(0, data.getDataPointSpot("activity", 5.0));
        data.addDataPoint("activity", new DataPoint(0.0, 0.0));
        assertEquals(0, data.getDataPointSpot("activity", -0.1));
        assertEquals(1, data.getDataPointSpot("activity", 0.0));
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

    @Test(expected = NumberFormatException.class)
    public void getXAxisTestBad() {
        data.getXAxis("d");
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

    @Test(expected = NumberFormatException.class)
    public void getXAxisMinBadTest() {
        data.getXAxisMin("d", null);
    }

    @Test(expected = NumberFormatException.class)
    public void getXAxisMaxBadTest() {
        data.getXAxisMax("d", null);
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
