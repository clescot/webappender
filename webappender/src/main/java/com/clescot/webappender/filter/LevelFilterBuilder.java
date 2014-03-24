package com.clescot.webappender.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LevelFilterBuilder {
    public static final String LEVEL_FILTER_SEPARATOR = ",";
    public static final String LEVEL_FILTER_PROPERTY_SEPARATOR = ";";
    public static final String FITLER_LEVEL_MATCH_PROPERTY = "MATCH";
    public static final String FILTER_LEVEL_MISMATCH_PROPERTY = "MISMATCH";
    public static final String FILTER_LEVEL_LEVEL_PROPERTY = "LEVEL";
    public static final String KEY_VALUE_SEPARATOR = ":";

    public static Collection<LevelFilter> checkLevelFilter(List<String> headers) {
        List<LevelFilter> levelFilters = Lists.newArrayList();
        while (headers!=null&&!headers.isEmpty()) {
            String valueElement = headers.get(0);
            List<String> values = Arrays.asList(valueElement.split(LEVEL_FILTER_SEPARATOR));
            for (String value : values) {
                LevelFilter levelFilter = getLevelFilter(value);
                levelFilter.start();
                levelFilters.add(levelFilter);
            }

        }
        return levelFilters;
    }
    private static LevelFilter getLevelFilter(String valueElement) {
        LevelFilter levelFilter = new LevelFilter();
        List<String> values = Arrays.asList(valueElement.split(LEVEL_FILTER_PROPERTY_SEPARATOR));
        List<String> filterReplyValues = Arrays.asList(FilterReply.NEUTRAL.toString(), FilterReply.ACCEPT.toString(), FilterReply.DENY.toString());
        for (String value : values) {
            if (value.trim().startsWith(FITLER_LEVEL_MATCH_PROPERTY)) {
                String firstValueTrimmed = getFirstValueTrimmed(value);

                if (Iterables.contains(filterReplyValues, firstValueTrimmed)){
                    levelFilter.setOnMatch(FilterReply.valueOf(firstValueTrimmed));
                }
            } else if (value.trim().startsWith(FILTER_LEVEL_MISMATCH_PROPERTY)) {
                String firstValueTrimmed = getFirstValueTrimmed(value);
                if (Iterables.contains(filterReplyValues,firstValueTrimmed)){
                    levelFilter.setOnMismatch(FilterReply.valueOf(firstValueTrimmed));
                }
            } else if (value.trim().startsWith(FILTER_LEVEL_LEVEL_PROPERTY)) {
                String firstValueTrimmed = getFirstValueTrimmed(value);
                levelFilter.setLevel(Level.toLevel(firstValueTrimmed));
            }
        }
        return levelFilter;
    }

    private static String getFirstValueTrimmed(String value) {
        List<String> matchValues = Arrays.asList(value.split(KEY_VALUE_SEPARATOR));
        return matchValues.get(1).trim();
    }

}
