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

import static com.fatmax.beerfit.utilities.Database.EXERCISES_TABLE;

public class Measures {

    private final Context context;
    private final SQLiteDatabase sqLiteDatabase;
    private AlertDialog dialog;
    private String selectedOption;
    private TextView exerciseColorView;

    public Measures(Context context, SQLiteDatabase database) {
        this.context = context;
        this.sqLiteDatabase = database;
    }

    public void editExercises() {
        Database database = new Database(sqLiteDatabase);
        List<Object> allExercises = database.getFullColumn(EXERCISES_TABLE, "current");
        String[] selectExercise = allExercises.toArray(new String[allExercises.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.modify_exercise);
        builder.setSingleChoiceItems(selectExercise, -1, (dialog, which) -> {
            selectedOption = selectExercise[which];
            this.dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
            this.dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        });
        builder.setNeutralButton(context.getString(R.string.add_new), (dialog, which) -> addNewExercise());
        builder.setNegativeButton(context.getString(R.string.edit), (dialog, which) -> editExercise());
        builder.setPositiveButton(context.getString(R.string.delete), (dialog, which) -> deleteExercise());
        this.dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void addNewExercise() {
        setupExerciseModal(new Exercise(sqLiteDatabase));
    }

    private void editExercise() {
        setupExerciseModal(new Exercise(sqLiteDatabase, selectedOption));
    }

    private void deleteExercise() {
        Exercise exercise = new Exercise(sqLiteDatabase, selectedOption);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_exercise);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        if( exercise.safeToDelete() ) {
            builder.setMessage(context.getString(R.string.confirm_exercise_delete, exercise.getCurrent()));
            builder.setPositiveButton(android.R.string.yes, (dialog, whichButton) -> exercise.delete());
            builder.setNegativeButton(android.R.string.no, null);
        } else {
            builder.setMessage(R.string.unable_to_delete_exercise);
        }
        builder.show();
    }

    private void setupExerciseModal(Exercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_exercise);
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout editExerciseModal = (LinearLayout) inflater.inflate(R.layout.modal_edit_exercise, null);
        exerciseColorView = editExerciseModal.findViewById(R.id.editExerciseColor);
        ((EditText) editExerciseModal.findViewById(R.id.editExerciseName)).setText(exercise.getCurrent());
        ((EditText) editExerciseModal.findViewById(R.id.editExercisePastName)).setText(exercise.getPast());
        exerciseColorView.setBackgroundColor(exercise.getColor());
        exerciseColorView.setOnClickListener(v -> pickColor(exercise));
        builder.setView(editExerciseModal);
        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel, null);
        this.dialog = builder.create();
        dialog.setOnShowListener(dialog -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                // check to ensure exercise is filled in
                EditText exerciseName = editExerciseModal.findViewById(R.id.editExerciseName);
                if (TextUtils.isEmpty(exerciseName.getText())) {
                    exerciseName.setError(context.getString(R.string.exercise_required));
                }
                exercise.setCurrent(exerciseName.getText().toString());
                // check to ensure past tense exercise is filled in
                EditText exercisePastName = editExerciseModal.findViewById(R.id.editExercisePastName);
                if (TextUtils.isEmpty(exercisePastName.getText())) {
                    exercisePastName.setError(context.getString(R.string.past_exercise_required));
                }
                exercise.setPast(exercisePastName.getText().toString());
                // check for uniqueness
                if (!exercise.isCurrentUnique()) {
                    exerciseName.setError(context.getString(R.string.duplicate_exercise_description));
                }
                if (!exercise.isColorUnique()) {
                    exerciseColorView.setTextColor(Color.RED);
                    exerciseColorView.setText(R.string.duplicate_exercise_color);
                }
                if (!"".equals(exercise.getPast()) && !"".equals(exercise.getCurrent()) && exercise.isCurrentUnique() && exercise.isColorUnique()) {
                    exercise.save();
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }

    public void pickColor(Exercise exercise) {
        ColorPickerDialogBuilder
                .with(context)
                .setTitle(R.string.choose_color)
                .initialColor(exercise.getColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton(R.string.choose, (dialog, selectedColor, allColors) -> {
                    exercise.setColor(selectedColor);
                    exerciseColorView.setBackgroundColor(selectedColor);
                    exerciseColorView.setText("");
                })
                .setNegativeButton(R.string.cancel, null)
                .build()
                .show();
    }
}
