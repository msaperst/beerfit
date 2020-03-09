package com.fatmax.beerfit.unit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MetricUnitTest {

    @Test
    public void updateFilterEmptyTest() {
        List<String> filter = new ArrayList<>();
//        Metric metric = new Metric("YEAR", "%Y", filter);

//        metric.updateFilter("", "1234");
//        assertEquals(filter, metric.getFilters());
    }

    @Test
    public void updateFilterSingleTest() {
        List<String> filter = Collections.singletonList("strftime('%Y',time) = 'YEAR'");
//        Metric metric = new Metric("MONTH", "%Y %m", filter);

//        metric.updateFilter("tl", "1234");
//        assertEquals(filter, metric.getFilters());

//        metric.updateFilter("t", "1234");
//        assertEquals(Collections.singletonList("s1234rf1234ime('%Y',1234ime) = 'YEAR'"), metric.getFilters());
    }

    @Test
    public void updateFilterMultipleTest() {
        List<String> filter = Arrays.asList("strftime('%Y',time) = 'YEAR'", "strftime('%m',time) = 'MONTH'");
//        Metric metric = new Metric("WEEK", "%Y %m %W", filter);

//        metric.updateFilter("tl", "1234");
//        assertEquals(filter, metric.getFilters());

//        metric.updateFilter("t", "1234");
//        assertEquals(Arrays.asList("s1234rf1234ime('%Y',1234ime) = 'YEAR'", "s1234rf1234ime('%m',1234ime) = 'MONTH'"), metric.getFilters());
    }
}
