package com.fatmax.beerfit.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.fatmax.beerfit.MainActivity;
import com.fatmax.beerfit.utilities.CSVReader;
import com.fatmax.beerfit.utilities.CSVWriter;
import com.fatmax.beerfit.utilities.Database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImportExport {

    File exportDir = new File(Environment.getExternalStorageDirectory(), "BeerFit");
    Context context;
    private SQLiteDatabase sqLiteDatabase;

    public ImportExport(Context context, SQLiteDatabase database) {
        this.context = context;
        this.sqLiteDatabase = database;
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void exportData() {
        exportData(Database.EXERCISES_TABLE);
        exportData(Database.GOALS_TABLE);
        exportData(Database.MEASUREMENTS_TABLE);
        exportData(Database.ACTIVITIES_TABLE);
    }

    public void exportData(String table) {
        File beerfitExport = new File(exportDir, table + ".csv");
        try {
            beerfitExport.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileOutputStream(beerfitExport));
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);
            int columns = cursor.getColumnCount();
            csvWriter.writeNext(cursor.getColumnNames());
            while (cursor.moveToNext()) {
                String[] elements = new String[columns];
                for (int col = 0; col < columns; col++) {
                    elements[col] = (cursor.getString(col));
                }
                csvWriter.writeNext(elements);
            }
            csvWriter.close();
            cursor.close();

        } catch (IOException e) {
            Log.e("bad export", e.getMessage());
        }
    }

    public void importData() {
        List<String> existingImports = new ArrayList<>();
        List<String> allTables = Arrays.asList(Database.EXERCISES_TABLE, Database.GOALS_TABLE, Database.MEASUREMENTS_TABLE, Database.ACTIVITIES_TABLE);
        for (String table : allTables) {
            if (new File(exportDir, table + ".csv").exists()) {
                existingImports.add(table);
            }
        }
        String[] tables = existingImports.toArray(new String[existingImports.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Table to Import");
//        builder.setMessage("Note that this entire table will be wiped out and replaced with data in file");
        builder.setItems(tables, (dialog, which) -> importData(tables[which]));
        builder.show();
    }

    public void importData(String table) {
        File beerfitExport = new File(exportDir, table + ".csv");
        // wipe out our table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table);
        // create a fresh table
        Database database = new Database(sqLiteDatabase);
        database.setupDatabase();
        // add all our data
        try {
            CSVReader csvReader = new CSVReader(new FileReader(beerfitExport));
            String[] colNames = csvReader.readNext();
            String[] values = csvReader.readNext();
            while (values != null) {
                if (!database.doesDataExist(table, Integer.parseInt(values[0]))) {
                    StringBuilder sqlStatement = new StringBuilder("INSERT INTO " + table + " VALUES (");
                    for (int col = 0; col < colNames.length; col++) {
                        String colType = database.getColumnType(table, colNames[col]);
                        if (colType.equals("TEXT") || colType.equals("VARCHAR")) {
                            sqlStatement.append("'").append(values[col]).append("',");
                        } else {
                            sqlStatement.append(values[col]).append(",");
                        }
                    }
                    sqlStatement.setLength(sqlStatement.length() - 1);
                    sqlStatement.append(");");
                    sqLiteDatabase.execSQL(sqlStatement.toString());
                }
                values = csvReader.readNext();
            }
            csvReader.close();
        } catch (IOException e) {
            Log.e("bad import", e.getMessage());
        }
        MainActivity.setBeersRemaining(context, sqLiteDatabase);
    }

    //TODO - verify data from imports
}
