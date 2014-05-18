package com.clescot.webappender.jee;

import com.clescot.webappender.HttpBridge;
import com.clescot.webappender.HttpBridgeLimitDecorator;
import com.clescot.webappender.collector.LogCollector;
import com.clescot.webappender.formatter.Formatter;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class WebAppenderTag extends TagSupport {

    private static final long serialVersionUID = 4611181044692549740L;

    private static Logger LOGGER = LoggerFactory.getLogger(WebAppenderTag.class);

    @Override
    public int doStartTag() throws JspException {
        Optional<LogCollector> optionalLogCollector = Optional.fromNullable((LogCollector) pageContext.getServletContext().getAttribute(WebAppenderFilter.WEBAPPENDER_LOGCOLLECTOR_SERVLET_CONTEXT_KEY));
        Optional<Formatter> optionalFormatter = Optional.fromNullable((Formatter) pageContext.getRequest().getAttribute(WebAppenderFilter.WEBAPPENDER_FORMATTER_REQUEST_ATTRIBUTE_KEY));
        if (optionalLogCollector.isPresent() && optionalFormatter.isPresent()) {
            HttpBridge httpBridge = new HttpBridgeLimitDecorator(new JSPBridge(pageContext));
            LogCollector collector = optionalLogCollector.get();
            collector.serializeLogs(httpBridge, optionalFormatter.get());

        }

        return SKIP_BODY;
    }

}
