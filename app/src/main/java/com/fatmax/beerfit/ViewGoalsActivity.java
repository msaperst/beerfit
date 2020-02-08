package com.fatmax.beerfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.GOALS_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.MEASUREMENTS_TABLE;
import static com.fatmax.beerfit.TableBuilder.createDeleteButton;
import static com.fatmax.beerfit.TableBuilder.createEditButton;
import static com.fatmax.beerfit.TableBuilder.createTextView;

public class ViewGoalsActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goals);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // dynamically build our table
        TableLayout tableLayout = findViewById(R.id.goalsTable);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + GOALS_TABLE + ".id, " + ACTIVITIES_TABLE + ".current, " + GOALS_TABLE + ".amount, " + MEASUREMENTS_TABLE + ".unit FROM " + GOALS_TABLE + " LEFT JOIN " + ACTIVITIES_TABLE + " ON " + GOALS_TABLE + ".activity = " + ACTIVITIES_TABLE + ".id LEFT JOIN " + MEASUREMENTS_TABLE + " ON " + GOALS_TABLE + ".measurement = " + MEASUREMENTS_TABLE + ".id", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // setup our table row
            TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            row.setTag(cursor.getInt(0));

            // setup our cells
            TextView activity = createTextView(this, "goal", cursor.getString(1) + " for " + cursor.getString(2) + " " + cursor.getString(3));
            activity.setTextSize(20);
            // create and setup our edit button
            ImageButton editButton = createEditButton(this);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editGoal(v);
                }
            });
            // create and setup our delete button
            ImageButton deleteButton = createDeleteButton(this);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteGoal(v);
                }
            });

            row.addView(activity);
            row.addView(editButton);
            row.addView(deleteButton);
            tableLayout.addView(row);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void addGoal(View view) {
        Intent intent = new Intent(this, AddGoalActivity.class);
        startActivity(intent);
    }

    void editGoal(View editButton) {
        TableRow row = (TableRow) editButton.getParent();
        int activityId = (int) row.getTag();
    }

    void deleteGoal(View deleteButton) {
        final TableRow row = (TableRow) deleteButton.getParent();
        final int activityId = (int) row.getTag();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Goal");
        alert.setMessage("Are you sure to delete the goal of " + ((TextView) row.findViewWithTag("goal")).getText());
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                beerFitDatabase.removeGoal(activityId);
                ((LinearLayout) findViewById(R.id.goalsTable)).removeView(row);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
}
