package com.fatmax.beerfit.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Metric {

    private final String dateTimePattern;

    public Metric(String dateTimePattern) {
        this.dateTimePattern = dateTimePattern;
    }

    public String getDateTimePattern() {
        return dateTimePattern;
    }

    public String getTitle(String dateMetric) {
        String[] bits = dateMetric.split(" ");
        Calendar cal = Calendar.getInstance();
        switch (bits.length) {
            case 2:     //monthly
                try {
                    cal.setTime(new SimpleDateFormat("yyyy MM", Locale.US).parse(bits[0] + " " + bits[1]));
                } catch (ParseException e) {
                    return dateMetric;
                }
                return new SimpleDateFormat("MMMM yyyy", Locale.US).format(cal.getTime());
            case 3:     //weekly
                try {
                    cal.setTime(new SimpleDateFormat("yyyy ww", Locale.US).parse(bits[0] + " " + (Integer.parseInt(bits[2]))));
                } catch (NumberFormatException | ParseException e) {
                    return dateMetric;
                }
                return getWeeklySpread(cal);
            case 4:     //daily
                try {
                    cal.setTime(new SimpleDateFormat("yyyy D", Locale.US).parse(bits[0] + " " + bits[3]));
                } catch (ParseException e) {
                    return dateMetric;
                }
                return new SimpleDateFormat("EEEE, MMMM d yyyy", Locale.US).format(cal.getTime());
            default:    //yearly
                return dateMetric;
        }
    }

    public String getWeeklySpread(Calendar start) {
        start.add(Calendar.DATE, 8);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DATE, 6);
        if( !new SimpleDateFormat("yyyy").format(start.getTime()).equals(new SimpleDateFormat("yyyy").format(end.getTime()))) {
            return new SimpleDateFormat("MMMM d yyyy - ").format(start.getTime()) + new SimpleDateFormat("MMMM d yyyy").format(end.getTime());
        } else if ( !new SimpleDateFormat("MMMM").format(start.getTime()).equals(new SimpleDateFormat("MMMM").format(end.getTime()))) {
            return new SimpleDateFormat("MMMM d - ").format(start.getTime()) + new SimpleDateFormat("MMMM d yyyy").format(end.getTime());
        } else {
            return new SimpleDateFormat("MMMM d - ").format(start.getTime()) + new SimpleDateFormat("d yyyy").format(end.getTime());
        }
    }
}
