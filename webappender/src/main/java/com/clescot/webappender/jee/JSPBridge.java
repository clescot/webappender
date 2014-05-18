package com.clescot.webappender.jee;

import com.clescot.webappender.collector.LogCollector;
import com.clescot.webappender.formatter.Formatter;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class JSPBridge extends JEEHttpBridge {

    private static Logger LOGGER = LoggerFactory.getLogger(WebAppenderTag.class);
    private PageContext pageContext;
    private Optional<LogCollector> optionalLogCollector;
    private Optional<Formatter> optionalFormatter;
    private static final String SCRIPT_TYPE_TEXT_JAVASCRIPT_START = "<script type=\"text/javascript\">";
    private static final String SCRIPT_END = "</script>";
    private StringBuilder json;

    public JSPBridge(PageContext pageContext) {
        super((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
        this.pageContext = pageContext;
        optionalLogCollector = Optional.fromNullable((LogCollector) pageContext.getServletContext().getAttribute(WebAppenderFilter.WEBAPPENDER_LOGCOLLECTOR_SERVLET_CONTEXT_KEY));
        optionalFormatter = Optional.fromNullable((Formatter) pageContext.getRequest().getAttribute(WebAppenderFilter.WEBAPPENDER_FORMATTER_REQUEST_ATTRIBUTE_KEY));
        json = new StringBuilder();
    }

    @Override
    public void start() {
        json.append(SCRIPT_TYPE_TEXT_JAVASCRIPT_START);
    }

    @Override
    public void finish() {
        json.append(SCRIPT_END);
        JspWriter out = pageContext.getOut();

        try {
            out.println(json);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void serializeLogs(String key, String value) {
        LogCollector collector = optionalLogCollector.get();
        json.append(collector.serializeLogs(this, optionalFormatter.get()));
    }
}
