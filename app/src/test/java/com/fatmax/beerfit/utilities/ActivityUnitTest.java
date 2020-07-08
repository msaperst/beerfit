package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.junit.Test;
import org.mockito.Mock;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActivityUnitTest {

    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);

    @Mock
    Cursor mockedCursor = mock(Cursor.class);

    @Test
    public void activityNotFoundTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(null);

        Activity activity = new Activity(mockedSQLiteDatabase, "Walk");
        assertEquals(0, activity.getId());
        assertNull("Walk", activity.getCurrent());
        assertNull("Walked", activity.getPast());
        // not checking color, as it's random
    }

    @Test
    public void activityExistsTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, "Walk");
        assertEquals(1, activity.getId());
        assertEquals("Walk", activity.getCurrent());
        assertEquals("Walked", activity.getPast());
        assertEquals(Color.GREEN, activity.getColor());
    }

    @Test
    public void activityNotExistsTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'walk';", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, "walk");
        assertEquals(0, activity.getId());
        assertNull(activity.getCurrent());
        assertNull(activity.getPast());
        // not checking color, as it's random
    }

    @Test
    public void activityNotExistsSetPastTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, "walk");
        activity.setPast("wulked");
        assertEquals("wulked", activity.getPast());
    }

    @Test
    public void existingActivityUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, "Walk");
        assertTrue(activity.isActivityUnique());
    }

    @Test
    public void newActivityBadUnique() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Wulk' AND id != 0;", null)).thenReturn(null);

        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setCurrent("Wulk");
        assertTrue(activity.isActivityUnique());
    }

    @Test
    public void newActivityUnique() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Wulk' AND id != 0;", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setCurrent("Wulk");
        assertTrue(activity.isActivityUnique());
    }

    @Test
    public void newActivityNotUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk' AND id != 0;", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setCurrent("Walk");
        assertFalse(activity.isActivityUnique());
    }

    @Test
    public void colorBadUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE color = '" + Color.GREEN + "' AND id != 1;", null)).thenReturn(null);

        Activity activity = new Activity(mockedSQLiteDatabase, "Walk");
        assertTrue(activity.isColorUnique());
    }

    @Test
    public void existingColorUnique() {
        when(mockedCursor.getCount()).thenReturn(1).thenReturn(0);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE color = '" + Color.GREEN + "' AND id != 1;", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase, "Walk");
        assertTrue(activity.isColorUnique());
    }

    @Test
    public void newColorUnique() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE color = '1234' AND id != 0;", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setColor(1234);
        assertTrue(activity.isColorUnique());
    }

    @Test
    public void newColorNotUnique() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE color = '" + Color.GREEN + "' AND id != 0;", null)).thenReturn(mockedCursor);

        Activity activity = new Activity(mockedSQLiteDatabase);
        activity.setColor(Color.GREEN);
        assertFalse(activity.isColorUnique());
    }

    @Test
    public void safeToDeleteNull() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE activity = 0;", null)).thenReturn(null);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 0;", null)).thenReturn(null);

        Activity activity = new Activity(mockedSQLiteDatabase);
        assertTrue(activity.safeToDelete());
    }

    @Test
    public void safeToDeleteExistingActivityNotInEither() {
        when(mockedCursor.getCount()).thenReturn(1).thenReturn(0);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE activity = 1;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 1;", null)).thenReturn(mockedCursor);
        Activity activity = new Activity(mockedSQLiteDatabase, "Walk");
        assertTrue(activity.safeToDelete());
    }

    @Test
    public void safeToDeleteExistingActivityNotInGoalsInActivity() {
        when(mockedCursor.getCount()).thenReturn(1).thenReturn(0).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE activity = 1;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 1;", null)).thenReturn(mockedCursor);
        Activity activity = new Activity(mockedSQLiteDatabase, "Walk");
        assertFalse(activity.safeToDelete());
    }

    @Test
    public void notSafeToDeleteExistingActivityInGoals() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("Walked");
        when(mockedCursor.getString(2)).thenReturn("Walk");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITIES_TABLE + " WHERE current = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE activity = 1;", null)).thenReturn(mockedCursor);
        Activity activity = new Activity(mockedSQLiteDatabase, "Walk");
        assertFalse(activity.safeToDelete());
    }
}
