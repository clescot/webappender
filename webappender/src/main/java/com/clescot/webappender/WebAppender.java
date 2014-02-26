package com.clescot.webappender;

import com.clescot.webappender.formatter.FireLoggerFormatter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebFilter(urlPatterns = "/*")
public class WebAppender implements Filter {
    public static final String SYSTEM_PROPERTY_KEY = "webappender";
    private LogGrabber logGrabber;


    private boolean active;
    private FireLoggerFormatter fireLoggerFormatter;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY_KEY))) {
            setActive(true);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);

        if (active) {
            List<Row> logs = logGrabber.getLogs();
            logGrabber.removeCurrentThreadAppender();
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            if (httpServletRequest.getHeader("X-FireLogger") != null) {

                for (Map.Entry<String, String> entry : fireLoggerFormatter.getHeadersAsMap(logs).entrySet()) {
                    httpServletResponse.addHeader(entry.getKey(), entry.getValue());
                }
            }


        }
    }


    @Override
    public void destroy() {
        logGrabber.shutdown();
    }

    public void setActive(boolean active) {
        this.active = active;
        logGrabber = LogGrabber.newLogGrabber();
//        chromeLoggerFormatter = new ChromeLoggerFormatter();
        fireLoggerFormatter = new FireLoggerFormatter();
    }

}
