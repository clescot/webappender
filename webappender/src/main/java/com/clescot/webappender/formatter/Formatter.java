package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface Formatter {

    boolean isActive(Map<String, List<String>> headers);

     String getJSON(List<Row> rows);

    Map<String, String> serializeRows(List<Row> rows) throws JsonProcessingException;

     abstract Location getLocation();

     enum Location{
        HEADER,
        BODY
    }
}
