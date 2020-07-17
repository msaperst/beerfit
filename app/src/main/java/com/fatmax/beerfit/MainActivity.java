package com.fatmax.beerfit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fatmax.beerfit.utilities.Activity;
import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.views.ImportExport;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST = 112;
    SQLiteDatabase sqLiteDatabase;
    private MenuItem storedMenu;

    private ActionBarDrawerToggle t;

    /**
     * Calculates the number of beers remaining. Looks at the goals identified activities logged
     * against them and calculates the total number of beers earned.
     * Then subtracts all beers drank from the drank log.
     */
    public static void setBeersRemaining(Context context, SQLiteDatabase sqLiteDatabase) {
        int beersLeft = new Database(sqLiteDatabase).getBeersRemaining();
        TextView beerCounter = ((android.app.Activity) context).findViewById(R.id.beersLeft);
        if (beerCounter == null) {
            return;
        }
        if (beersLeft == 1) {
            beerCounter.setText(context.getString(R.string.one_beer_left));
        } else {
            beerCounter.setText(context.getString(R.string.beers_left, beersLeft));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        new Database(sqLiteDatabase).setupDatabase();
        setBeersRemaining(this, sqLiteDatabase);

        // setup our nav menu
        DrawerLayout dl = findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exportData || item.getItemId() == R.id.importData) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!ImportExport.hasPermissions(this, PERMISSIONS)) {
                // save off the menu, so we can use it once permissions granted
                this.storedMenu = item;
                //get required permissions
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST);
                return false;
            } else {
                return doImportExport(item);
            }
        }
        if (t.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Indicates the user drank a beer, so acts accordingly
     * Specifically, adds a beer drank to the beer log, and then
     * recalculates how many beers are remaining
     *
     * @param view the view to be used
     */
    public void drinkBeer(View view) {
        Activity drankBeer = new Activity(sqLiteDatabase, 0);
        drankBeer.save();
        setBeersRemaining(this, sqLiteDatabase);
    }

    public void addActivity(View view) {
        ActivityModal activityModal = new ActivityModal(this, sqLiteDatabase);
        activityModal.launch(new Activity(sqLiteDatabase));
    }

    public void viewSite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://beerfit.app"));
        startActivity(browserIntent);
    }

    public void viewActivities(MenuItem menuItem) {
        Intent intent = new Intent(this, ActivitiesActivity.class);
        startActivity(intent);
    }

    public void viewGoals(MenuItem menuItem) {
        Intent intent = new Intent(this, GoalsActivity.class);
        startActivity(intent);
    }

    public void viewMetrics(MenuItem menuItem) {
        Intent intent = new Intent(this, MetricsActivity.class);
        startActivity(intent);
    }
}
