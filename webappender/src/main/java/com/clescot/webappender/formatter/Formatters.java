package com.clescot.webappender.formatter;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Formatters {

    private final static List<? extends Formatter> FORMATTERS = Arrays.asList(new FireLoggerFormatter(), new ChromeLoggerFormatter(),new ConsoleFormatter());

    public static Optional<? extends Formatter> findFormatter(final Map<String, List<String>> headers) {
        return Iterables.tryFind(FORMATTERS, new Predicate<Formatter>() {
            @Override
            public boolean apply(Formatter formatter) {
                return formatter.isActive(headers);
            }
        });
    }
}
