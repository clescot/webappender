package com.clescot.webappender.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class ThresholdFilterBuilder implements FilterBuilder{
    public static final String X_THRESHOLD_FILTER = "X-wa-threshold-filter";
    public List<? extends Filter<ILoggingEvent>> buildFilters(Optional<Map<String, List<String>>> headers) {
        List<ThresholdFilter> filters = Lists.newArrayList();
        if(headers.isPresent()&& !headers.get().isEmpty()) {
            Optional<List<String>> values = Optional.fromNullable(headers.get().get(X_THRESHOLD_FILTER));
            if (values.isPresent() && !values.get().isEmpty()){
                Level threshold = Level.toLevel(values.get().get(0));
                ThresholdFilter thresholdFilter = new ThresholdFilter();
                thresholdFilter.setLevel(threshold.toString());
                thresholdFilter.start();
                filters.add(thresholdFilter);
            }
        }
        return filters;
    }
}
