package com.clescot.webappender.collector;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AppenderTracker;
import com.clescot.webappender.Row;
import com.clescot.webappender.filter.LevelFilterBuilder;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LogCollector {
    public static final String SIFTING_APPENDER_KEY = "WEB_APPENDER_SIFT";
    public static final String X_VERBOSE_LOGS = "X-wa-verbose-logs";
    public static final String X_THRESHOLD_FILTER = "X-wa-threshold-filter";
    public static final String X_LEVEL_FILTER = "X-wa-level-filter";
    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private SiftingAppender siftingAppender;
    private ch.qos.logback.classic.Logger rootLogger;
    private boolean globalUseConverters = true;





    private LogCollector() {
        rootLogger = loggerContext.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        siftingAppender = new FilterableSiftingAppender();
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
        return (CustomListAppender) ((FilterableSiftingAppender) rootLogger.getAppender(SIFTING_APPENDER_KEY)).getChildAppender();
    }


    public void addFilters(Map<String, List<String>> headers) {
        List<String> useConverters = headers.get(X_VERBOSE_LOGS);
        checkUseConverters(useConverters);
        Optional<ThresholdFilter> thresholdFilter = checkThresholdFilter(headers.get(X_THRESHOLD_FILTER));
        if (thresholdFilter.isPresent()) {
            getChildAppender().addFilter(thresholdFilter.get());
        }
        Collection<LevelFilter> levelFilters = LevelFilterBuilder.checkLevelFilter(headers.get(X_LEVEL_FILTER));
        for (LevelFilter filter : levelFilters) {
            getChildAppender().addFilter(filter);
        }
    }

    private void checkUseConverters(List<String> headers) {

        //by default, useCollectors is true. init parameter can override it, and request too
        //request is stronger than init-param, than default configuration
        boolean useConvertersHeader = true;
        if (headers!=null&&!headers.isEmpty()) {
            useConvertersHeader = Boolean.parseBoolean(headers.get(0));
        }
        if (!useConvertersHeader || (!globalUseConverters)) {
            getChildAppender().setUseConverters(false);
        }
    }

    private Optional<ThresholdFilter> checkThresholdFilter(List<String> headers) {
        Optional<ThresholdFilter> optional = Optional.absent();

        if (headers!=null&&!headers.isEmpty()) {
            Level threshold = Level.toLevel(headers.get(0));
            ThresholdFilter thresholdFilter = new ThresholdFilter();
            thresholdFilter.setLevel(threshold.toString());
            thresholdFilter.start();
            optional = Optional.of(thresholdFilter);
        }
        return optional;
    }




    public void setVerboseLogs(String initParameter) {
        if (initParameter != null && "false".equalsIgnoreCase(initParameter)) {
            globalUseConverters = false;
        }
    }

}
