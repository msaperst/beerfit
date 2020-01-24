package com.fatmax.beerfit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase database;

    TextView beerCounter;
    ImageButton drankBeer;

    int beersRemaining = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beerCounter = findViewById(R.id.beersLeft);
        drankBeer = findViewById(R.id.drankABeer);

        setupDatabase();
        // TODO - need to fix this to pull in total beers (will need log, etc)
        beersRemaining = 10;
        //on app launch, set beer to 10
        beerCounter.setText(String.valueOf(beersRemaining));

        drankBeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beersRemaining = beersRemaining - 1;
                beerCounter.setText(String.valueOf(beersRemaining));
            }
        });
    }

    private void setupDatabase() {
        database = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        if (!isTableExists("Measurement")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Measurement(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR);");
            database.execSQL("INSERT INTO Measurement VALUES(null,'time');");
            database.execSQL("INSERT INTO Measurement VALUES(null,'distance');");
        }
        if (!isTableExists("Goals")) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Goals(id INTEGER PRIMARY KEY AUTOINCREMENT, type VARCHAR,Activity VARCHAR,Measurement INTEGER,Amount INTEGER);");
        }
    }

    private boolean isTableExists(String tableName) {
        boolean isExist = false;
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }
}
