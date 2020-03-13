package com.fatmax.beerfit.utilities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseUnitTest {

    @Mock
    SQLiteDatabase mockedSQLiteDatabase = mock(SQLiteDatabase.class);

    @Mock
    Cursor mockedCursor = mock(Cursor.class);

    @Test
    public void isTableMissingFalseTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + MEASUREMENTS_TABLE + "'", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertFalse(database.isTableMissing(MEASUREMENTS_TABLE));
        assertTrue(database.isTableMissing("myTable"));
    }

    @Test
    public void isTableMissingTrueTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + MEASUREMENTS_TABLE + "'", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertTrue(database.isTableMissing("myTable"));
        assertTrue(database.isTableMissing(MEASUREMENTS_TABLE));
    }

    @Test
    public void getColumnTypeTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getString(0)).thenReturn("number");
        when(mockedSQLiteDatabase.rawQuery("SELECT typeof(amount) FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals("number", database.getColumnType(MEASUREMENTS_TABLE, "amount"));
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoColumnTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT typeof(amount) FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        database.getColumnType(MEASUREMENTS_TABLE, "amount");
        fail();
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoTableTest() {
        Database database = new Database(mockedSQLiteDatabase);
        database.getColumnType(MEASUREMENTS_TABLE, "amount");
        fail();
    }

    @Test
    public void getTableValueDefaultTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getString(0)).thenReturn("number");
        when(mockedCursor.getInt(1)).thenReturn(10);
        when(mockedCursor.getDouble(1)).thenReturn(11.123);
        when(mockedCursor.getBlob(1)).thenReturn("foo".getBytes());
        when(mockedCursor.getString(1)).thenReturn("bar");
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT typeof(amount) FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals("bar", database.getTableValue(mockedCursor, MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getTableValueIntTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getString(0)).thenReturn("integer");
        when(mockedCursor.getInt(1)).thenReturn(10);
        when(mockedCursor.getDouble(1)).thenReturn(11.123);
        when(mockedCursor.getBlob(1)).thenReturn("foo".getBytes());
        when(mockedCursor.getString(1)).thenReturn("bar");
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT typeof(amount) FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(10, database.getTableValue(mockedCursor, MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getTableValueDoubleTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getString(0)).thenReturn("real");
        when(mockedCursor.getInt(1)).thenReturn(10);
        when(mockedCursor.getDouble(1)).thenReturn(11.123);
        when(mockedCursor.getBlob(1)).thenReturn("foo".getBytes());
        when(mockedCursor.getString(1)).thenReturn("bar");
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT typeof(amount) FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(11.123, database.getTableValue(mockedCursor, MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getTableValueBlobTest() {
        byte[] bytes = "foo".getBytes();
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getString(0)).thenReturn("blob");
        when(mockedCursor.getInt(1)).thenReturn(10);
        when(mockedCursor.getDouble(1)).thenReturn(11.123);
        when(mockedCursor.getBlob(1)).thenReturn(bytes);
        when(mockedCursor.getString(1)).thenReturn("bar");
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT typeof(amount) FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(bytes, database.getTableValue(mockedCursor, MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getTableValueStringTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getString(0)).thenReturn("text");
        when(mockedCursor.getInt(1)).thenReturn(10);
        when(mockedCursor.getDouble(1)).thenReturn(11.123);
        when(mockedCursor.getBlob(1)).thenReturn("foo".getBytes());
        when(mockedCursor.getString(1)).thenReturn("bar");
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT typeof(amount) FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals("bar", database.getTableValue(mockedCursor, MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getFullColumnNoColsTest() {
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(new ArrayList<>(), database.getFullColumn(MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getFullColumnEmptyTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(new ArrayList<>(), database.getFullColumn(MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getFullColumnSingleTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.isAfterLast()).thenReturn(false).thenReturn(true);
        when(mockedCursor.getString(0)).thenReturn("text");
        when(mockedCursor.getString(1)).thenReturn("foo");
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT typeof(amount) FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Collections.singletonList("foo"), database.getFullColumn(MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getFullColumnMultipleTest() {
        when(mockedCursor.getCount()).thenReturn(4);
        when(mockedCursor.isAfterLast()).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);
        when(mockedCursor.getString(0)).thenReturn("text");
        when(mockedCursor.getString(1)).thenReturn("foo").thenReturn("bar");
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT typeof(amount) FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Arrays.asList("foo","bar","bar","bar"), database.getFullColumn(MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getOrdinalNullTest() {
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(-1, database.getOrdinal(MEASUREMENTS_TABLE, "unit", "hours"));
    }

    @Test
    public void getOrdinalNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'hours';", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(-1, database.getOrdinal(MEASUREMENTS_TABLE, "unit", "hours"));
    }

    @Test
    public void getOrdinalMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(5);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'minutes';", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(5, database.getOrdinal(MEASUREMENTS_TABLE, "unit", "minutes"));
    }

    @Test
    public void getActivityColorNullTest() {
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Color.YELLOW, database.getActivityColor("running"));
    }

    @Test
    public void getActivityColorNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1).thenReturn(0);
        when(mockedCursor.getInt(0)).thenReturn(5);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + ACTIVITIES_TABLE + " WHERE past = 'ran';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT color FROM " + ACTIVITIES_TABLE + " WHERE id = '5';", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Color.YELLOW, database.getActivityColor("ran"));
    }

    @Test
    public void getActivityColorMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(5).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + ACTIVITIES_TABLE + " WHERE past = 'ran';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT color FROM " + ACTIVITIES_TABLE + " WHERE id = '5';", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Color.GREEN, database.getActivityColor("ran"));
    }


    @Test
    public void getActivityTimeNullTest() {
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals("Unknown", database.getActivityTime(1));
    }

    @Test
    public void getActivityTimeNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT time FROM " + ACTIVITY_LOG_TABLE + " WHERE id = '1';", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals("Unknown", database.getActivityTime(1));
    }

    @Test
    public void getActivityTimeMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getString(0)).thenReturn("2020-03-02 00:00");
        when(mockedSQLiteDatabase.rawQuery("SELECT time FROM " + ACTIVITY_LOG_TABLE + " WHERE id = '1';", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals("2020-03-02 00:00", database.getActivityTime(1));
    }

    @Test
    public void getBeersDrankNullTest() {
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getBeersDrank());
    }

    @Test
    public void getBeersDrankNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getBeersDrank());
    }

    @Test
    public void getBeersDrankMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(10, database.getBeersDrank());
    }

    @Test
    public void getBeersEarnedNullTest() {
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0.0, database.getBeersEarned("Ran", "kilometers", 10), 0);
    }

    @Test
    public void getBeersEarnedNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT amount FROM " + GOALS_TABLE + " WHERE activity = -1 AND measurement = -1;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0.0, database.getBeersEarned("Ran", "kilometers", 10), 0);
    }

    @Test
    public void getBeersEarnedSingleTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(5.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT amount FROM " + GOALS_TABLE + " WHERE activity = -1 AND measurement = -1;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(2.0, database.getBeersEarned("Ran", "kilometers", 10), 0);
    }

    @Test
    public void getTotalBeersEarnedNullTest() {
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0.0, database.getTotalBeersEarned(), 0);
    }

    @Test
    public void getTotalBeersEarnedNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity != 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0.0, database.getTotalBeersEarned(), 0);
    }

    @Test
    public void getTotalBeersEarnedMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(10.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity != 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(10.0, database.getTotalBeersEarned(), 0);
    }

    @Test
    public void getBeersRemainingNoneTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(10.0);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity != 0;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getBeersRemaining());
    }

    @Test
    public void getBeersRemainingNegativeTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(5.0);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity != 0;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(-5, database.getBeersRemaining());
    }

    @Test
    public void getBeersRemainingPartialTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(11.2);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity != 0;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE activity = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(1, database.getBeersRemaining());
    }
}