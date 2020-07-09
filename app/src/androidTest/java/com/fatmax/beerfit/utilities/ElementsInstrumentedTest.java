package com.fatmax.beerfit.utilities;

import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Test;

import java.util.List;

import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
import static org.junit.Assert.assertEquals;

public class ElementsInstrumentedTest {

    @After
    public void cleanupDB() {
        wipeOutDB();
    }

    @Test
    public void getAllActivitiesEmptyTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertEquals(0, Elements.getAllActivities(db).size());
    }

    @Test
    public void getAllActivitiesTwoItemsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2020-10-10 10:23", "Walked", "kilometers", 5.0);
        database.logBeer();
        assertEquals(2, Elements.getAllActivities(db).size());
    }

    @Test
    public void getAllActivitiesInOrderTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.logActivity("2020-10-10 10:23", "Walked", "kilometers", 5.0);
        database.logActivity("2020-10-10 10:26", "Ran", "kilometers", 5.0);
        database.logActivity("2020-10-10 11:23", "Walked", "kilometers", 5.0);
        database.logActivity("2020-10-09 09:23", "Walked", "kilometers", 5.0);
        List<Activity> activites = Elements.getAllActivities(db);
        assertEquals(4, activites.size());
        assertEquals(3, activites.get(0).getId());
        assertEquals(2, activites.get(1).getId());
        assertEquals(1, activites.get(2).getId());
        assertEquals(4, activites.get(3).getId());
    }

    @Test
    public void getAllGoalsEmptyTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        assertEquals(0, Elements.getAllGoals(db).size());
    }

    @Test
    public void getAllGoalsTwoItemsTest() {
        SQLiteDatabase db = getDB();
        Database database = new Database(db);
        database.setupDatabase();
        database.addGoal("Run", "kilometers", 5);
        database.addGoal("Walk", "minutes", 30);
        List<Goal> goals = Elements.getAllGoals(db);
        assertEquals(2, goals.size());
        assertEquals(1, goals.get(0).getId());
        assertEquals(2, goals.get(1).getId());
    }
}
