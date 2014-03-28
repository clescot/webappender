package com.clescot.webappender.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class AbstractMatcherFilterBuilder<T extends AbstractMatcherFilter> implements FilterBuilder {

    private static final String LEVEL_FILTER_SEPARATOR = ",";
    public static final String FITLER_MATCH_PROPERTY = "MATCH";
    public static final String FILTER_MISMATCH_PROPERTY = "MISMATCH";
    public static final String FILTER_PROPERTY_SEPARATOR = ";";
    public static final String KEY_VALUE_SEPARATOR = ":";
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractMatcherFilterBuilder.class);

    protected abstract T newFilter();

    protected abstract String getFilterHeader();

    protected abstract void handleCustomValue(T filter, String key, String value);

    public List<AbstractMatcherFilter<ILoggingEvent>> buildFilters(Optional<Map<String, List<String>>> headers) {
        List<AbstractMatcherFilter<ILoggingEvent>> filters = Lists.newArrayList();
        if (headers.isPresent() && !headers.get().isEmpty()) {
            Optional<List<String>> found = Optional.fromNullable(headers.get().get(getFilterHeader()));
            if (found.isPresent() && !found.get().isEmpty()) {
                String valueElement = found.get().get(0);
                List<String> values = Arrays.asList(valueElement.split(LEVEL_FILTER_SEPARATOR));
                for (String value : values) {
                    Optional<T> abstractMatcherFilter = getFilter(value);
                    if(abstractMatcherFilter.isPresent()) {
                        T filter = abstractMatcherFilter.get();
                        filter.start();
                        filters.add(filter);
                    }
                }
            }
        }

        return filters;
    }

    private Optional<T> getFilter(String valueElement) {
        Optional<T> filter = Optional.of(newFilter());
        List<String> values = Arrays.asList(valueElement.split(FILTER_PROPERTY_SEPARATOR));
        List<String> filterReplyValues = Arrays.asList(FilterReply.NEUTRAL.toString(), FilterReply.ACCEPT.toString(), FilterReply.DENY.toString());
        for (String val : values) {
            List<String> strings = Arrays.asList(val.split(KEY_VALUE_SEPARATOR));
            if(strings.size()!=2){
                LOGGER.warn("header \"{}\" does not contains 2 elements separated by a "+KEY_VALUE_SEPARATOR+" in its value=\"{}\"",getFilterHeader(),val);
                filter = Optional.absent();
                break;
            }else {
                String key = strings.get(0).trim();
                String value = strings.get(1).trim();
                if (key.startsWith(FITLER_MATCH_PROPERTY)) {
                    if (Iterables.contains(filterReplyValues, value)) {
                        filter.get().setOnMatch(FilterReply.valueOf(value));
                    }
                } else if (key.startsWith(FILTER_MISMATCH_PROPERTY)) {
                    if (Iterables.contains(filterReplyValues, value)) {
                        filter.get().setOnMismatch(FilterReply.valueOf(value));
                    }
                } else {
                    handleCustomValue(filter.get(), key, value);
                }
            }
        }
        return filter;
    }


}
