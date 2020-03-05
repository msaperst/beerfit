package com.fatmax.beerfit.utilities;

import java.util.List;

public class Metric {

    private final String type;
    private final String dateTimePattern;
    private List<String> filter;
    private final String title;

    public Metric(String type, String title, String dateTimePattern, List<String> filter) {
        this.type = type;
        this.title = title;
        this.dateTimePattern = dateTimePattern;
        this.filter = filter;
    }

    public String getType() {
        return type;
    }

    public String getDateTimePattern() {
        return dateTimePattern;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }

    public String getTitle() {
        return title;
    }
}
