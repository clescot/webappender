package com.clescot.webappender.jee;

import com.clescot.webappender.Row;
import com.clescot.webappender.collector.LogCollector;
import com.clescot.webappender.formatter.Formatter;
import com.clescot.webappender.formatter.Formatters;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

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
        filterChain.doFilter(servletRequest, servletResponse);

        if (active) {
            List<Row> logs = logCollector.getLogs();
            logCollector.removeCurrentThreadAppender();
            Optional<? extends Formatter> optional = Formatters.findFormatter(getHeadersAsMap(httpServletRequest));
            if (optional.isPresent()) {
                for (Map.Entry<String, String> entry : optional.get().serializeRows(logs).entrySet()) {
                    httpServletResponse.addHeader(entry.getKey(), entry.getValue());
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
