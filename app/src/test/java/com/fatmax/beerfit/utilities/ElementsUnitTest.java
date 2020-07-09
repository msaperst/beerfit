package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.mockito.Mock;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
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
}
