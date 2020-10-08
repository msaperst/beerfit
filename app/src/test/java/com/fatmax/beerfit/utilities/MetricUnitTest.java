package com.fatmax.beerfit.utilities;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

public class MetricUnitTest {

    private Metric metric = new Metric(null);

    @Test
    public void getDateTimePatternTest() {
        assertNull(metric.getDateTimePattern());
        assertEquals("2020", new Metric("2020").getDateTimePattern());
    }

    @Test
    public void getTitleNullTest() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> metric.getTitle(null));
        assertNull(exception.getMessage());
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
        assertEquals("c 3 5", metric.getTitle("c 3 5"));
        assertEquals("January 3 - 9 2000", metric.getTitle("2000 1 1"));
        assertEquals("December 27 1999 - January 2 2000", metric.getTitle("2000 0 0"));
        assertEquals("January 27 - February 2 2020", metric.getTitle("2020 3 4"));
    }

    @Test
    public void getWeeklySpreadTest() throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yyyy ww", Locale.US).parse("2020 1"));
        assertEquals("January 6 - 12 2020", metric.getWeeklySpread(cal));
        cal.setTime(new SimpleDateFormat("yyyy ww", Locale.US).parse("2020 4"));
        assertEquals("January 27 - February 2 2020", metric.getWeeklySpread(cal));
        cal.setTime(new SimpleDateFormat("yyyy ww", Locale.US).parse("2020 0"));
        assertEquals("December 30 2019 - January 5 2020", metric.getWeeklySpread(cal));
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
