package com.fatmax.beerfit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.fatmax.beerfit.MainActivity.getScreenWidth;

public class AddActivityActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    BeerFitDatabase beerFitDatabase;

    Calendar cal;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        beerFitDatabase = new BeerFitDatabase(sqLiteDatabase);

        // setup our two spinners
        createSpinner("Activities", "type", R.id.activitySelection);
        createSpinner("Measurements", "unit", R.id.activityDurationUnits);
        //setup our object widths
        findViewById(R.id.activityDate).getLayoutParams().width = (int) (getScreenWidth(this) * 0.3);
        findViewById(R.id.activityTime).getLayoutParams().width = (int) (getScreenWidth(this) * 0.3);
        findViewById(R.id.activityDurationInput).getLayoutParams().width = (int) (getScreenWidth(this) * 0.3);
        // initialize date time
        cal = Calendar.getInstance();
        ((TextView) findViewById(R.id.activityDate)).setText(dateFormat.format(cal.getTime()));
        ((TextView) findViewById(R.id.activityTime)).setText(timeFormat.format(cal.getTime()));
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
                ((TextView) findViewById(R.id.activityDate)).setText(dateFormat.format(calendar.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void pickTime(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                ((TextView) findViewById(R.id.activityTime)).setText(timeFormat.format(calendar.getTime()));
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    public void logActivity(View view) {
        boolean isFilledOut = true;
        Spinner activity = findViewById(R.id.activitySelection);
        TextView date = findViewById(R.id.activityDate);
        TextView time = findViewById(R.id.activityTime);
        Spinner units = findViewById(R.id.activityDurationUnits);
        EditText duration = findViewById(R.id.activityDurationInput);
        if ("".equals(activity.getSelectedItem().toString())) {
            TextView errorText = (TextView) activity.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("You need to indicate some activity");
            isFilledOut = false;
        }
        if ("".equals(units.getSelectedItem().toString())) {
            TextView errorText = (TextView) units.getSelectedView();
            errorText.setError("");
            isFilledOut = false;
        }
        if ("".equals(duration.getText().toString())) {
            duration.setError("You need to indicate some duration of your activity");
            isFilledOut = false;
        }
        if (!isFilledOut) {
            return;
        }
        beerFitDatabase.logActivity(date.getText() + " " + time.getText(), activity.getSelectedItem().toString(), units.getSelectedItem().toString(), Double.valueOf(duration.getText().toString()));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
