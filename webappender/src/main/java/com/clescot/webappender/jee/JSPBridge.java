package com.clescot.webappender.jee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class JSPBridge extends JEEHttpBridge {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebAppenderTag.class);
    private final PageContext pageContext;
    private static final String SCRIPT_TYPE_TEXT_JAVASCRIPT_START = "<script type=\"text/javascript\">";
    private static final String SCRIPT_END = "</script>";
    private final StringBuilder json;

    public JSPBridge(PageContext pageContext) {
        super((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
        this.pageContext = pageContext;
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
        json.append(key);
    }
}
