package com.fatmax.beerfit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITIES_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.BeerFitDatabase.MEASUREMENTS_TABLE;
import static com.fatmax.beerfit.MainActivity.getScreenWidth;

public class AddActivityActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    Calendar cal;
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // setup our two spinners
        createSpinner(ACTIVITIES_TABLE, "past", R.id.activitySelection);
        createSpinner(MEASUREMENTS_TABLE, "unit", R.id.activityDurationUnits);
        //setup our object widths
        findViewById(R.id.activityDate).getLayoutParams().width = (int) (getScreenWidth(this) * 0.3);
        findViewById(R.id.activityTime).getLayoutParams().width = (int) (getScreenWidth(this) * 0.3);
        findViewById(R.id.activityDurationInput).getLayoutParams().width = (int) (getScreenWidth(this) * 0.3);

        cal = Calendar.getInstance();

        Intent myIntent = getIntent();
        if (myIntent.hasExtra("activityId")) {
            //if this is an existing activity
            int activityId = myIntent.getIntExtra("activityId", -1);
            TextView header = findViewById(R.id.addActivityHeader);
            header.setText("Edit Your Activity");
            header.setTag(activityId);
            ((Button) findViewById(R.id.submitActivity)).setText("Update Activity");

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE id = " + activityId, null);
            cursor.moveToFirst();
            String dateTime = cursor.getString(1);

            cal.set(Calendar.YEAR, Integer.parseInt(dateTime.split(" ")[0].split("-")[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(dateTime.split(" ")[0].split("-")[1]));
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateTime.split(" ")[0].split("-")[2]));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateTime.split(" ")[1].split(":")[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(dateTime.split(" ")[1].split(":")[1]));

            ((Spinner) findViewById(R.id.activitySelection)).setSelection(cursor.getInt(2));
            ((TextView) findViewById(R.id.activityDate)).setText(dateTime.split(" ")[0]);
            ((TextView) findViewById(R.id.activityTime)).setText(dateTime.split(" ")[1]);
            ((TextView) findViewById(R.id.activityDurationInput)).setText(cursor.getString(4));
            ((Spinner) findViewById(R.id.activityDurationUnits)).setSelection(cursor.getInt(3));

            ((TextView) findViewById(R.id.activityDateTimeHeader)).setText("Update Time");

            //if beer activity
            if (cursor.getInt(2) == 0) {
                ((TextView) findViewById(R.id.activitySelectionHeader)).setText("Activity");
                ((TextView) findViewById(R.id.activityDurationHeader)).setText("Enter Amount");
                ((EditText) findViewById(R.id.activityDurationInput)).setInputType(InputType.TYPE_CLASS_NUMBER);
                //fix our activity
                Spinner activitySpinner = findViewById(R.id.activitySelection);
                ViewGroup.LayoutParams activityLayoutParams = activitySpinner.getLayoutParams();
                TextView activity = new TextView(this);
                activity.setId(activitySpinner.getId());
                activity.setText("Drank Beer");
                activity.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                activity.setLayoutParams(activityLayoutParams);
                ViewGroup rootLayout = (ViewGroup) activitySpinner.getParent();
                rootLayout.removeView(findViewById(R.id.activitySelection));
                rootLayout.addView(activity);
                //fix our units
                Spinner unitSpinner = findViewById(R.id.activityDurationUnits);
                ViewGroup.LayoutParams unitLayoutParams = unitSpinner.getLayoutParams();
                TextView unit = new TextView(this);
                unit.setId(unitSpinner.getId());
                unit.setText("beers");
                unit.setLayoutParams(unitLayoutParams);
                rootLayout.removeView(findViewById(R.id.activityDurationUnits));
                rootLayout.addView(unit);
            }
        } else {
            // otherwise initialize date time
            cal = Calendar.getInstance();
            ((TextView) findViewById(R.id.activityDate)).setText(DATE_FORMAT.format(cal.getTime()));
            ((TextView) findViewById(R.id.activityTime)).setText(TIME_FORMAT.format(cal.getTime()));
        }
    }

    private void createSpinner(String activity, String type, int p) {
        ArrayList<Object> activities = beerFitDatabase.getFullColumn(activity, type);
        activities.add(0, "");
        ArrayAdapter<Object> activitiesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activities);
        activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner activitySpinner = findViewById(p);
        activitySpinner.setAdapter(activitiesAdapter);
    }

    public void pickDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                ((TextView) findViewById(R.id.activityDate)).setText(DATE_FORMAT.format(calendar.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) - 1, cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void pickTime(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                ((TextView) findViewById(R.id.activityTime)).setText(TIME_FORMAT.format(calendar.getTime()));
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    public void logActivity(View view) {
        boolean isFilledOut = true;
        Spinner activity = null;
        TextView date = findViewById(R.id.activityDate);
        TextView time = findViewById(R.id.activityTime);
        Spinner units = null;
        EditText duration = findViewById(R.id.activityDurationInput);
        if (!isBeerActivity()) {
            activity = findViewById(R.id.activitySelection);
            if ("".equals(activity.getSelectedItem().toString())) {
                TextView errorText = (TextView) activity.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("You need to indicate some activity");
                isFilledOut = false;
            }
            units = findViewById(R.id.activityDurationUnits);
            if ("".equals(units.getSelectedItem().toString())) {
                TextView errorText = (TextView) units.getSelectedView();
                errorText.setError("");
                isFilledOut = false;
            }
        }
        if ("".equals(duration.getText().toString())) {
            duration.setError("You need to indicate some duration of your activity");
            isFilledOut = false;
        }
        if (!isFilledOut) {
            return;
        }
        TextView header = findViewById(R.id.addActivityHeader);
        if (header.getTag() instanceof Integer) {
            // if we're updating an activity
            int activityId = (int) header.getTag();
            beerFitDatabase.removeActivity(activityId);
            if (isBeerActivity()) {
                beerFitDatabase.logBeer(String.valueOf(activityId), "'" + date.getText() + " " + time.getText() + "'", Integer.parseInt(duration.getText().toString()));
            } else {
                beerFitDatabase.logActivity(String.valueOf(activityId), date.getText() + " " + time.getText(), activity.getSelectedItem().toString(), units.getSelectedItem().toString(), Double.parseDouble(duration.getText().toString()));
            }
        } else {
            beerFitDatabase.logActivity(date.getText() + " " + time.getText(), activity.getSelectedItem().toString(), units.getSelectedItem().toString(), Double.parseDouble(duration.getText().toString()));
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isBeerActivity() {
        return "Activity".contentEquals(((TextView) findViewById(R.id.activitySelectionHeader)).getText());
    }
}
