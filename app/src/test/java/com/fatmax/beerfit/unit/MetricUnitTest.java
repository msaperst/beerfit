package com.fatmax.beerfit.unit;

import com.fatmax.beerfit.utilities.Metric;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetricUnitTest {

    private Metric metric = new Metric(null, null, null);

    @Test(expected = NullPointerException.class)
    public void getTitleNullTest() {
        metric.getTitle(null);
    }

    @Test
    public void getTitleEmptyTest() {
        assertEquals("", metric.getTitle(""));
    }

    @Test
    public void getTitleYearTest() {
        assertEquals("123", metric.getTitle("123"));
        assertEquals("abcd", metric.getTitle("abcd"));
    }

    @Test
    public void getTitleMonthTest() {
        assertEquals("123 a", metric.getTitle("123 a"));
        assertEquals("b 123", metric.getTitle("b 123"));
        assertEquals("b a", metric.getTitle("b a"));
        assertEquals("March 0123", metric.getTitle("123 3"));
        assertEquals("January 1999", metric.getTitle("1999 01"));
        assertEquals("January 2000", metric.getTitle("1999 13"));
    }

    @Test
    public void getTitleWeeklyTest() {
        assertEquals("123 3 a", metric.getTitle("123 3 a"));
        assertEquals("123 b 3", metric.getTitle("123 b 3"));
        assertEquals("c 3 5", metric.getTitle("c 3 5"));
        assertEquals("Week 5, January 0123", metric.getTitle("123 3 4"));
        assertEquals("Week 2, January 2000", metric.getTitle("2000 1 1"));
        assertEquals("Week 5, December 1999", metric.getTitle("2000 0 0"));
        assertEquals("Week 5, December 1999", metric.getTitle("2000 99 0"));
    }

    @Test
    public void getTitleDailyTest() {
        assertEquals("123 3 5 a", metric.getTitle("123 3 5 a"));
        assertEquals("Saturday, January 3 0123", metric.getTitle("123 3 b 3"));
        assertEquals("Saturday, January 3 0123", metric.getTitle("123 b 3 3"));
        assertEquals("c 3 5 6", metric.getTitle("c 3 5 6"));
        assertEquals("Thursday, April 9 0123", metric.getTitle("123 3 4 99"));
        assertEquals("Saturday, January 1 2000", metric.getTitle("2000 1 1 1"));
        assertEquals("Tuesday, December 31 2019", metric.getTitle("2020 0 0 0"));
        assertEquals("Wednesday, September 25 2002", metric.getTitle("2000 99 0 999"));
    }
}
