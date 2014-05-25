package com.clescot.webappender.jee;

import com.clescot.webappender.HttpBridge;
import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class JEEHttpBridge implements HttpBridge {
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    public JEEHttpBridge(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

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
