//package com.fatmax.beerfit.utilities;
//
//import android.database.sqlite.SQLiteDatabase;
//
//import org.junit.After;
//import org.junit.Test;
//
//import java.util.List;
//import java.util.Map;
//
//import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;
//import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
//import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.getDB;
//import static com.fatmax.beerfit.utilities.DatabaseInstrumentedTest.wipeOutDB;
//import static org.junit.Assert.assertEquals;
//
//public class ElementsInstrumentedTest {
//
//    @After
//    public void cleanupDB() {
//        wipeOutDB();
//    }
//
//    @Test
//    public void getAllActivitiesEmptyTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        assertEquals(0, Elements.getAllActivities(db).size());
//    }
//
//    @Test
//    public void getAllActivitiesTwoItemsTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(1,'2020-10-10 10:23',1,2,5,0);");
//        db.execSQL("INSERT INTO " + ACTIVITIES_TABLE + " VALUES(2,'2020-10-10 10:23',0,0,1,-1);");
//        assertEquals(2, Elements.getAllActivities(db).size());
//    }
//
//    @Test
//    public void getAllActivitiesInOrderTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        database.logActivity("2020-10-10 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-10 10:26", "Ran", "kilometer", 5.0);
//        database.logActivity("2020-10-10 11:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-09 09:23", "Walked", "kilometer", 5.0);
//        List<Activity> activites = Elements.getAllActivities(db);
//        assertEquals(4, activites.size());
//        assertEquals(3, activites.get(0).getId());
//        assertEquals(2, activites.get(1).getId());
//        assertEquals(1, activites.get(2).getId());
//        assertEquals(4, activites.get(3).getId());
//    }
//
//    @Test
//    public void getAllGoalsEmptyTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        assertEquals(0, Elements.getAllGoals(db).size());
//    }
//
//    @Test
//    public void getAllGoalsTwoItemsTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,5);");
//        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(2,1,1,30);");
//        List<Goal> goals = Elements.getAllGoals(db);
//        assertEquals(2, goals.size());
//        assertEquals(1, goals.get(0).getId());
//        assertEquals(2, goals.get(1).getId());
//    }
//
//    @Test
//    public void getAllActivityTimesEmptyTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        assertEquals(0, Elements.getAllActivityTimes(db, new Metric("%Y"), "ASC").size());
//    }
//
//    @Test
//    public void getAllActivityTimesByYearAscTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-10 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2021-11-10 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("6", "'2021-11-11 10:23'", 2);
//        List<String> activityTimes = Elements.getAllActivityTimes(db, new Metric("%Y"), "ASC");
//        assertEquals(2, activityTimes.size());
//        assertEquals("2020", activityTimes.get(0));
//        assertEquals("2021", activityTimes.get(1));
//    }
//
//    @Test
//    public void getAllActivityTimesByYearDescTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-10 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2021-11-10 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("6", "'2021-11-11 10:23'", 2);
//        List<String> activityTimes = Elements.getAllActivityTimes(db, new Metric("%Y"), "DESC");
//        assertEquals(2, activityTimes.size());
//        assertEquals("2021", activityTimes.get(0));
//        assertEquals("2020", activityTimes.get(1));
//    }
//
//    @Test
//    public void getAllActivityTimesByMonthTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-10 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2021-11-10 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("6", "'2021-11-11 10:23'", 2);
//        List<String> activityTimes = Elements.getAllActivityTimes(db, new Metric("%Y %m"), "ASC");
//        assertEquals(3, activityTimes.size());
//        assertEquals("2020 08", activityTimes.get(0));
//        assertEquals("2020 10", activityTimes.get(1));
//        assertEquals("2021 11", activityTimes.get(2));
//    }
//
//    @Test
//    public void getAllActivityTimesByWeekAscTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-10 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2021-02-10 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("6", "'2021-02-11 10:23'", 2);
//        List<String> activityTimes = Elements.getAllActivityTimes(db, new Metric("%Y %m %W"), "ASC");
//        assertEquals(3, activityTimes.size());
//        assertEquals("2020 08 31", activityTimes.get(0));
//        assertEquals("2020 10 40", activityTimes.get(1));
//        assertEquals("2021 02 06", activityTimes.get(2));
//    }
//
//    @Test
//    public void getAllActivityTimesByWeekDescTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-10 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2021-02-10 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("6", "'2021-02-11 10:23'", 2);
//        List<String> activityTimes = Elements.getAllActivityTimes(db, new Metric("%Y %m %W"), "DESC");
//        assertEquals(3, activityTimes.size());
//        assertEquals("2020 08 31", activityTimes.get(2));
//        assertEquals("2020 10 40", activityTimes.get(1));
//        assertEquals("2021 02 06", activityTimes.get(0));
//    }
//
//    @Test
//    public void getAllActivityTimesByDayTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-10 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2021-11-10 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("6", "'2021-11-11 10:23'", 2);
//        List<String> activityTimes = Elements.getAllActivityTimes(db, new Metric("%Y %m %W %j"), "DESC");
//        assertEquals(5, activityTimes.size());
//        assertEquals("2020 08 31 222", activityTimes.get(4));
//        assertEquals("2020 10 40 283", activityTimes.get(3));
//        assertEquals("2020 10 40 284", activityTimes.get(2));
//        assertEquals("2021 11 45 314", activityTimes.get(1));
//        assertEquals("2021 11 45 315", activityTimes.get(0));
//    }
//
//    @Test
//    public void getActivitiesGroupedByExerciseAndTimeFrameEmptyTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        Data data = new Data();
//        List<Activity> activities = Elements.getActivitiesGroupedByExerciseAndTimeFrame(db, new Metric("%Y"), data, "2020");
//        assertEquals(0, activities.size());
//    }
//
//    @Test
//    public void getActivitiesGroupedByExerciseAndTimeFrameByYearTest() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-10 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-08-09 10:23", "Ran", "kilometer", 5.0);
//        database.logActivity("2021-11-10 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("6", "'2021-11-11 10:23'", 2);
//        Data data = new Data();
//        List<Activity> activities2020 = Elements.getActivitiesGroupedByExerciseAndTimeFrame(db, new Metric("%Y"), data, "2020");
//        assertEquals(2, activities2020.size());
//        // kludge for (x,y) gives (beers,amount)
//        assertEquals(2020.0, activities2020.get(0).getBeers(), 0.0001);
//        assertEquals(15.0, activities2020.get(0).getAmount(), 0.0001);
//        assertEquals(2020.0, activities2020.get(1).getBeers(), 0.0001);
//        assertEquals(5.0, activities2020.get(1).getAmount(), 0.0001);
//        List<Activity> activities2021 = Elements.getActivitiesGroupedByExerciseAndTimeFrame(db, new Metric("%Y"), data, "2021");
//        assertEquals(2, activities2021.size());
//        assertEquals(2021.0, activities2021.get(0).getBeers(), 0.0001);
//        assertEquals(2.0, activities2021.get(0).getAmount(), 0.0001);
//        assertEquals(2021.0, activities2021.get(1).getBeers(), 0.0001);
//        assertEquals(5.0, activities2021.get(1).getAmount(), 0.0001);
//    }
//
//    @Test
//    public void getBeersDrankEmptyTable() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        assertEquals(0, Elements.getBeersDrank(db, new Metric("%Y"), "2020"));
//    }
//
//    @Test
//    public void getBeersDrankFullTable() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        database.logBeer("1", "'2020-08-09 10:23'", 1);
//        database.logActivity("2020-10-09 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("3", "'2020-10-10 10:23'", 1);
//        database.logActivity("2020-08-09 10:23", "Ran", "kilometer", 5.0);
//        database.logActivity("2021-11-10 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("6", "'2021-11-11 10:23'", 2);
//        assertEquals(2, Elements.getBeersDrank(db, new Metric("%Y"), "2020"));
//        assertEquals(2, Elements.getBeersDrank(db, new Metric("%Y"), "2021"));
//        assertEquals(0, Elements.getBeersDrank(db, new Metric("%Y"), "2022"));
//    }
//
//    @Test
//    public void getActivitiesPerformedEmptyTable() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        assertEquals(0, Elements.getActivitiesPerformed(db, new Metric("%Y"), "2020").size());
//    }
//
//    @Test
//    public void getActivitiesPerformedFullTable() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(1,2,2,5);");
//        db.execSQL("INSERT INTO " + GOALS_TABLE + " VALUES(2,1,2,1);");
//        database.logActivity("2020-08-09 10:23", "Walked", "kilometer", 5.0);
//        database.logActivity("2020-10-09 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("3", "'2020-10-10 10:23'", 1);
//        database.logActivity("2020-08-09 10:23", "Ran", "kilometer", 1.0);
//        database.logActivity("2021-11-10 10:23", "Walked", "kilometer", 5.0);
//        database.logBeer("6", "'2021-11-11 10:23'", 2);
//        Map<String, Integer> activityGroups2020 = Elements.getActivitiesPerformed(db, new Metric("%Y"), "2020");
//        assertEquals(2, activityGroups2020.size());
//        assertEquals(0, (int) activityGroups2020.get("Ran for 1.0 kilometer"));
//        assertEquals(10, (int) activityGroups2020.get("Walked for 10.0 kilometers"));
//        Map<String, Integer> activityGroups2021 = Elements.getActivitiesPerformed(db, new Metric("%Y"), "2021");
//        assertEquals(1, activityGroups2021.size());
//        assertEquals(5, (int) activityGroups2021.get("Walked for 5.0 kilometers"));
//    }
//
//    @Test
//    public void getSortedMeasurementsDefault() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        List<String> measurements = Elements.getSortedMeasurements(db, 1);
//        assertEquals(7, measurements.size());
//        assertEquals("class", measurements.get(0));
//        assertEquals("repetition", measurements.get(1));
//        assertEquals("mile", measurements.get(2));
//        assertEquals("kilometer", measurements.get(3));
//        assertEquals("hour", measurements.get(4));
//        assertEquals("minute", measurements.get(5));
//        assertEquals("second", measurements.get(6));
//    }
//
//    @Test
//    public void getSortedMeasurementsDefaultPlural() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        List<String> measurements = Elements.getSortedMeasurements(db, 2);
//        assertEquals(7, measurements.size());
//        assertEquals("classes", measurements.get(0));
//        assertEquals("repetitions", measurements.get(1));
//        assertEquals("miles", measurements.get(2));
//        assertEquals("kilometers", measurements.get(3));
//        assertEquals("hours", measurements.get(4));
//        assertEquals("minutes", measurements.get(5));
//        assertEquals("seconds", measurements.get(6));
//    }
//
//    @Test
//    public void getSortedMeasurementNewMeasurement() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        assertEquals(-1, Elements.getSortedMeasurement(db, 1, new Measurement(db, "feet")));
//    }
//
//    @Test
//    public void getSortedMeasurementSingularMeasurement() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        assertEquals(3, Elements.getSortedMeasurement(db, 1, new Measurement(db, "kilometer")));
//    }
//
//    @Test
//    public void getSortedMeasurementPluralMeasurement() {
//        SQLiteDatabase db = getDB();
//        Database database = new Database(db);
//        database.setupDatabase();
//        assertEquals(3, Elements.getSortedMeasurement(db, 2, new Measurement(db, "kilometer")));
//    }
//}
