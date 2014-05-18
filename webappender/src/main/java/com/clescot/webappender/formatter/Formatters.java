package com.clescot.webappender.formatter;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class Formatters {


    private List<? extends Formatter> formatters;

    @Inject
    public Formatters(List<? extends Formatter> formatters) {
        this.formatters = formatters;
    }

    public Optional<? extends Formatter> findFormatter(final Map<String, List<String>> headers) {
        return Iterables.tryFind(formatters, new Predicate<Formatter>() {
            @Override
            public boolean apply(Formatter formatter) {
                return formatter.isActive(headers);
            }
        });
    }
}
