package com.clescot.webappender.jee;

import com.clescot.webappender.Row;
import com.clescot.webappender.collector.LogCollector;
import com.clescot.webappender.formatter.FireLoggerFormatter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@WebFilter(urlPatterns = "/*")
public class WebAppenderFilter implements Filter {
    public static final String SYSTEM_PROPERTY_KEY = "webappender";
    private LogCollector logCollector;


    private boolean active;
    private FireLoggerFormatter fireLoggerFormatter;
    private boolean globalUseConverters = true;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY_KEY))) {
            setActive(true);
            String initParameter = filterConfig.getInitParameter("X-verbose-logs");
            if(initParameter!=null && "false".equalsIgnoreCase(initParameter)){
                globalUseConverters = false;
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        if(active){
            Enumeration<String> headers = httpServletRequest.getHeaders("X-Verbose-Logs");
            //by default, useCollectors is true. init parameter can override it, and request too
            //request is stronger than init-param, than default configuration
            boolean useConvertersHeader = true;
            if(headers.hasMoreElements()){
                useConvertersHeader = Boolean.parseBoolean(headers.nextElement());
            }
            if(!useConvertersHeader||(useConvertersHeader&&!globalUseConverters)){
                logCollector.getChildAppender().setUseConverters(false);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);

        if (active) {
            List<Row> logs = logCollector.getLogs();
            logCollector.removeCurrentThreadAppender();

            if (httpServletRequest.getHeader("X-FireLogger") != null) {
                for (Map.Entry<String, String> entry : fireLoggerFormatter.getHeadersAsMap(logs).entrySet()) {
                    httpServletResponse.addHeader(entry.getKey(), entry.getValue());
                }
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
//        chromeLoggerFormatter = new ChromeLoggerFormatter();
        fireLoggerFormatter = new FireLoggerFormatter();
    }

}
