package com.fatmax.beerfit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewActivitiesActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_activities);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // dynamically build our table
        TableLayout tableLayout = findViewById(R.id.activitiesTable);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT ActivityLog.id, ActivityLog.time, Activities.type, ActivityLog.amount, Measurements.unit FROM ActivityLog LEFT JOIN Activities ON ActivityLog.activity = Activities.id LEFT JOIN Measurements ON ActivityLog.measurement = Measurements.id ORDER BY ActivityLog.time DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // setup our table row
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            row.setTag(cursor.getInt(0));

            // setup our cells
            TextView time = createTextView(this, cursor.getString(1));
            TextView activity = createTextView(this, cursor.getString(2));
            TextView duration = createTextView(this, "for " + cursor.getString(3) + " " + cursor.getString(4));
            ImageButton delete = createDeleteButton(this);

            //if we drank, put in different values
            if( cursor.getString(2) == null || "".equals(cursor.getString(2))) {
                activity.setText("Drank a beer");
                duration.setText("");
            }
            // build our rows
            row.addView(time);
            row.addView(activity);
            row.addView(duration);
            row.addView(delete);
            tableLayout.addView(row);
            cursor.moveToNext();
        }
        cursor.close();
    }

    static TextView createIdTextView(Context context, Cursor res) {
        TextView id = new TextView(context);
        id.setText(res.getString(0));
        id.setWidth(0);
        return id;
    }

    static TextView createTextView(Context context, String text) {
        TextView view = new TextView(context);
        view.setPadding(10, 2, 10, 2);
        view.setText(text);
        return view;
    }

    ImageButton createDeleteButton(Context context) {
        ImageButton button = new ImageButton(context);
        button.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));
        button.setContentDescription("Delete Activity");
        button.setMaxHeight(5);
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setImageResource(android.R.drawable.ic_menu_delete);
        button.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_dark)));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteActivity(v);
            }
        });
        return button;
    }

    void deleteActivity(final View deleteButton) {
        final TableRow row = (TableRow) deleteButton.getParent();
        final int activityId = (int) row.getTag();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Activity");
        alert.setMessage("Are you sure to delete the activity on " + beerFitDatabase.getActivityTime(activityId));
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                beerFitDatabase.removeActivity(activityId);
                ((LinearLayout) findViewById(R.id.activitiesTable)).removeView(row);
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
