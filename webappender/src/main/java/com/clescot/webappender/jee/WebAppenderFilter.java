package com.clescot.webappender.jee;

import com.clescot.webappender.HttpBridge;
import com.clescot.webappender.Row;
import com.clescot.webappender.collector.LogCollector;
import com.clescot.webappender.formatter.Formatter;
import com.clescot.webappender.formatter.Formatters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebFilter(filterName="webAppender",urlPatterns = "/*",description = "output your logback logs in your favorite browser")
public class WebAppenderFilter implements Filter {
    public static final String SYSTEM_PROPERTY_KEY = "webappender";


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
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        JEEHttpBridge httpBridge = new JEEHttpBridge(httpServletRequest, httpServletResponse);
        Map<String, List<String>> headers = httpBridge.getHeadersAsMap();

        if (active) {
            logCollector.addFilters(headers);
        }
        filterChain.doFilter(servletRequest, servletResponse);

        if (active) {
            serializeLogs(httpBridge, headers);
        }
    }

    private void serializeLogs(HttpBridge httpBridge, Map<String, List<String>> headers) {
        List<Row> logs = logCollector.getLogs();
        logCollector.removeCurrentThreadAppender();

        Optional<? extends Formatter> optional = Formatters.findFormatter(headers);
        if (optional.isPresent()) {
            try {
                Map<String, String> serializedRows = optional.get().serializeRows(logs);
                for (Map.Entry<String, String> entry : serializedRows.entrySet()) {
                    httpBridge.addHeader(entry.getKey(), entry.getValue());
                }
            } catch (JsonProcessingException e) {
                LOGGER.warn("webAppender serialization error", e);
            }
        }
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
