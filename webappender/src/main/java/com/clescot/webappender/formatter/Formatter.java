package com.clescot.webappender.formatter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.clescot.webappender.Row;

import java.util.List;
import java.util.Map;

public interface Formatter {

    String getRequestHeaderId();

    Map<String, String> getHeadersAsMap(List<Row> rows);
}
