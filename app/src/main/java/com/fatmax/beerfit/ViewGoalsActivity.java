package com.fatmax.beerfit;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Measures;
import com.fatmax.beerfit.utilities.TableBuilder;

import java.util.Arrays;

import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class ViewGoalsActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    Database database;
    TableBuilder tableBuilder;
    Measures measures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goals);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        database = new Database(sqLiteDatabase);
        tableBuilder = new TableBuilder(this);

        // dynamically build our table
        TableLayout tableLayout = findViewById(R.id.goalsTable);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + GOALS_TABLE + ".id, " + EXERCISES_TABLE + ".current, " + GOALS_TABLE + ".amount, " + MEASUREMENTS_TABLE + ".unit FROM " + GOALS_TABLE + " LEFT JOIN " + EXERCISES_TABLE + " ON " + GOALS_TABLE + ".activity = " + EXERCISES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + GOALS_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // setup our cells
            TextView activity = tableBuilder.createTextView(cursor.getString(1) + " for " + cursor.getString(2) + " " + cursor.getString(3), "goal");
            activity.setTextSize(20);
            // create and setup our edit button
            ImageButton editButton = tableBuilder.createEditButton();
            editButton.setOnClickListener(this::editGoal);
            // create and setup our delete button
            ImageButton deleteButton = tableBuilder.createDeleteButton();
            deleteButton.setOnClickListener(this::deleteGoal);

            // build our row
            tableLayout.addView(tableBuilder.createTableRow(cursor.getString(0),
                    Arrays.asList(activity, editButton, deleteButton)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.goals_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        measures = new Measures(this, sqLiteDatabase);
        switch (item.getItemId()) {
            case R.id.editExercises:
                measures.editExercises();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addGoal(View view) {
        Intent intent = new Intent(this, AddGoalActivity.class);
        startActivity(intent);
    }

    void editGoal(View editButton) {
        TableRow row = (TableRow) editButton.getParent();
        int goalId = Integer.parseInt(row.getTag().toString());
        Intent intent = new Intent(this, AddGoalActivity.class);
        intent.putExtra("goalId", goalId);
        startActivity(intent);
    }

    void deleteGoal(View deleteButton) {
        final TableRow row = (TableRow) deleteButton.getParent();
        final int goalId = Integer.parseInt(row.getTag().toString());
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Goal");
        alert.setMessage("Are you sure to delete the goal of " + ((TextView) row.findViewWithTag("goal")).getText());
        alert.setPositiveButton("YES", (dialog, which) -> {
            database.removeGoal(goalId);
            ((LinearLayout) findViewById(R.id.goalsTable)).removeView(row);
            dialog.dismiss();
        });
        alert.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
        alert.show();
    }
}
