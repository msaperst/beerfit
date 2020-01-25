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
    BeerFitDatabase beerFitDatabase;

    TextView beerCounter;
    ImageButton drankBeer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beerCounter = findViewById(R.id.beersLeft);
        drankBeer = findViewById(R.id.drankABeer);

        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);
        beerFitDatabase.setupDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBeersRemaining();
    }

    /**
     * Calculates the number of beers remaining. Looks at the goals identified activities logged
     * against them and calculates the total number of beers earned.
     * Then subtracts all beers drank from the drank log.
     *
     * @return the number of beers remaining
     */
    void setBeersRemaining() {
        beerCounter.setText(String.valueOf(beerFitDatabase.getBeersRemaining()));
    }

    /**
     * Indicates the user drank a beer, so acts accordingly
     * Specifically, adds a beer drank to the beer log, and then
     * recalculates how many beers are remaining
     *
     * @param view
     */
    public void drinkBeer(View view) {
        beerFitDatabase.logBeer();
        setBeersRemaining();
    }

    public void addActivity(View view) {
        Intent intent = new Intent(this, AddActivityActivity.class);
        startActivity(intent);
    }

    public void viewActivities(View view) {
        Intent intent = new Intent(this, ViewActivitiesActivity.class);
        startActivity(intent);
    }

    public void viewGoals(View view) {
        Intent intent = new Intent(this, ViewGoalsActivity.class);
        startActivity(intent);
    }
}
