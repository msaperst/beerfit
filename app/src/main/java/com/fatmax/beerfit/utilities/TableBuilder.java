package com.fatmax.beerfit.utilities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.fatmax.beerfit.R;

import java.util.List;

public class TableBuilder {

    private Context context;

    public TableBuilder(Context context) {
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

    public ImageButton createEditButton() {
        ImageButton button = new ImageButton(context);
        button.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));
        button.setContentDescription("Edit Activity");
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setImageResource(android.R.drawable.ic_menu_edit);
        button.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_orange_light)));
        return button;
    }

    public ImageButton createDeleteButton() {
        ImageButton button = new ImageButton(context);
        button.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));
        button.setContentDescription("Delete Activity");
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setImageResource(android.R.drawable.ic_menu_delete);
        button.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_dark)));
        return button;
    }
}
