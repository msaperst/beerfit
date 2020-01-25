package com.fatmax.beerfit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;

    TextView beerCounter;
    ImageButton drankBeer;

    int beersRemaining = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beerCounter = findViewById(R.id.beersLeft);
        drankBeer = findViewById(R.id.drankABeer);

        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        BeerFitDatabase beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);
        beerFitDatabase.setupDatabase();
        //on app launch, set beer to 10
        beerCounter.setText(String.valueOf(calculateBeersRemaining()));
    }

    /**
     * Calculates the number of beers remaining. Looks at the goals identified activities logged
     * against them and calculates the total number of beers earned.
     * Then subtracts all beers drank from the drank log.
     * @return the number of beers remaining
     */
    private int calculateBeersRemaining() {
        // swap out the below with something meaningful
        return beersRemaining;
    }

    /**
     * Indicates the user drank a beer, so acts accordingly
     * Specifically, adds a beer drank to the beer log, and then
     * recalculates how many beers are remaining
     * @param view
     */
    public void drinkBeer(View view) {
        //TODO - needs to add to the log, not what happens below should add this to the log
        beersRemaining = beersRemaining - 1;
        beerCounter.setText(String.valueOf(calculateBeersRemaining()));

    }

    public void addActivity(View view) {
        Intent intent = new Intent(this, AddActivityActivity.class);
        startActivity(intent);
    }
}
