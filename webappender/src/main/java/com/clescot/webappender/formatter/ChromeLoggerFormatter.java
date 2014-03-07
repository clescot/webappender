package com.clescot.webappender.formatter;

import com.clescot.webappender.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChromeLoggerFormatter extends AbstractFormatter<ChromeRow> {

    public final static String RESPONSE_CHROME_LOGGER_HEADER = "X-ChromeLogger-Data";
    public static final String REQUEST_HEADER_IDENTIFIER = "";

    protected String getJSON(List<Row> rows) {
        Map<String, Object> globalStructure = Maps.newHashMap();
        globalStructure.put("version", "1.0");
        globalStructure.put("columns", Arrays.asList("log", "backtrace", "type"));
        globalStructure.put("rows", getFormatterRows(rows));
        try {
            return objectMapper.writeValueAsString(globalStructure);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected ChromeRow newFormatterRow(Row row) {
        return new ChromeRow(row);
    }

    @Override
    public String getRequestHeaderId() {
        return REQUEST_HEADER_IDENTIFIER;
    }

    @Override
    public Map<String, String> serializeRows(List<com.clescot.webappender.Row> rows) {
        Map<String,String> rowsSerialized = Maps.newHashMap();
        rowsSerialized.put(RESPONSE_CHROME_LOGGER_HEADER, getJSON(rows));
        return rowsSerialized;
    }




}
