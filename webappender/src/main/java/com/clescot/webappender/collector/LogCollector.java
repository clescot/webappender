package com.clescot.webappender.collector;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AppenderTracker;
import com.clescot.webappender.Row;
import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LogCollector {
    public static final String SIFTING_APPENDER_KEY = "SIFT";
    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private SiftingAppender siftingAppender;
    private ch.qos.logback.classic.Logger rootLogger;

    private LogCollector() {
        rootLogger = loggerContext.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        siftingAppender = new SiftingAppender();
        siftingAppender.setName(SIFTING_APPENDER_KEY);
        siftingAppender.setContext(loggerContext);
        ThreadIdBasedDiscriminator discriminator = new ThreadIdBasedDiscriminator();
        discriminator.start();
        siftingAppender.setDiscriminator(discriminator);

        ListAppenderPerThreadFactory appenderFactory = new ListAppenderPerThreadFactory(loggerContext);
        siftingAppender.setAppenderFactory(appenderFactory);
        siftingAppender.start();
        rootLogger.addAppender(siftingAppender);
    }

    public void shutdown() {
        rootLogger.detachAppender(siftingAppender);
    }

    /**
     * called one time, at init,  in the application lifeCycle.
     *
     * @return
     */
    public static LogCollector newLogCollector() {
        return new LogCollector();
    }

    /**
     * called each time a new Thread is created.
     *
     * @return
     */
    public List<Row> getLogs() {
        CustomListAppender perThreadIdAppender = getChildAppender();
        return Lists.newArrayList(perThreadIdAppender.getRows());
    }

    public void removeCurrentThreadAppender() {
        getAppenderTracker().endOfLife(Thread.currentThread().getId() + "");
        getAppenderTracker().removeStaleComponents(System.currentTimeMillis());
    }

    private AppenderTracker<ILoggingEvent> getAppenderTracker() {
        return ((SiftingAppender) rootLogger.getAppender(SIFTING_APPENDER_KEY)).getAppenderTracker();
    }


    public CustomListAppender getChildAppender() {
        return (CustomListAppender) getAppenderTracker().getOrCreate(Thread.currentThread().getId() + "", System.currentTimeMillis());
    }


}
