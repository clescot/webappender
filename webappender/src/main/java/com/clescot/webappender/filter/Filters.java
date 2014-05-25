package com.clescot.webappender.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Filters {

    private final List<FilterBuilder> filterBuilders;

    @Inject
    public Filters(List<FilterBuilder> filterBuilders) {
        this.filterBuilders = filterBuilders;
    }

    public  Collection<? extends Filter<ILoggingEvent>> buildFilters(final Map<String, List<String>> headers){
        Optional<Map<String, List<String>>> optionalHeaders = Optional.fromNullable(headers);
        List<Filter<ILoggingEvent>> filters = Lists.newArrayList();
        if(optionalHeaders.isPresent()){
            final Map<String, List<String>> headersWithInsensitiveKey = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);
            headersWithInsensitiveKey.putAll(optionalHeaders.get());

            Function<FilterBuilder, List<? extends Filter<ILoggingEvent>>> function = new Function<FilterBuilder, List<? extends Filter<ILoggingEvent>>>() {
                @Override
                public List<? extends Filter<ILoggingEvent>> apply(FilterBuilder input) {

                    return input.buildFilters(Optional.of(headersWithInsensitiveKey));
                }
            };


            filters= Lists.newArrayList(Iterables.concat(Collections2.transform(filterBuilders, function)));
        }

        return filters;
    }
}
