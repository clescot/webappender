package com.clescot.webappender.collector;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.sift.AppenderFactory;

public class ListAppenderPerThreadFactory implements AppenderFactory<ILoggingEvent> {

    private LoggerContext loggerContext;

    public ListAppenderPerThreadFactory(LoggerContext loggerContext) {
        this.loggerContext = loggerContext;
    }

    @Override
    public Appender<ILoggingEvent> buildAppender(Context context, String discriminatingValue) throws JoranException {
        CustomListAppender appender = new CustomListAppender();
        appender.setName(Thread.currentThread().getId() + "");
        appender.setContext(loggerContext);
        appender.start();
        return appender;
    }

}
