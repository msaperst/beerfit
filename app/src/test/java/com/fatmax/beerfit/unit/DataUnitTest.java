package com.fatmax.beerfit.unit;

import com.fatmax.beerfit.utilities.Data;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DataUnitTest {

    @Test
    public void getSeriesDataTest() {
        assertEquals(new ArrayList<>(), new Data().getSeriesData());
    }

    @Test
    public void getMultiplierTest() {
        assertEquals(0.0, new Data().getMultiplier("d"), 0);
        assertEquals(0.0, new Data().getMultiplier("2000"), 0);
        assertEquals(1.0 / 12, new Data().getMultiplier("0 0"), 0);
        assertEquals(1.0 / 52, new Data().getMultiplier("0 0 0"), 0);
        assertEquals(1.0 / 366, new Data().getMultiplier("0 0 0 0"), 0);
    }

    @Test (expected = NumberFormatException.class)
    public void getXAxisTestBad() {
        new Data().getXAxis("d");
    }

    @Test
    public void getXAxisTest() {
        assertEquals(2000.0, new Data().getXAxis("2000"), 0);
        assertEquals(2000.0, new Data().getXAxis("2000 1"), 0);
        assertEquals(2000.5, new Data().getXAxis("2000 7"), 0);
        assertEquals(2001, new Data().getXAxis("2000 13"), 0);
        assertEquals(2000, new Data().getXAxis("2000 1 1"), 0);
        assertEquals(2000.5, new Data().getXAxis("2000 1 27"), 0);
        assertEquals(2001, new Data().getXAxis("2000 1 53"), 0);
        assertEquals(2000, new Data().getXAxis("2000 1 1 1"), 0);
        assertEquals(2000.5, new Data().getXAxis("2000 1 1 184"), 0);
        assertEquals(2001, new Data().getXAxis("2000 1 0 367"), 0);
    }

    @Test (expected = NumberFormatException.class)
    public void getXAxisMinBadTest() {
        new Data().getXAxisMin("d", null);
    }

    @Test (expected = NumberFormatException.class)
    public void getXAxisMaxBadTest() {
        new Data().getXAxisMax("d", null);
    }

    @Test
    public void getXAxisMinMaxTest() {
        assertEquals(2000.0, new Data().getXAxisMin("2000", null), 0);
        assertEquals(2001.0, new Data().getXAxisMax("2000", null), 0);

        assertEquals(2000.5, new Data().getXAxisMin("2000 7", null), 0);
        assertEquals(2000.58333, new Data().getXAxisMax("2000 7", null), 0.00001);

        assertEquals(2000.5, new Data().getXAxisMin("2000 7 27", null), 0);
        assertEquals(2000.5192, new Data().getXAxisMax("2000 7 27", null), 0.0001);

        assertEquals(2000.5, new Data().getXAxisMin("2000 7 27 184", null), 0);
        assertEquals(2000.5027, new Data().getXAxisMax("2000 7 27 184", null), 0.0001);
    }
}
