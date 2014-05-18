package com.clescot.webappender.jee;

import com.clescot.webappender.HttpBridge;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class JEEHttpBridge implements HttpBridge {
    public static final String REQUEST_SCOPE_IDENTIFIER = "wa-logs";
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private static Logger LOGGER = LoggerFactory.getLogger(JEEHttpBridge.class);

    public JEEHttpBridge(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void serializeLogs(String key, String value) {
        httpServletResponse.addHeader(key, value);
    }

    public Map<String, List<String>> getHeadersAsMap() {

        Map<String, List<String>> map = Maps.newHashMap();

        Enumeration headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = ((String) headerNames.nextElement()).toLowerCase();
            ArrayList<String> value = Collections.list(httpServletRequest.getHeaders(key));
            map.put(key, value);
        }
        return map;
    }

}
