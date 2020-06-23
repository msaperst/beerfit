package com.fatmax.beerfit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fatmax.beerfit.utilities.Database;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    Database database;

    TextView beerCounter;
    ImageButton drankBeer;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beerCounter = findViewById(R.id.beersLeft);
        drankBeer = findViewById(R.id.drankABeer);

        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        database = new Database(sqLiteDatabase);
        database.setupDatabase();

        // setup our nav menu
        dl = (DrawerLayout) findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBeersRemaining();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Calculates the number of beers remaining. Looks at the goals identified activities logged
     * against them and calculates the total number of beers earned.
     * Then subtracts all beers drank from the drank log.
     */
    void setBeersRemaining() {
        beerCounter.setText(String.valueOf(database.getBeersRemaining()));
    }

    /**
     * Indicates the user drank a beer, so acts accordingly
     * Specifically, adds a beer drank to the beer log, and then
     * recalculates how many beers are remaining
     *
     * @param view
     */
    public void drinkBeer(View view) {
        database.logBeer();
        setBeersRemaining();
    }

    public void addActivity(View view) {
        Intent intent = new Intent(this, AddActivityActivity.class);
        startActivity(intent);
    }

    public void viewActivities(MenuItem menuItem) {
        Intent intent = new Intent(this, ViewActivitiesActivity.class);
        startActivity(intent);
    }

    public void viewGoals(MenuItem menuItem) {
        Intent intent = new Intent(this, ViewGoalsActivity.class);
        startActivity(intent);
    }

    public void viewMetrics(MenuItem menuItem) {
        Intent intent = new Intent(this, ViewMetricsActivity.class);
        startActivity(intent);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
