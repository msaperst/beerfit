package com.fatmax.beerfit;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fatmax.beerfit.utilities.Activity;
import com.fatmax.beerfit.utilities.Database;
import com.fatmax.beerfit.utilities.Elements;
import com.fatmax.beerfit.utilities.Exercise;
import com.fatmax.beerfit.utilities.Measurement;
import com.fatmax.beerfit.utilities.Modal;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.fatmax.beerfit.utilities.Activity.DATE_FORMAT;
import static com.fatmax.beerfit.utilities.Activity.DATE_TIME_FORMAT;
import static com.fatmax.beerfit.utilities.Activity.TIME_FORMAT;
import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;
import static com.fatmax.beerfit.utilities.Database.MEASUREMENTS_TABLE;

public class ActivityModal {

    private final Context context;
    private final SQLiteDatabase sqLiteDatabase;
    private AlertDialog dialog;
    private Modal modal;
    private Calendar calendar;

    public ActivityModal(Context context, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.sqLiteDatabase = sqLiteDatabase;
        this.modal = new Modal(context, sqLiteDatabase);
    }

    public void launch(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View activityView = inflater.inflate(R.layout.modal_activity, null);
        modal.createSpinner(activityView, EXERCISES_TABLE, "past", R.id.activityExercise, false);
        modal.createSpinner(activityView, MEASUREMENTS_TABLE, "unit", R.id.activityMeasurement, true);
        calendar = Calendar.getInstance();
        // customization for edit or add
        if (activity.getId() != -1) {
            builder.setTitle(R.string.edit_your_activity);
            ((Spinner) activityView.findViewById(R.id.activityExercise)).setSelection(activity.getExercise().getId());
            ((TextView) activityView.findViewById(R.id.activityAmount)).setText(String.valueOf(activity.getAmount()));
            ((Spinner) activityView.findViewById(R.id.activityMeasurement)).setSelection(Elements.getSortedMeasurement(sqLiteDatabase, 1, activity.getMeasurement()) + 1);
            builder.setPositiveButton(R.string.delete, (dialog, id) -> delete(activity));
            builder.setNegativeButton(R.string.update, null);
            calendar.setTime(activity.getDateTime());
        } else {
            builder.setTitle(R.string.add_an_activity);
            builder.setPositiveButton(R.string.add_new, null);
        }
        ((TextView) activityView.findViewById(R.id.activityDate)).setText(DATE_FORMAT.format(calendar.getTime()));
        ((TextView) activityView.findViewById(R.id.activityDate)).setOnClickListener(this::pickDate);

        ((TextView) activityView.findViewById(R.id.activityTime)).setText(TIME_FORMAT.format(calendar.getTime()));
        ((TextView) activityView.findViewById(R.id.activityTime)).setOnClickListener(this::pickTime);

        builder.setView(activityView);
        this.dialog = builder.create();
        if (activity.getId() != -1) {
            dialog.setOnShowListener(dialog -> {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                button.setOnClickListener(view -> save(activityView, activity));
            });
        } else {
            dialog.setOnShowListener(dialog -> {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view -> save(activityView, activity));
            });
        }
        dialog.show();
    }

    public void pickDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (v, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            ((TextView) view).setText(DATE_FORMAT.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void pickTime(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (v, hour, minute) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            ((TextView) view).setText(TIME_FORMAT.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void save(View activityView, Activity activity) {
        boolean isFilledOut = true;
        TextView date = activityView.findViewById(R.id.activityDate);
        TextView time = activityView.findViewById(R.id.activityTime);
        Spinner exercise = activityView.findViewById(R.id.activityExercise);
        Spinner unit = activityView.findViewById(R.id.activityMeasurement);
        EditText amount = activityView.findViewById(R.id.activityAmount);

        // set the date, if there is an error, just use the current time
        try {
            activity.setDateTime(DATE_TIME_FORMAT.parse(date.getText() + " " + time.getText()));
        } catch (ParseException e) {
            activity.setDateTime(new Date());
        }
        // grab the rest of the content
        activity.setExercise(new Exercise(sqLiteDatabase, exercise.getSelectedItem().toString()));
        activity.setMeasurement(new Measurement(sqLiteDatabase, unit.getSelectedItem().toString()));
        if (!"".equals(amount.getText().toString())) {
            activity.setAmount(Double.parseDouble(amount.getText().toString()));
        }
        activity.calculateBeers();
        // check for our errors
        if (activity.getExercise().getId() == -1) {
            TextView errorText = (TextView) exercise.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(R.string.indicate_exercise);
            isFilledOut = false;
        }
        if (activity.getMeasurement().getId() == -1) {
            TextView errorText = (TextView) unit.getSelectedView();
            errorText.setError("");
            isFilledOut = false;
        }
        if (activity.getAmount() == 0) {
            amount.setError(context.getString(R.string.indicate_duration));
            isFilledOut = false;
        }
        if (!isFilledOut) {
            return;
        }
        activity.save();
        ActivitiesActivity.populateActivities(context, sqLiteDatabase);
        dialog.dismiss();
    }

    private void delete(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_activity);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(context.getString(R.string.confirm_activity_delete, activity.getString()));
        builder.setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
            activity.delete();
            ActivitiesActivity.populateActivities(context, sqLiteDatabase);
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }
}
