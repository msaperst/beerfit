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

import com.fatmax.beerfit.utilities.Database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.fatmax.beerfit.MainActivity.getScreenWidth;
import static com.fatmax.beerfit.utilities.Database.ACTIVITY_LOG_TABLE;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class AddActivityActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    Database database;

    Calendar cal;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        //retrieve the current activities
        sqLiteDatabase = openOrCreateDatabase("beerfit", MODE_PRIVATE, null);
        database = new Database(sqLiteDatabase);

        // setup our two spinners
        createSpinner(EXERCISES_TABLE, "past", R.id.activitySelection);
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
            setTitle(getString(R.string.edit_your_activity));
            Button submit = findViewById(R.id.submitActivity);
            submit.setTag(activityId);
            submit.setText(getString(R.string.update_activity));

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + ACTIVITY_LOG_TABLE + " WHERE id = " + activityId, null);
            cursor.moveToFirst();
            String dateTime = cursor.getString(1);

            cal.set(Calendar.YEAR, Integer.parseInt(dateTime.split(" ")[0].split("-")[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(dateTime.split(" ")[0].split("-")[1]) - 1);  //subtracting one as that's how months are counted
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateTime.split(" ")[0].split("-")[2]));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateTime.split(" ")[1].split(":")[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(dateTime.split(" ")[1].split(":")[1]));

            ((Spinner) findViewById(R.id.activitySelection)).setSelection(cursor.getInt(2));
            ((TextView) findViewById(R.id.activityDate)).setText(dateTime.split(" ")[0]);
            ((TextView) findViewById(R.id.activityTime)).setText(dateTime.split(" ")[1]);
            ((TextView) findViewById(R.id.activityDurationInput)).setText(cursor.getString(4));
            ((Spinner) findViewById(R.id.activityDurationUnits)).setSelection(cursor.getInt(3));

            ((TextView) findViewById(R.id.activityDateTimeHeader)).setText(R.string.update_time);

            //if beer activity
            if (cursor.getInt(2) == 0) {
                ((TextView) findViewById(R.id.activitySelectionHeader)).setText(R.string.activity);
                ((TextView) findViewById(R.id.activityDurationHeader)).setText(R.string.enter_amount);
                ((EditText) findViewById(R.id.activityDurationInput)).setInputType(InputType.TYPE_CLASS_NUMBER);
                //fix our activity
                Spinner activitySpinner = findViewById(R.id.activitySelection);
                ViewGroup.LayoutParams activityLayoutParams = activitySpinner.getLayoutParams();
                TextView activity = new TextView(this);
                activity.setId(activitySpinner.getId());
                activity.setText(R.string.drank_beer);
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
                unit.setText(R.string.beers);
                unit.setLayoutParams(unitLayoutParams);
                rootLayout.removeView(findViewById(R.id.activityDurationUnits));
                rootLayout.addView(unit);
            }
            cursor.close();
        } else {
            // otherwise initialize date time
            cal = Calendar.getInstance();
            ((TextView) findViewById(R.id.activityDate)).setText(DATE_FORMAT.format(cal.getTime()));
            ((TextView) findViewById(R.id.activityTime)).setText(TIME_FORMAT.format(cal.getTime()));
        }
    }

    private void createSpinner(String activity, String type, int p) {
        List<Object> activities = database.getFullColumn(activity, type);
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
                ((TextView) findViewById(R.id.activityTime)).setText(TIME_FORMAT.format(calendar.getTime()));
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    public void addActivity(View view) {
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
                errorText.setText(R.string.indicate_exercise);
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
            duration.setError(getString(R.string.indicate_duration));
            isFilledOut = false;
        }
        if (!isFilledOut) {
            return;
        }
        Button submit = findViewById(R.id.submitActivity);
        if (submit.getTag() instanceof Integer) {
            // if we're updating an activity
            int activityId = (int) submit.getTag();
            database.removeActivity(activityId);
            if (isBeerActivity()) {
                database.logBeer(String.valueOf(activityId), "'" + date.getText() + " " + time.getText() + "'", Integer.parseInt(duration.getText().toString()));
            } else {
                database.logActivity(String.valueOf(activityId), date.getText() + " " + time.getText(), activity.getSelectedItem().toString(), units.getSelectedItem().toString(), Double.parseDouble(duration.getText().toString()));
            }
        } else {
            database.logActivity(date.getText() + " " + time.getText(), activity.getSelectedItem().toString(), units.getSelectedItem().toString(), Double.parseDouble(duration.getText().toString()));
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isBeerActivity() {
        return "Activity".contentEquals(((TextView) findViewById(R.id.activitySelectionHeader)).getText());
    }
}
