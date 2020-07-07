package com.fatmax.beerfit.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fatmax.beerfit.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.List;

import static com.fatmax.beerfit.utilities.Database.ACTIVITIES_TABLE;

public class Measures {

    private final Context context;
    private final SQLiteDatabase sqLiteDatabase;
    private AlertDialog dialog;
    private String selectedOption;
    private TextView activityColorView;

    public Measures(Context context, SQLiteDatabase database) {
        this.context = context;
        this.sqLiteDatabase = database;
    }

    public void editActivities() {
        Database database = new Database(sqLiteDatabase);
        List<Object> allActivities = database.getFullColumn(ACTIVITIES_TABLE, "current");
        String[] selectActivity = allActivities.toArray(new String[allActivities.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.modify_activity);
        builder.setSingleChoiceItems(selectActivity, -1, (dialog, which) -> {
            selectedOption = selectActivity[which];
            this.dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
            this.dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        });
        builder.setNeutralButton(context.getString(R.string.add_new), (dialog, which) -> addNewActivity());
        builder.setNegativeButton(context.getString(R.string.edit), (dialog, which) -> editActivity());
        builder.setPositiveButton(context.getString(R.string.delete), (dialog, which) -> deleteActivity());
        this.dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void addNewActivity() {
        setupActivityModal(new Activity(sqLiteDatabase));
    }

    private void editActivity() {
        setupActivityModal(new Activity(sqLiteDatabase, selectedOption));
    }

    private void deleteActivity() {
        Activity activity = new Activity(sqLiteDatabase, selectedOption);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_activity);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        if( activity.safeToDelete() ) {
            builder.setMessage(context.getString(R.string.confirm_activity_delete, activity.getCurrent()));
            builder.setPositiveButton(android.R.string.yes, (dialog, whichButton) -> activity.deleteActivity());
            builder.setNegativeButton(android.R.string.no, null);
        } else {
            builder.setMessage(R.string.unable_to_delete_activity);
        }
        builder.show();
    }

    private void setupActivityModal(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_activity);
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout editActivityModal = (LinearLayout) inflater.inflate(R.layout.modal_edit_activity, null);
        activityColorView = editActivityModal.findViewById(R.id.editActivityColor);
        ((EditText) editActivityModal.findViewById(R.id.editActivityName)).setText(activity.getCurrent());
        ((EditText) editActivityModal.findViewById(R.id.editActivityPastName)).setText(activity.getPast());
        activityColorView.setBackgroundColor(activity.getColor());
        activityColorView.setOnClickListener(v -> pickColor(activity));
        builder.setView(editActivityModal);
        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel, null);
        this.dialog = builder.create();
        dialog.setOnShowListener(dialog -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                // check to ensure activity is filled in
                EditText activityName = editActivityModal.findViewById(R.id.editActivityName);
                if (TextUtils.isEmpty(activityName.getText())) {
                    activityName.setError(context.getString(R.string.activity_required));
                }
                activity.setCurrent(activityName.getText().toString());
                // check to ensure past tense activity is filled in
                EditText activityPastName = editActivityModal.findViewById(R.id.editActivityPastName);
                if (TextUtils.isEmpty(activityPastName.getText())) {
                    activityPastName.setError(context.getString(R.string.past_activity_required));
                }
                activity.setPast(activityPastName.getText().toString());
                // check for uniqueness
                if (!activity.isActivityUnique()) {
                    activityName.setError(context.getString(R.string.duplicate_activity_description));
                }
                if (!activity.isColorUnique()) {
                    activityColorView.setTextColor(Color.RED);
                    activityColorView.setText(R.string.duplicate_activity_color);
                }
                if (!"".equals(activity.getPast()) && !"".equals(activity.getCurrent()) && activity.isActivityUnique() && activity.isColorUnique()) {
                    activity.saveActivity();
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }

    public void pickColor(Activity activity) {
        ColorPickerDialogBuilder
                .with(context)
                .setTitle(R.string.choose_color)
                .initialColor(activity.getColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton(R.string.choose, (dialog, selectedColor, allColors) -> {
                    activity.setColor(selectedColor);
                    activityColorView.setBackgroundColor(selectedColor);
                    activityColorView.setText("");
                })
                .setNegativeButton(R.string.cancel, null)
                .build()
                .show();
    }
}
