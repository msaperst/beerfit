package com.fatmax.beerfit;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fatmax.beerfit.utilities.Activity;
import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Elements;
import com.fatmax.beerfit.utilities.Goal;
import com.fatmax.beerfit.utilities.TableBuilder;

import java.util.Arrays;
import java.util.List;

import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.GOALS_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class ViewGoalsActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    Database database;
    TableBuilder tableBuilder;

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
        List<Goal> goals = Elements.getAllGoals(sqLiteDatabase);
        for (Goal goal : goals) {
            // setup our cells
            TextView goalView = tableBuilder.createTextView(goal.getExercise().getCurrent() + " for " + goal.getAmount() + " " + goal.getMeasurement().getUnit(), "goal");
            goalView.setTextSize(20);
            // create and setup our edit button
            ImageButton editButton = tableBuilder.createEditButton();
            editButton.setOnClickListener(this::editGoal);
            // create and setup our delete button
            ImageButton deleteButton = tableBuilder.createDeleteButton();
            deleteButton.setOnClickListener(this::deleteGoal);

            // build our row
            tableLayout.addView(tableBuilder.createTableRow(String.valueOf(goal.getId()),
                    Arrays.asList(goalView, editButton, deleteButton)));
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
