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
import java.util.List;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
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
    public void getColumnsNullTest() {
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(null);
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getColumns(MEASUREMENTS_TABLE).size());
    }

    @Test
    public void getColumnsEmptyTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getColumns(MEASUREMENTS_TABLE).size());
    }

    @Test
    public void getColumnsFullTest() {
        when(mockedCursor.getCount()).thenReturn(2);
        when(mockedCursor.isAfterLast()).thenReturn(false, false, true);
        when(mockedCursor.getString(1)).thenReturn("hello", "world");
        when(mockedSQLiteDatabase.rawQuery("PRAGMA table_info(" + MEASUREMENTS_TABLE + ")", null)).thenReturn(mockedCursor);
        Database database = new Database(mockedSQLiteDatabase);
        List<String> columns = database.getColumns(MEASUREMENTS_TABLE);
        assertEquals(2, columns.size());
        assertEquals("hello", columns.get(0));
        assertEquals("world", columns.get(1));
    }

    @Test
    public void getActivityColorNullTest() {
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Color.YELLOW, database.getExerciseColor("running"));
    }

    @Test
    public void getActivityColorNoMatchTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'ran' OR past = 'ran';", null, null)).thenReturn(null);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(Color.YELLOW, database.getExerciseColor("ran"));
    }

    @Test
    public void getActivityColorMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(5);
        when(mockedCursor.getString(1)).thenReturn("Run");
        when(mockedCursor.getString(2)).thenReturn("Ran");
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'ran' OR past = 'ran';", null)).thenReturn(mockedCursor);

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
        when(mockedSQLiteDatabase.rawQuery("SELECT time FROM " + ACTIVITIES_TABLE + " WHERE id = '1';", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals("Unknown", database.getActivityTime(1));
    }

    @Test
    public void getActivityTimeMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getString(0)).thenReturn("2020-03-02 00:00");
        when(mockedSQLiteDatabase.rawQuery("SELECT time FROM " + ACTIVITIES_TABLE + " WHERE id = '1';", null)).thenReturn(mockedCursor);

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
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITIES_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getBeersDrank());
    }

    @Test
    public void getBeersDrankMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITIES_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(10, database.getBeersDrank());
    }

    @Test
    public void getMatchingMeasurementsNullTypeTest() {
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(null);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(1, database.getMatchingMeasurements(measurement).size());
        assertEquals(measurement.getId(), database.getMatchingMeasurements(measurement).get(0).getId());
        assertEquals(measurement.getUnit(), database.getMatchingMeasurements(measurement).get(0).getUnit());
        assertEquals(measurement.getType(), database.getMatchingMeasurements(measurement).get(0).getType());
        assertEquals(measurement.getConversion(), database.getMatchingMeasurements(measurement).get(0).getConversion(), 0);
    }

    @Test
    public void getMatchingMeasurementNoSuchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(null);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getMatchingMeasurements(measurement).size());
    }

    @Test
    public void getMatchingMeasurementEmptyTableTest() {
        when(mockedCursor.getCount()).thenReturn(1,0);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getMatchingMeasurements(measurement).size());
    }

    @Test
    public void getMatchingMeasurementOneMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Database database = new Database(mockedSQLiteDatabase);
        List<Measurement> measurements = database.getMatchingMeasurements(measurement);
        assertEquals(1, measurements.size());
        assertEquals(measurement.getId(), measurements.get(0).getId());
        assertEquals(measurement.getUnit(), measurements.get(0).getUnit());
        assertEquals(measurement.getType(), measurements.get(0).getType());
        assertEquals(measurement.getConversion(), measurements.get(0).getConversion(), 0);
    }

    @Test
    public void getMatchingGoalsBadQueryTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance", "Walked");
        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE + " WHERE exercise = 1 AND measurement = 1", null)).thenReturn(null);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Database database = new Database(mockedSQLiteDatabase);
        assertNull(database.getMatchingGoals(exercise, measurement));
    }

    @Test
    public void getMatchingGoalsNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1, 1,1,1,0);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance", "Walked");
        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedCursor.isAfterLast()).thenReturn(false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE + " WHERE exercise = 1 AND measurement = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Database database = new Database(mockedSQLiteDatabase);
        assertNull(database.getMatchingGoals(exercise, measurement));
    }

    @Test
    public void getMatchingGoalsEmptyMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance", "Walked");
        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedCursor.isAfterLast()).thenReturn(false, true, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE + " WHERE exercise = 1 AND measurement = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(-1, database.getMatchingGoals(exercise, measurement).getId());
    }

    @Test
    public void getMatchingGoalsRealMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance", "Walked", "distance", "Walked", "distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk", "kilometer", "Walk", "kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedCursor.getInt(1)).thenReturn(1);
        when(mockedCursor.getInt(2)).thenReturn(1);
        when(mockedCursor.isAfterLast()).thenReturn(false, true, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE + " WHERE exercise = 1 AND measurement = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Database database = new Database(mockedSQLiteDatabase);
        Goal goal = database.getMatchingGoals(exercise, measurement);
        assertEquals(1, goal.getId());
    }

    @Test
    public void getBeersEarnedNullTest() {
        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0.0, database.getBeersEarned(exercise, measurement, 10), 0);
    }

    @Test
    public void getBeersEarnedMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getInt(0)).thenReturn(1);
        when(mockedCursor.getString(1)).thenReturn("distance", "Walked", "distance", "Walked", "distance");
        when(mockedCursor.getString(2)).thenReturn("kilometer", "Walk", "kilometer", "Walk", "kilometer");
        when(mockedCursor.getDouble(3)).thenReturn(1.0);
        when(mockedCursor.getInt(3)).thenReturn(Color.GREEN);
        when(mockedCursor.getInt(1)).thenReturn(1);
        when(mockedCursor.getInt(2)).thenReturn(1);
        when(mockedCursor.isAfterLast()).thenReturn(false, true, false, true);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE unit = 'kilometer';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE current = 'Walk' OR past = 'Walk';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + MEASUREMENTS_TABLE + " WHERE type = 'distance';", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + MEASUREMENTS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT id FROM " + GOALS_TABLE + " WHERE exercise = 1 AND measurement = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + GOALS_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT * FROM " + EXERCISES_TABLE + " WHERE id = 1", null)).thenReturn(mockedCursor);

        Measurement measurement = new Measurement(mockedSQLiteDatabase, "kilometer");
        Exercise exercise = new Exercise(mockedSQLiteDatabase, "Walk");
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(10.0, database.getBeersEarned(exercise, measurement, 10), 0);
    }

    @Test
    public void getTotalBeersEarnedNullTest() {
        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0.0, database.getTotalBeersEarned(), 0);
    }

    @Test
    public void getTotalBeersEarnedNoMatchTest() {
        when(mockedCursor.getCount()).thenReturn(0);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITIES_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0.0, database.getTotalBeersEarned(), 0);
    }

    @Test
    public void getTotalBeersEarnedMatchTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(10.0);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITIES_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(10.0, database.getTotalBeersEarned(), 0);
    }

    @Test
    public void getBeersRemainingNoneTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(10.0);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITIES_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITIES_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(0, database.getBeersRemaining());
    }

    @Test
    public void getBeersRemainingNegativeTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(5.0);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITIES_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITIES_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(-5, database.getBeersRemaining());
    }

    @Test
    public void getBeersRemainingPartialTest() {
        when(mockedCursor.getCount()).thenReturn(1);
        when(mockedCursor.getDouble(0)).thenReturn(11.2);
        when(mockedCursor.getInt(0)).thenReturn(10);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(beers) FROM " + ACTIVITIES_TABLE + " WHERE exercise != 0;", null)).thenReturn(mockedCursor);
        when(mockedSQLiteDatabase.rawQuery("SELECT SUM(amount) FROM " + ACTIVITIES_TABLE + " WHERE exercise = 0;", null)).thenReturn(mockedCursor);

        Database database = new Database(mockedSQLiteDatabase);
        assertEquals(1, database.getBeersRemaining());
    }
}
