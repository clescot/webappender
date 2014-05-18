package com.clescot.webappender.filter;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import java.util.Arrays;
import java.util.List;

public class FiltersModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<List<FilterBuilder>>() {
        }).toInstance(Arrays.asList(//
                new ThresholdFilterBuilder(),//
                new LevelFilterBuilder(), //
                new JaninoEventEvaluatorBuilder()//
        ));
    }
}
