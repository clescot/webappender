package com.clescot.webappender.jee;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.clescot.webappender.Row;
import com.clescot.webappender.collector.LogCollector;
import com.clescot.webappender.formatter.Formatter;
import com.clescot.webappender.formatter.Formatters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebFilter(urlPatterns = "/*")
public class WebAppenderFilter implements Filter {
    public static final String SYSTEM_PROPERTY_KEY = "webappender";
    public static final String X_VERBOSE_LOGS = "X-verbose-logs";
    public static final String X_THRESHOLD_FILTER = "X-threshold-filter";
    public static final String X_LEVEL_FILTER = "X-level-filter";
    public static final String LEVEL_FILTER_SEPARATOR = ",";
    public static final String LEVEL_FILTER_PROPERTY_SEPARATOR = ";";
    public static final String FITLER_LEVEL_MATCH_PROPERTY = "MATCH";
    public static final String FILTER_LEVEL_MISMATCH_PROPERTY = "MISMATCH";
    public static final String FILTER_LEVEL_LEVEL_PROPERTY = "LEVEL";
    public static final String KEY_VALUE_SEPARATOR = ":";
    private static Logger LOGGER = LoggerFactory.getLogger(WebAppenderFilter.class);

    private LogCollector logCollector;
    private boolean active;
    private boolean globalUseConverters = true;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY_KEY))) {
            setActive(true);
            String initParameter = filterConfig.getInitParameter(X_VERBOSE_LOGS);
            if (initParameter != null && "false".equalsIgnoreCase(initParameter)) {
                globalUseConverters = false;
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        if (active) {
            checkUseConverters(httpServletRequest);
            checkUseFilters(httpServletRequest);

        }
        filterChain.doFilter(servletRequest, servletResponse);

        if (active) {
            List<Row> logs = logCollector.getLogs();
            logCollector.removeCurrentThreadAppender();
            Optional<? extends Formatter> optional = Formatters.findFormatter(getHeadersAsMap(httpServletRequest));
            if (optional.isPresent()) {
                try {
                    Map<String, String> serializedRows = optional.get().serializeRows(logs);
                    for (Map.Entry<String, String> entry : serializedRows.entrySet()) {
                        httpServletResponse.addHeader(entry.getKey(), entry.getValue());
                    }
                } catch (JsonProcessingException e) {
                    LOGGER.warn("webAppender serialization error", e);
                }
            }

        }
    }

    private void checkUseFilters(HttpServletRequest httpServletRequest) {
        Optional<ThresholdFilter> thresholdFilter = checkThresholdFilter(httpServletRequest);
        if (thresholdFilter.isPresent()) {
            logCollector.getChildAppender().addFilter(thresholdFilter.get());
        }
        Collection<LevelFilter> levelFilters = checkLevelFilter(httpServletRequest);
        for (LevelFilter filter : levelFilters) {
            logCollector.getChildAppender().addFilter(filter);
        }

    }

    private Optional<ThresholdFilter> checkThresholdFilter(HttpServletRequest httpServletRequest) {
        Optional<ThresholdFilter> optional = Optional.absent();
        Enumeration<String> headers = httpServletRequest.getHeaders(X_THRESHOLD_FILTER);
        if (headers.hasMoreElements()) {
            Level threshold = Level.toLevel(headers.nextElement());
            ThresholdFilter thresholdFilter = new ThresholdFilter();
            thresholdFilter.setLevel(threshold.toString());
            thresholdFilter.start();
            optional = Optional.of(thresholdFilter);
        }
        return optional;
    }

    private Collection<LevelFilter> checkLevelFilter(HttpServletRequest httpServletRequest) {
        List<LevelFilter> levelFilters = Lists.newArrayList();
        Enumeration<String> headers = httpServletRequest.getHeaders(X_LEVEL_FILTER);
        while (headers.hasMoreElements()) {
            String valueElement = headers.nextElement();
            List<String> values = Arrays.asList(valueElement.split(LEVEL_FILTER_SEPARATOR));
            for (String value : values) {
                LevelFilter levelFilter = getLevelFilter(value);
                levelFilter.start();
                levelFilters.add(levelFilter);
            }

        }
        return levelFilters;
    }

    private LevelFilter getLevelFilter(String valueElement) {
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

    private String getFirstValueTrimmed(String value) {
        List<String> matchValues = Arrays.asList(value.split(KEY_VALUE_SEPARATOR));
        return matchValues.get(1).trim();
    }

    private void checkUseConverters(HttpServletRequest httpServletRequest) {
        Enumeration<String> headers = httpServletRequest.getHeaders(X_VERBOSE_LOGS);
        //by default, useCollectors is true. init parameter can override it, and request too
        //request is stronger than init-param, than default configuration
        boolean useConvertersHeader = true;
        if (headers.hasMoreElements()) {
            useConvertersHeader = Boolean.parseBoolean(headers.nextElement());
        }
        if (!useConvertersHeader || (!globalUseConverters)) {
            logCollector.getChildAppender().setUseConverters(false);
        }
    }

    private static Map<String, List<String>> getHeadersAsMap(HttpServletRequest httpServletRequest) {

        Map<String, List<String>> map = Maps.newHashMap();

        Enumeration headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            ArrayList<String> value = Collections.list(httpServletRequest.getHeaders(key));
            map.put(key, value);
        }
        return map;
    }

    @Override
    public void destroy() {
        logCollector.shutdown();
    }

    public void setActive(boolean active) {
        this.active = active;
        logCollector = LogCollector.newLogCollector();
    }

}
