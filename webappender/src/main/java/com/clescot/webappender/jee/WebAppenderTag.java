package com.clescot.webappender.jee;

import com.clescot.webappender.HttpBridge;
import com.clescot.webappender.collector.LogCollector;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class WebAppenderTag extends TagSupport {

    private static final long serialVersionUID = 4611181044692549740L;

    private static Logger LOGGER = LoggerFactory.getLogger(WebAppenderTag.class);

    @Override
    public int doStartTag() throws JspException {
        Optional<LogCollector> optionalLogCollector = Optional.fromNullable((LogCollector) pageContext.getServletContext().getAttribute(WebAppenderFilter.WEBAPPENDER_LOGCOLLECTOR_SERVLET_CONTEXT_KEY));
        if (optionalLogCollector.isPresent()) {
            LogCollector collector = optionalLogCollector.get();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            HttpBridge httpBridge = new JEEHttpBridge(request, response);

            String json = collector.serializeLogs(httpBridge);
            JspWriter out = pageContext.getOut();

            try {
                out.println(json);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return SKIP_BODY;
    }

}
