package com.clescot.webappender.collector;

import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.FilterReply;

public class FilterableSiftingAppender extends SiftingAppender {

    @Override
    public FilterReply getFilterChainDecision(ILoggingEvent event) {
        return getChildAppender().getFilterChainDecision(event);
    }

    public Appender<ILoggingEvent> getChildAppender(){
        return getAppenderTracker().getOrCreate(Thread.currentThread().getId() + "", System.currentTimeMillis());
    }
}
