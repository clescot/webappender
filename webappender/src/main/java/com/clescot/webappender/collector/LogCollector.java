package com.clescot.webappender.collector;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.sift.AppenderTracker;
import com.clescot.webappender.HttpBridge;
import com.clescot.webappender.filter.Filters;
import com.clescot.webappender.formatter.Formatter;
import com.clescot.webappender.formatter.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LogCollector {
    public static final String SIFTING_APPENDER_KEY = "WEB_APPENDER_SIFT";
    public static final String X_VERBOSE_LOGS = "X-wa-verbose-logs";


    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private SiftingAppender siftingAppender;
    private ch.qos.logback.classic.Logger rootLogger;
    private boolean globalUseConverters = true;

    private static Logger LOGGER = LoggerFactory.getLogger(LogCollector.class);
    private Filters filtersBuilder;

    @Inject
    public LogCollector(Filters filtersBuilder) {
        this.filtersBuilder = filtersBuilder;
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
     * called each time a new Thread is created.
     *
     * @return
     */
    public List<Row> getLogs() {
        CustomListAppender perThreadIdAppender = getOrCreateChildAppender();
        return Lists.newArrayList(perThreadIdAppender.getRows());
    }

    public void removeCurrentThreadAppender() {
        getAppenderTracker().endOfLife(Thread.currentThread().getId() + "");
        getAppenderTracker().removeStaleComponents(System.currentTimeMillis());
    }

    private AppenderTracker<ILoggingEvent> getAppenderTracker() {
        return ((SiftingAppender) rootLogger.getAppender(SIFTING_APPENDER_KEY)).getAppenderTracker();
    }


    public CustomListAppender getOrCreateChildAppender() {
        return (CustomListAppender) ((FilterableSiftingAppender) rootLogger.getAppender(SIFTING_APPENDER_KEY)).getChildAppender();
    }


    public void addFiltersToChildAppender(Map<String, List<String>> headers) {

        Collection<? extends Filter<ILoggingEvent>> filters = filtersBuilder.buildFilters(headers);
        for (Filter<ILoggingEvent> filter : filters) {
            getOrCreateChildAppender().addFilter(filter);
        }

    }

    public void checkUseConverters(Map<String, List<String>> headers) {
        List<String> useConverters = headers.get(X_VERBOSE_LOGS);
        checkUseConverters(useConverters);
    }

    private void checkUseConverters(List<String> headers) {

        //by default, useCollectors is true. init parameter can override it, and request too
        //request is stronger than init-param, than default configuration
        boolean useConvertersHeader = true;
        if (headers != null && !headers.isEmpty()) {
            useConvertersHeader = Boolean.parseBoolean(headers.get(0));
        }
        if (!useConvertersHeader || (!globalUseConverters)) {
            getOrCreateChildAppender().setUseConverters(false);
        }
    }


    public LinkedHashMap serializeLogs(HttpBridge httpBridge, Formatter formatter) {
        List<Row> logs = getLogs();
        removeCurrentThreadAppender();
        LinkedHashMap<String, String> serializedRows = null;
        try {
            serializedRows = formatter.formatRows(logs);
            httpBridge.start();
            for (Map.Entry<String, String> entry : serializedRows.entrySet()) {
                String value = entry.getValue();
                httpBridge.serializeLogs(entry.getKey(), value);
            }
            httpBridge.finish();

        } catch (JsonProcessingException e) {
            LOGGER.warn("webAppender serialization error", e);
        }

        return serializedRows;
    }


    public void setVerboseLogs(String initParameter) {
        if (initParameter != null && "false".equalsIgnoreCase(initParameter)) {
            globalUseConverters = false;
        }
    }

}
