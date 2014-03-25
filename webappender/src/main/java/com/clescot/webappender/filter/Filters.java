package com.clescot.webappender.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.*;

public class Filters {

    public static final  List<FilterBuilder> FILTER_BUILDERS = Arrays.asList(new ThresholdFilterBuilder(), new LevelFilterBuilder(),new JaninoEventEvaluatorBuilder());
    public static Collection<? extends Filter<ILoggingEvent>> getFilters(final Map<String, List<String>> headers){
        final List<Filter<ILoggingEvent>> filters = Lists.newArrayList();

        Collections2.transform(FILTER_BUILDERS,new Function<FilterBuilder,  List<? extends Filter<ILoggingEvent>>>() {
            @Override
            public  List<? extends Filter<ILoggingEvent>> apply(com.clescot.webappender.filter.FilterBuilder input) {
                List<? extends Filter<ILoggingEvent>> buildFilters = input.buildFilters(headers);
                filters.addAll(buildFilters);
                return buildFilters;
            }
        });
        return filters;
    }
}
