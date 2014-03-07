package com.clescot.webappender.formatter;

import com.clescot.webappender.Row;

import java.util.List;
import java.util.Map;

public interface Formatter {

    String getRequestHeaderId();

    Map<String, String> serializeRows(List<Row> rows);
}
