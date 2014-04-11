package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class BodyFormatter extends AbstractFormatter<Row> {

    public static final String REQUEST_HEADER_IDENTIFIER = "X-BodyLogger";
    public static final String RESPONSE_BODY_LOGGER_HEADER = "Bodylogger-";

    @Override
    protected String getJSON(List<Row> rows) {
        try {
            return objectMapper.writeValueAsString(getFormatterRows(rows));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getRequestHeaderIdentifier() {
        return REQUEST_HEADER_IDENTIFIER;
    }

    @Override
    protected Row newFormatterRow(Row row) {
        return row;
    }



    @Override
    public Map<String, String> serializeRows(List<Row> rows) throws JsonProcessingException {
        Map<String,String> rowsSerialized = Maps.newHashMap();
        rowsSerialized.put(RESPONSE_BODY_LOGGER_HEADER, format(rows));
        return rowsSerialized;
    }

    @Override
    public String format(List<Row> rows) throws JsonProcessingException {
        return getJSON(rows);
    }

    @Override
    public Location getLocation() {
        return Location.BODY;
    }
}
