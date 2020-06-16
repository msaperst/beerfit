package com.fatmax.beerfit.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.ContactsContract;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImportExportInstrumentedTest {

    private static final String DATABASE_NAME = "testDB";
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    File exportDir = new File(Environment.getExternalStorageDirectory(), "Beerfit");

//    @Before
//    public void setupTest() {
//        deleteExports();
//    }

//    @After
//    public void cleanupDB() {
//        wipeOutDB();
//    }

//    @Test
//    public void constructorTest() {
//        SQLiteDatabase db = getDB();
//        File file = new File(Environment.getExternalStorageDirectory(), "Beerfit");
//        assertFalse(new File(Environment.getExternalStorageDirectory(), "Beerfit").exists());
//        ImportExport importExport = new ImportExport(appContext, db);
//        assertTrue(new File(Environment.getExternalStorageDirectory(), "Beerfit").exists());
//    }

//    @Test
//    public void hasPermissionsTest() {
//        String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        assertFalse(ImportExport.hasPermissions(appContext, PERMISSIONS));
//    }

//    private void wipeOutDB() {
//        SQLiteDatabase.deleteDatabase(appContext.getDatabasePath(DATABASE_NAME));
//    }

//    private SQLiteDatabase getDB() {
//        SQLiteOpenHelper helper = new SQLiteOpenHelper(appContext, DATABASE_NAME, null, 1) {
//            @Override
//            public void onCreate(SQLiteDatabase db) {
//
//            }
//
//            @Override
//            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//            }
//        };
//        return helper.getWritableDatabase();
//    }

//    private void deleteExports() {
//        String[] entries = exportDir.list();
//        for (String s : entries) {
//            File currentFile = new File(exportDir.getPath(), s);
//            currentFile.delete();
//        }
//    }
}
