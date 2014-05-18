package com.clescot.webappender;

import java.util.List;
import java.util.Map;

public interface HttpBridge {

    void start();
    void finish();
    void serializeLogs(String key, String value);
    Map<String, List<String>> getHeadersAsMap();
}
