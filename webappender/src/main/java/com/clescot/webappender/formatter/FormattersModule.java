package com.clescot.webappender.formatter;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import java.util.Arrays;
import java.util.List;

public class FormattersModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<List<? extends Formatter>>() {
        }).toInstance(Arrays.asList(new FireLoggerFormatter(), new ChromeLoggerFormatter(),new ConsoleFormatter()));
    }
}
