package com.fatmax.beerfit.utilities;

import java.util.ArrayList;
import java.util.List;

public class Metric {

    private final String type;
    private final String dateTimePattern;
    private List<String> filters;
    private final String title;

    public Metric(String type, String title, String dateTimePattern, List<String> filters) {
        this.type = type;
        this.title = title;
        this.dateTimePattern = dateTimePattern;
        this.filters = filters;
    }

    public String getType() {
        return type;
    }

    public String getDateTimePattern() {
        return dateTimePattern;
    }

    public List<String> getFilters() {
        return filters;
    }

    public String getTitle() {
        return title;
    }

    public void updateFilter(String pattern, String replacement) {
        List<String> newFilters = new ArrayList<>();
        for (String filter : this.filters) {
            newFilters.add(filter.replaceAll(pattern, replacement));
        }
        this.filters = newFilters;
    }
}
