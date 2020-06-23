package com.fatmax.beerfit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.ImportExport;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST = 112;
    SQLiteDatabase sqLiteDatabase;
    Database database;
    TextView beerCounter;
    ImageButton drankBeer;
    private MenuItem storedMenu;

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beerCounter = findViewById(R.id.beersLeft);
        drankBeer = findViewById(R.id.drankABeer);

        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        database = new Database(sqLiteDatabase);
        database.setupDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBeersRemaining();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!ImportExport.hasPermissions(this, PERMISSIONS)) {
            // save off the menu, so we can use it once permissions granted
            this.storedMenu = item;
            //get required permissions
            ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST);
            return false;
        } else {
            return doImportExport(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doImportExport(storedMenu);
            } else {
                Toast.makeText(this, "The app was not allowed to read your store.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean doImportExport(MenuItem item) {
        ImportExport importExport = new ImportExport(this, sqLiteDatabase);
        switch (item.getItemId()) {
            case R.id.exportData:
                importExport.exportData();
                return true;
            case R.id.importData:
                importExport.importData();
                beerCounter.invalidate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void viewActivities(View view) {
        Intent intent = new Intent(this, ViewActivitiesActivity.class);
        startActivity(intent);
    }

    public void viewGoals(View view) {
        Intent intent = new Intent(this, ViewGoalsActivity.class);
        startActivity(intent);
    }

    public void viewMetrics(View view) {
        Intent intent = new Intent(this, ViewMetricsActivity.class);
        startActivity(intent);
    }
}
