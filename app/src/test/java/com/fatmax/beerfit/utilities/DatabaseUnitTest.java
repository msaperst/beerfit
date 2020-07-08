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

import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
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
    public void doesTableHaveColumnBadTableTest() {
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(null);

        Database database = new Database(mockedSQLiteDatabase);
        assertFalse(database.doesTableHaveColumn(MEASUREMENTS_TABLE, "exercise"));
    }

    @Test
    public void doesTableHaveColumnEmptyTableTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertFalse(database.doesTableHaveColumn(MEASUREMENTS_TABLE, "exercise"));
    }

    @Test
    public void doesTableHaveColumnNotPresentTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedCursor.getString(1)).thenReturn("hello", "world");
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertFalse(database.doesTableHaveColumn(MEASUREMENTS_TABLE, "exercise"));
    }

    @Test
    public void doesTableHaveColumnPresentTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.getString(1)).thenReturn("exercise");
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertTrue(database.doesTableHaveColumn(MEASUREMENTS_TABLE, "exercise"));
    }

    @Test
    public void doesExistBadQueryTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + EXERCISES_TABLE + "'", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(null);

        Database database = new Database(mockedSQLiteDatabase);
        assertFalse(database.doesDataExist(EXERCISES_TABLE, 1));
    }

    @Test
    public void doesExistNoCountTest() {
        when(mockedCursor.getCount()).thenReturn(1, 0);
        when(mockedSQLiteDatabase.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + EXERCISES_TABLE + "'", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertFalse(database.doesDataExist(EXERCISES_TABLE, 1));
    }

    @Test
    public void doesExistFalseTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertFalse(database.doesDataExist(EXERCISES_TABLE, 1));
    }

    @Test
    public void doesExistNoTableTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertFalse(database.doesDataExist(EXERCISES_TABLE, 1));
    }

    @Test
    public void doesExistTrueTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + EXERCISES_TABLE + "'", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertTrue(database.doesDataExist(EXERCISES_TABLE, 1));
    }

    @Test
    public void getColumnTypeNoPragmaTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        SQLiteException exception = assertThrows(SQLiteException.class, () -> database.getColumnType(MEASUREMENTS_TABLE, "time"));
        assertNull(exception.getMessage());
    }

    @Test
    public void getColumnTypeTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedCursor.getString(1)).thenReturn("time");
        when(mockedCursor.getString(2)).thenReturn("INTEGER");
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals("INTEGER", database.getColumnType(MEASUREMENTS_TABLE, "time"));
    }

    @Test(expected = SQLiteException.class)
    public void getColumnTypeNoColumnTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedCursor.getString(1)).thenReturn("number");
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);

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
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedCursor.getString(1)).thenReturn("amount", "bar");
        when(mockedCursor.getString(2)).thenReturn("VARCHAR");
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals("bar", database.getTableValue(mockedCursor, MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getTableValueIntTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedCursor.getString(1)).thenReturn("amount");
        when(mockedCursor.getString(2)).thenReturn("INTEGER");
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedCursor.getInt(1)).thenReturn(10);


        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(10, database.getTableValue(mockedCursor, MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getTableValueDoubleTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedCursor.getString(1)).thenReturn("amount");
        when(mockedCursor.getString(2)).thenReturn("NUMBER");
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedCursor.getDouble(1)).thenReturn(11.123);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(11.123, database.getTableValue(mockedCursor, MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getTableValueStringTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedCursor.getString(1)).thenReturn("amount", "bar");
        when(mockedCursor.getString(2)).thenReturn("TEXT");
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);

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
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedCursor.getString(1)).thenReturn("amount", "foo");
        when(mockedCursor.getString(2)).thenReturn("TEXT");
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Collections.singletonList("foo"), database.getFullColumn(MEASUREMENTS_TABLE, "amount"));
    }

    @Test
    public void getFullColumnMultipleTest() {
        when(mockedCursor.getCount()).thenReturn(2, 1);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, false, false, true, true);
        when(mockedCursor.getString(1)).thenReturn("amount", "foo", "amount", "bar");
        when(mockedCursor.getString(2)).thenReturn("TEXT");
        when(mockedCursor.getColumnIndex("amount")).thenReturn(1);
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE, null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Arrays.asList("foo", "bar"), database.getFullColumn(MEASUREMENTS_TABLE, "amount"));
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
        assertEquals(Color.YELLOW, database.getExerciseColor("running"));
    }

    @Test
    public void getActivityColorNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1, 0);
        when(mockedCursor.getInt(0)).thenReturn(5);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + EXERCISES_TABLE + " WHERE past = 'ran';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT color FROM " + EXERCISES_TABLE + " WHERE id = '5';", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Color.YELLOW, database.getExerciseColor("ran"));
    }

    @Test
    public void getActivityColorMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(5, Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + EXERCISES_TABLE + " WHERE past = 'ran';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT color FROM " + EXERCISES_TABLE + " WHERE id = '5';", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Color.GREEN, database.getExerciseColor("ran"));
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
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getBeersDrank());
    }

    @Test
    public void getBeersDrankMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

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
        when(mockedSQLiteDatabase.rawQuery("SELECT amount FROM " + GOALS_TABLE + " WHERE exercise = -1 AND measurement = -1;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0.0, database.getBeersEarned("Ran", "kilometers", 10), 0);
    }

    @Test
    public void getBeersEarnedSingleTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(5.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT amount FROM " + GOALS_TABLE + " WHERE exercise = -1 AND measurement = -1;", null)).thenReturn(mockedCursor);

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
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0.0, database.getTotalBeersEarned(), 0);
    }

    @Test
    public void getTotalBeersEarnedMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(10.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(10.0, database.getTotalBeersEarned(), 0);
    }

    @Test
    public void getBeersRemainingNoneTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(10.0);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getBeersRemaining());
    }

    @Test
    public void getBeersRemainingNegativeTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(5.0);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(-5, database.getBeersRemaining());
    }

    @Test
    public void getBeersRemainingPartialTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(11.2);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITY_LOG_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(1, database.getBeersRemaining());
    }
}
