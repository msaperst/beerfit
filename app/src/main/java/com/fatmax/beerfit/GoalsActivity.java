package com.fatmax.beerfit;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Elements;
import com.fatmax.beerfit.utilities.Goal;
import com.fatmax.beerfit.views.Measures;
import com.fatmax.beerfit.views.ViewBuilder;

import java.util.Collections;
import java.util.List;

public class GoalsActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    Database database;
    Measures measures;

    public static void populateGoals(Context context, SQLiteDatabase sqLiteDatabase) {
        ViewBuilder viewBuilder = new ViewBuilder(context);
        TableLayout tableLayout = ((Activity) context).findViewById(R.id.goalsTable);
        tableLayout.removeAllViews();
        List<Goal> goals = Elements.getAllGoals(sqLiteDatabase);
        for (Goal goal : goals) {
            // setup our cells
            TextView goalView = viewBuilder.createTextView(goal.getString(), "goal");
            goalView.setTextSize(20);
            goalView.setOnClickListener(view -> editGoal(context, sqLiteDatabase, view));
            tableLayout.addView(viewBuilder.createTableRow(String.valueOf(goal.getId()),
                    Collections.singletonList(goalView)));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        database = new Database(sqLiteDatabase);

        // dynamically build our table
        populateGoals(this, sqLiteDatabase);
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
            case R.id.addGoal:
                addGoal(null);
                return true;
            case R.id.editExercises:
                measures.editExercises();
                return true;
            case R.id.editMeasurements:
                measures.editMeasurements();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addGoal(View view) {
        GoalModal goalModal = new GoalModal(this, sqLiteDatabase);
        goalModal.launch(new Goal(sqLiteDatabase));
    }

    static void editGoal(Context context, SQLiteDatabase sqLiteDatabase, View view) {
        TableRow row = (TableRow) view.getParent();
        Goal goal = new Goal(sqLiteDatabase, Integer.parseInt(row.getTag().toString()));
        GoalModal goalModal = new GoalModal(context, sqLiteDatabase);
        goalModal.launch(goal);
    }
}
