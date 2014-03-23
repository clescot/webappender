package com.clescot.webappender.jee;

import com.clescot.webappender.Row;
import com.clescot.webappender.collector.LogCollector;
import com.clescot.webappender.formatter.Formatter;
import com.clescot.webappender.formatter.Formatters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebFilter(filterName="webAppender",urlPatterns = "/*",description = "output your logback logs in your favorite browser")
public class WebAppenderFilter implements Filter {
    public static final String SYSTEM_PROPERTY_KEY = "webappender";
    public static final String X_THRESHOLD_FILTER = "X-wa-threshold-filter";
    public static final String X_LEVEL_FILTER = "X-wa-level-filter";

    private static Logger LOGGER = LoggerFactory.getLogger(WebAppenderFilter.class);

    private LogCollector logCollector;
    private boolean active;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY_KEY))) {
            setActive(true);
            String initParameter = filterConfig.getInitParameter(LogCollector.X_VERBOSE_LOGS);
            logCollector.setVerboseLogs(initParameter);
        }
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        Map<String, List<String>> headers = getHeadersAsMap(httpServletRequest);

        if (active) {
            logCollector.addFilters(headers);
        }
        filterChain.doFilter(servletRequest, servletResponse);

        if (active) {
            List<Row> logs = logCollector.getLogs();
            logCollector.removeCurrentThreadAppender();

            Optional<? extends Formatter> optional = Formatters.findFormatter(headers);
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
