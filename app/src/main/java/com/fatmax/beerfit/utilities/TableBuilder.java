package com.fatmax.beerfit.utilities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.fatmax.beerfit.R;

public class TableBuilder {

    private TableBuilder() {
    }

    public static TextView createTextView(Context context, String tag, String text) {
        TextView view = new TextView(context);
        view.setTextAppearance(R.style.BodyText);
        view.setTag(tag);
        view.setText(text);
        return view;
    }

    public static ImageButton createEditButton(Context context) {
        ImageButton button = new ImageButton(context);
        button.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));
        button.setContentDescription("Edit Activity");
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setImageResource(android.R.drawable.ic_menu_edit);
        button.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_orange_light)));
        return button;
    }

    public static ImageButton createDeleteButton(Context context) {
        ImageButton button = new ImageButton(context);
        button.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));
        button.setContentDescription("Delete Activity");
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setImageResource(android.R.drawable.ic_menu_delete);
        button.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_dark)));
        return button;
    }
}
