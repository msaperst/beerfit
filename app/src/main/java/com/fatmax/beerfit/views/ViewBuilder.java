package com.fatmax.beerfit.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.fatmax.beerfit.R;

import java.util.List;

public class ViewBuilder {

    private Context context;

    public ViewBuilder(Context context) {
        this.context = context;
    }

    public TableRow createTableRow(List<View> cells) {
        return createTableRow(null, cells);
    }

    public TableRow createTableRow(String tag, List<View> cells) {
        TableRow row = new TableRow(context);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        if (tag != null) {
            row.setTag(tag);
        }
        for (View cell : cells) {
            row.addView(cell);
        }
        return row;
    }

    public TextView createHeaderView(String text) {
        TextView view = createTextView(text);
        view.setTextAppearance(R.style.HeaderText);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    public TextView createTextView(String text) {
        return createTextView(text, null);
    }

    public TextView createTextView(String text, String tag) {
        TextView view = new TextView(context);
        view.setTextAppearance(R.style.BodyText);
        view.setText(text);
        if (tag != null) {
            view.setTag(tag);
        }
        return view;
    }

    public ImageButton noGoalsAlert() {
        ImageButton button = new ImageButton(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(80, 80);
        button.setId(R.id.noGoalsAlert);
        button.setLayoutParams(layoutParams);
        button.setContentDescription(context.getString(R.string.no_goals_present));
        button.setImageResource(android.R.drawable.ic_dialog_alert);
        button.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)));
        button.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));
        button.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.no_goals_present);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(R.string.no_goals_set);
            builder.show();
        });
        return button;
    }
}
