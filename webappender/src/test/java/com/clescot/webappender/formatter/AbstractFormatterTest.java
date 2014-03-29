package com.clescot.webappender.formatter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
@Ignore
public abstract class AbstractFormatterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFormatterTest.class);

     ArrayList<Row> getILoggingEvents() {
        Row iLoggingEvent = new Row(new LoggingEvent(this.getClass().getName(), (ch.qos.logback.classic.Logger) LOGGER, Level.ERROR, "dummy message", null, null));
        Row iLoggingEvent2 = new Row(new LoggingEvent(this.getClass().getName(), (ch.qos.logback.classic.Logger) LOGGER, Level.ERROR, "dummy message", null, null));
        ArrayList<Row> iLoggingEvents = Lists.newArrayList();
        iLoggingEvents.add(iLoggingEvent);
        iLoggingEvents.add(iLoggingEvent2);
        return iLoggingEvents;
    }
}
