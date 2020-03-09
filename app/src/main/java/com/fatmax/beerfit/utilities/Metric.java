package com.fatmax.beerfit.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Metric {

    private final String type;
    private final String dateTimePattern;
    private final String patternMin;
    private final String patternMax;

    public Metric(String type, String dateTimePattern, String patternMin, String patternMax) {
        this.type = type;
        this.dateTimePattern = dateTimePattern;
        this.patternMin = patternMin;
        this.patternMax = patternMax;

    }

    String getPatternMin() {
        return patternMin;
    }

    String getPatternMax() {
        return patternMax;
    }

    public String getType() {
        return type;
    }

    public String getDateTimePattern() {
        return dateTimePattern;
    }

    public String getTitle(String dateMetric) {
        String[] bits = dateMetric.split(" ");
        Calendar cal = Calendar.getInstance();
        switch (bits.length) {
            case 2:
                try {
                    cal.setTime(new SimpleDateFormat("yyyy MM", Locale.US).parse(bits[0] + " " + bits[1]));
                } catch (ParseException e) {
                    return dateMetric;
                }
                return new SimpleDateFormat("MMMM yyyy", Locale.US).format(cal.getTime());
            case 3:
                try {
                    cal.setTime(new SimpleDateFormat("yyyy MM ww", Locale.US).parse(bits[0] + " " + bits[1] + " " + (Integer.parseInt(bits[2]) + 1)));
                } catch (NumberFormatException | ParseException e) {
                    return dateMetric;
                }
                return new SimpleDateFormat("'Week' W, MMMM yyyy", Locale.US).format(cal.getTime());
            case 4:
                try {
                    cal.setTime(new SimpleDateFormat("yyyy D", Locale.US).parse(bits[0] + " " + bits[3]));
                } catch (ParseException e) {
                    return dateMetric;
                }
                return new SimpleDateFormat("EEEE, MMMM d yyyy", Locale.US).format(cal.getTime());
            default:
                return dateMetric;
        }
    }
}
