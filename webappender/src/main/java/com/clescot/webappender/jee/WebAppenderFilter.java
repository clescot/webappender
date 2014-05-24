package com.clescot.webappender.jee;

import com.clescot.webappender.HttpBridge;
import com.clescot.webappender.collector.LogCollector;
import com.google.inject.Injector;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebFilter(filterName = "webAppender", urlPatterns = "/*", description = "output your logback logs in your favorite browser")
public class WebAppenderFilter implements Filter {
    public static final String SYSTEM_PROPERTY_KEY = "webappender";
    public static final String WEBAPPENDER_LOGCOLLECTOR_SERVLET_CONTEXT_KEY = "webappender.logcollector";
    public static final String WEBAPPENDER_FORMATTER_REQUEST_ATTRIBUTE_KEY = "webappender.formatter";


    private LogCollector logCollector;
    private boolean active;



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY_KEY))) {
            setActive(true);
            Injector injector = (Injector) filterConfig.getServletContext().getAttribute(Injector.class.getName());
            logCollector = injector.getInstance(LogCollector.class);
            String initParameter = filterConfig.getInitParameter(LogCollector.X_VERBOSE_LOGS);
            logCollector.setVerboseLogs(initParameter);
            filterConfig.getServletContext().setAttribute(WEBAPPENDER_LOGCOLLECTOR_SERVLET_CONTEXT_KEY, logCollector);
        }
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        HttpBridge httpBridge = new JEEHttpBridge(httpServletRequest, httpServletResponse);
        Map<String, List<String>> headers = httpBridge.getHeadersAsMap();

        if (active) {
            logCollector.getOrCreateChildAppender();
            logCollector.checkUseConverters(headers);
            logCollector.addFiltersToChildAppender(headers);
        }
        filterChain.doFilter(servletRequest, servletResponse);

        if (active) {
                logCollector.serializeLogs(httpBridge,true);
        }
    }


    @Override
    public void destroy() {
        if (active) {
            logCollector.shutdown();
        }
    }

    private void setActive(boolean active) {
        this.active = active;

    }

}
