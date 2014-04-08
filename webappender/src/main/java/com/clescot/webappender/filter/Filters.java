package com.clescot.webappender.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Filters {

    private static final  List<FilterBuilder> FILTER_BUILDERS = Arrays.asList(new ThresholdFilterBuilder(), new LevelFilterBuilder(),new JaninoEventEvaluatorBuilder());

    public static Collection<? extends Filter<ILoggingEvent>> getFilters(final Map<String, List<String>> headers){
        final List<Filter<ILoggingEvent>> filters = Lists.newArrayList();

        Function<FilterBuilder, List<? extends Filter<ILoggingEvent>>> function = new Function<FilterBuilder, List<? extends Filter<ILoggingEvent>>>() {
            @Override
            public List<? extends Filter<ILoggingEvent>> apply(FilterBuilder input) {
                List<? extends Filter<ILoggingEvent>> buildFilters = input.buildFilters(Optional.fromNullable(headers));
                filters.addAll(buildFilters);
                return buildFilters;
            }
        };
        return Lists.newArrayList(Iterables.concat(Collections2.transform(FILTER_BUILDERS, function)));

    }
}
