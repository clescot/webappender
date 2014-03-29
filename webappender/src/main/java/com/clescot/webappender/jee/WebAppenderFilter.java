package com.clescot.webappender.jee;

import com.clescot.webappender.HttpBridge;
import com.clescot.webappender.collector.LogCollector;
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
        HttpBridge httpBridge = new JEEHttpBridge(httpServletRequest, httpServletResponse);
        Map<String, List<String>> headers = httpBridge.getHeadersAsMap();

        if (active) {
            logCollector.checkUseConverters(headers);
            logCollector.addFilters(headers);
        }
        filterChain.doFilter(servletRequest, servletResponse);

        if (active) {
            logCollector.serializeLogs(httpBridge, headers);
        }
    }







    @Override
    public void destroy() {
        logCollector.shutdown();
    }

     void setActive(boolean active) {
        this.active = active;
        logCollector = LogCollector.newLogCollector();
    }

}
