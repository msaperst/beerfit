package com.fatmax.beerfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class BeerFitDatabase {

    SQLiteDatabase database;

    BeerFitDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    void setupDatabase() {
        // while i'm working on things, we'll need to wipe out all of these
        database.execSQL("DROP TABLE IF EXISTS Measurements");
        database.execSQL("DROP TABLE IF EXISTS Activities");
        database.execSQL("DROP TABLE IF EXISTS Goals");

        if (!isTableExists("Measurements")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Measurements(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR, unit VARCHAR);");
            database.execSQL("INSERT INTO Measurements VALUES(1,'time','minutes');");
            database.execSQL("INSERT INTO Measurements VALUES(2,'distance','kilometers');");
        }
        if (!isTableExists("Activities")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Activities(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR);");
            database.execSQL("INSERT INTO Activities VALUES(1,'Walking');");
            database.execSQL("INSERT INTO Activities VALUES(2,'Running');");
            database.execSQL("INSERT INTO Activities VALUES(3,'Cycling');");
            database.execSQL("INSERT INTO Activities VALUES(4,'Lifting');");
            database.execSQL("INSERT INTO Activities VALUES(5,'Soccer');");
        }
        if (!isTableExists("Goals")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Goals(id INTEGER PRIMARY KEY AUTOINCREMENT, Activity INTEGER, Measurement INTEGER, Amount INTEGER);");
            database.execSQL("INSERT INTO Goals VALUES(1,1,2,5);");
            database.execSQL("INSERT INTO Goals VALUES(2,2,2,5);");
            database.execSQL("INSERT INTO Goals VALUES(3,3,2,10);");
            database.execSQL("INSERT INTO Goals VALUES(4,4,1,30);");
            database.execSQL("INSERT INTO Goals VALUES(5,5,1,30);");
        }
    }

    boolean isTableExists(String tableName) {
        boolean isExist = false;
        Cursor cursor = database.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    public ArrayList<String> getFullColumn(String table, String column) {
        ArrayList<String> array_list = new ArrayList<>();

        //hp = new HashMap();
        Cursor res =  database.rawQuery( "SELECT * FROM " + table, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(column)));
            res.moveToNext();
        }
        return array_list;
    }
}
