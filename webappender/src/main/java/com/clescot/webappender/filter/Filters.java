package com.clescot.webappender.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Filters {


    public static Collection<? extends Filter<ILoggingEvent>> getFilters(Map<String, List<String>> headers) {
        ArrayList<Filter<ILoggingEvent>> filters = Lists.newArrayList(ThresholdFilterBuilder.buildFilters(headers));
        filters.addAll( LevelFilterBuilder.buildFilters(headers));
        return filters;
    }
}
