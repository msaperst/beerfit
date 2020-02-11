package com.fatmax.beerfit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class TableBuilder {

    private TableBuilder() {}

    static TextView createTextView(Context context, String tag, String text) {
        TextView view = new TextView(context);
        view.setPadding(10, 2, 10, 2);
        view.setTag(tag);
        view.setText(text);
        view.setTextSize(12);
        return view;
    }

    static ImageButton createEditButton(Context context) {
        ImageButton button = new ImageButton(context);
        button.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));
        button.setContentDescription("Edit Activity");
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setImageResource(android.R.drawable.ic_menu_edit);
        button.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_orange_light)));
        return button;
    }

    static ImageButton createDeleteButton(Context context) {
        ImageButton button = new ImageButton(context);
        button.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));
        button.setContentDescription("Delete Activity");
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setImageResource(android.R.drawable.ic_menu_delete);
        button.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_dark)));
        return button;
    }
}
