package com.clescot.webappender.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;

public class LevelFilterBuilder extends AbstractMatcherFilterBuilder<LevelFilter>{

    private static final String FILTER_LEVEL_LEVEL_PROPERTY = "LEVEL";

    public static final String X_LEVEL_FILTER = "X-wa-level-filter";


    @Override
    protected LevelFilter newFilter() {
        return new LevelFilter();
    }

    @Override
    protected String getFilterHeader() {
        return X_LEVEL_FILTER;
    }

    @Override
    protected void handleCustomValue(LevelFilter filter, String key,String value) {
        if (key.startsWith(FILTER_LEVEL_LEVEL_PROPERTY)) {
            filter.setLevel(Level.toLevel(value));
        }
    }

}
