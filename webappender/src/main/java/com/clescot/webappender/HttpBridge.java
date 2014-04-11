package com.clescot.webappender;

import java.util.List;
import java.util.Map;

public interface HttpBridge {
    void addHeader(String key,String value);
    Map<String, List<String>> getHeadersAsMap();
    void appendToBody(String value);

}
