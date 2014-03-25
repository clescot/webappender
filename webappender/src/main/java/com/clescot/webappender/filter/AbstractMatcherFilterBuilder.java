package com.clescot.webappender.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class AbstractMatcherFilterBuilder<T extends AbstractMatcherFilter> implements FilterBuilder {

    private static final String LEVEL_FILTER_SEPARATOR = ",";
    public static final String FITLER_MATCH_PROPERTY = "MATCH";
    public static final String FILTER_MISMATCH_PROPERTY = "MISMATCH";
    public static final String FILTER_PROPERTY_SEPARATOR = ";";
    public static final String KEY_VALUE_SEPARATOR = ":";


    protected abstract T newFilter();

    protected abstract String getFilterHeader();

    protected abstract void handleCustomValue(T filter, String key,String value);

    public List<AbstractMatcherFilter<ILoggingEvent>> buildFilters(Map<String, List<String>> headers) {
        List<String> found = headers.get(getFilterHeader());
        List<AbstractMatcherFilter<ILoggingEvent>> filters = Lists.newArrayList();
        if ((!headers.isEmpty() && found != null && !found.isEmpty())) {
            String valueElement = found.get(0);
            List<String> values = Arrays.asList(valueElement.split(LEVEL_FILTER_SEPARATOR));
            for (String value : values) {
                AbstractMatcherFilter abstractMatcherFilter = getFilter(value);
                abstractMatcherFilter.start();
                filters.add(abstractMatcherFilter);
            }

        }
        return filters;
    }

    private  AbstractMatcherFilter getFilter(String valueElement) {
        T filter = newFilter();
        List<String> values = Arrays.asList(valueElement.split(FILTER_PROPERTY_SEPARATOR));
        List<String> filterReplyValues = Arrays.asList(FilterReply.NEUTRAL.toString(), FilterReply.ACCEPT.toString(), FilterReply.DENY.toString());
        for (String val : values) {
            List<String> strings = Arrays.asList(val.split(KEY_VALUE_SEPARATOR));
            String key = strings.get(0).trim();
            String value = strings.get(1).trim();
            if (key.startsWith(FITLER_MATCH_PROPERTY)) {
                if (Iterables.contains(filterReplyValues, value)) {
                    filter.setOnMatch(FilterReply.valueOf(value));
                }
            } else if (key.startsWith(FILTER_MISMATCH_PROPERTY)) {
                if (Iterables.contains(filterReplyValues, value)) {
                    filter.setOnMismatch(FilterReply.valueOf(value));
                }
            } else{
                handleCustomValue(filter,key,value);
            }
        }
        return filter;
    }



}
