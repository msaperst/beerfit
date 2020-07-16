package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static com.fatmax.beerfit.MetricsActivity.TIME_AS_DATE_FROM;
import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElementsUnitTest {
    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);

    @Mock
    Cursor mockedCursor = mock(Cursor.class);

    @Test
    public void getAllActivitiesBadTableTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + ACTIVITIES_TABLE + " ORDER BY time DESC", null)).thenReturn(null);

        assertEquals(0, Elements.getAllActivities(mockedSQLiteDatabase).size());
    }

    @Test
    public void getAllActivitiesEmptyTableTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + ACTIVITIES_TABLE + " ORDER BY time DESC", null)).thenReturn(mockedCursor);

        assertEquals(0, Elements.getAllActivities(mockedSQLiteDatabase).size());
    }

    @Test
    public void getAllActivitiesFullTableTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + ACTIVITIES_TABLE + " ORDER BY time DESC", null)).thenReturn(mockedCursor);

        assertEquals(2, Elements.getAllActivities(mockedSQLiteDatabase).size());
    }

    @Test
    public void getAllGoalsBadTableTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE, null)).thenReturn(null);

        assertEquals(0, Elements.getAllGoals(mockedSQLiteDatabase).size());
    }

    @Test
    public void getAllGoalsEmptyTableTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE, null)).thenReturn(mockedCursor);

        assertEquals(0, Elements.getAllGoals(mockedSQLiteDatabase).size());
    }

    @Test
    public void getAllGoalsFullTableTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE, null)).thenReturn(mockedCursor);

        assertEquals(2, Elements.getAllGoals(mockedSQLiteDatabase).size());
    }

    @Test
    public void getAllActivityTimesBadTableTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT DISTINCT strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " ORDER BY date ASC", null)).thenReturn(null);

        assertEquals(0, Elements.getAllActivityTimes(mockedSQLiteDatabase, new Metric("%Y %m"), "ASC").size());
    }

    @Test
    public void getAllActivityTimesEmptyTableTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT DISTINCT strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " ORDER BY date DESC", null)).thenReturn(mockedCursor);

        assertEquals(0, Elements.getAllActivityTimes(mockedSQLiteDatabase, new Metric("%Y %m"), "DESC").size());
    }

    @Test
    public void getAllActivityTimesFullTableTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT DISTINCT strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " ORDER BY date ASC", null)).thenReturn(mockedCursor);

        assertEquals(2, Elements.getAllActivityTimes(mockedSQLiteDatabase, new Metric("%Y %m"), "ASC").size());
    }

    @Test
    public void getActivitiesGroupedByExerciseAndTimeFrameBadTableTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT exercise, measurement, SUM(amount), strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " WHERE date = '2020' GROUP BY exercise, measurement, date", null)).thenReturn(null);

        assertEquals(0, Elements.getActivitiesGroupedByExerciseAndTimeFrame(mockedSQLiteDatabase, new Metric("%Y %m"), new Data(), "2020").size());
    }

    @Test
    public void getActivitiesGroupedByExerciseAndTimeFrameEmptyTableTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT exercise, measurement, SUM(amount), strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " WHERE date = '2020' GROUP BY exercise, measurement, date", null)).thenReturn(mockedCursor);

        assertEquals(0, Elements.getActivitiesGroupedByExerciseAndTimeFrame(mockedSQLiteDatabase, new Metric("%Y %m"), new Data(), "2020").size());
    }

    @Test
    public void getActivitiesGroupedByExerciseAndTimeFrameFullTableTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT exercise, measurement, SUM(amount), strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " WHERE date = '2020' GROUP BY exercise, measurement, date", null)).thenReturn(mockedCursor);

        assertEquals(2, Elements.getActivitiesGroupedByExerciseAndTimeFrame(mockedSQLiteDatabase, new Metric("%Y %m"), new Data(), "2020").size());
    }

    @Test
    public void getBeersDrankBadTableTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount), strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " WHERE date = '2020' AND " + ACTIVITIES_TABLE + ".exercise = 0 GROUP BY date", null)).thenReturn(null);

        assertEquals(0, Elements.getBeersDrank(mockedSQLiteDatabase, new Metric("%Y %m"), "2020"));
    }

    @Test
    public void getBeersDrankEmptyTableTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount), strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " WHERE date = '2020' AND " + ACTIVITIES_TABLE + ".exercise = 0 GROUP BY date", null)).thenReturn(mockedCursor);

        assertEquals(0, Elements.getBeersDrank(mockedSQLiteDatabase, new Metric("%Y %m"), "2020"));
    }

    @Test
    public void getBeersDrankFullTableTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedCursor.getInt(0)).thenReturn(1, 2);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount), strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " WHERE date = '2020' AND " + ACTIVITIES_TABLE + ".exercise = 0 GROUP BY date", null)).thenReturn(mockedCursor);

        assertEquals(1, Elements.getBeersDrank(mockedSQLiteDatabase, new Metric("%Y %m"), "2020"));
    }

    @Test
    public void getActivitiesPerformedBadTableTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT " + EXERCISES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit, SUM(beers), strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " LEFT JOIN " + EXERCISES_TABLE + " ON " + ACTIVITIES_TABLE + ".exercise = " + EXERCISES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITIES_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE date = '2020' AND " + ACTIVITIES_TABLE + ".exercise != 0 GROUP BY " + EXERCISES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit, date", null)).thenReturn(null);

        assertEquals(0, Elements.getActivitiesPerformed(mockedSQLiteDatabase, new Metric("%Y %m"), "2020").size());
    }

    @Test
    public void getActivitiesPerformedEmptyTableTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT " + EXERCISES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit, SUM(beers), strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " LEFT JOIN " + EXERCISES_TABLE + " ON " + ACTIVITIES_TABLE + ".exercise = " + EXERCISES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITIES_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE date = '2020' AND " + ACTIVITIES_TABLE + ".exercise != 0 GROUP BY " + EXERCISES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit, date", null)).thenReturn(mockedCursor);

        assertEquals(0, Elements.getActivitiesPerformed(mockedSQLiteDatabase, new Metric("%Y %m"), "2020").size());
    }

    @Test
    public void getActivitiesPerformedFullTableTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedCursor.getString(0)).thenReturn("Walked", "Ran");
        when(mockedCursor.getDouble(1)).thenReturn(1.0, 1.0, 5.0, 5.0);
        when(mockedCursor.getString(2)).thenReturn("kilometer", "kilometer");
        when(mockedCursor.getInt(3)).thenReturn(1, 1);
        when(mockedSQLiteDatabase.rawQuery("SELECT " + EXERCISES_TABLE + ".past, SUM(amount), " + MEASUREMENTS_TABLE + ".unit, SUM(beers), strftime('%Y %m" + TIME_AS_DATE_FROM + ACTIVITIES_TABLE + " LEFT JOIN " + EXERCISES_TABLE + " ON " + ACTIVITIES_TABLE + ".exercise = " + EXERCISES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + ACTIVITIES_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id WHERE date = '2020' AND " + ACTIVITIES_TABLE + ".exercise != 0 GROUP BY " + EXERCISES_TABLE + ".past, " + MEASUREMENTS_TABLE + ".unit, date", null)).thenReturn(mockedCursor);

        Map<String, Integer> activities = Elements.getActivitiesPerformed(mockedSQLiteDatabase, new Metric("%Y %m"), "2020");
        assertEquals(1, (int) activities.get("Walked for 1.0 kilometer"));
        assertEquals(1, (int) activities.get("Ran for 5.0 kilometers"));
    }

    @Test
    public void getProperStringPluralizationEmptyTest() {
        assertEquals("s", Elements.getProperStringPluralization("", 0));
    }

    @Test
    public void getProperStringPluralizationSigularTest() {
        assertEquals("beer", Elements.getProperStringPluralization("beer", 1.0));
    }

    @Test
    public void getProperStringPluralizationDoubleTest() {
        assertEquals("beers", Elements.getProperStringPluralization("beer", 2.0));
    }

    @Test
    public void getProperStringPluralizationCloseTest() {
        assertEquals("beers", Elements.getProperStringPluralization("beer", 1.000001));
    }

    @Test
    public void getProperStringPluralizationFancyTest() {
        assertEquals("classes", Elements.getProperStringPluralization("class", 0));
    }

    @Test
    public void getProperStringPluralizationSuperFancyTest() {
        assertEquals("activities", Elements.getProperStringPluralization("activity", 0));
    }

    @Test
    public void getSortedMeasurementsBadTableTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT unit FROM " + MEASUREMENTS_TABLE + " ORDER BY conversion ASC", null)).thenReturn(null);

        assertEquals(0, Elements.getSortedMeasurements(mockedSQLiteDatabase, 1).size());
    }

    @Test
    public void getSortedMeasurementsEmptyTableTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT unit FROM " + MEASUREMENTS_TABLE + " ORDER BY conversion ASC", null)).thenReturn(mockedCursor);

        assertEquals(0, Elements.getSortedMeasurements(mockedSQLiteDatabase, 2).size());
    }

    @Test
    public void getSortedMeasurementsFullTableTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedCursor.getString(0)).thenReturn("mile", "kilometer");
        when(mockedSQLiteDatabase.rawQuery("SELECT unit FROM " + MEASUREMENTS_TABLE + " ORDER BY conversion ASC", null)).thenReturn(mockedCursor);

        List<String> measurements = Elements.getSortedMeasurements(mockedSQLiteDatabase, 1);
        assertEquals(2, measurements.size());
        assertEquals("mile", measurements.get(0));
        assertEquals("kilometer", measurements.get(1));
    }

    @Test
    public void getSortedMeasurementNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedCursor.getString(0)).thenReturn("mile", "kilometer");
        when(mockedSQLiteDatabase.rawQuery("SELECT unit FROM " + MEASUREMENTS_TABLE + " ORDER BY conversion ASC", null)).thenReturn(mockedCursor);

        assertEquals(-1, Elements.getSortedMeasurement(mockedSQLiteDatabase, 1, new Measurement(mockedSQLiteDatabase, "kilometer")));
    }

    @Test
    public void getSortedMeasurementMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1, 2);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedCursor.getString(0)).thenReturn("mile", "kilometer");
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT unit FROM " + MEASUREMENTS_TABLE + " ORDER BY conversion ASC", null)).thenReturn(mockedCursor);

        assertEquals(1, Elements.getSortedMeasurement(mockedSQLiteDatabase, 1, new Measurement(mockedSQLiteDatabase, "kilometer")));
    }
}
