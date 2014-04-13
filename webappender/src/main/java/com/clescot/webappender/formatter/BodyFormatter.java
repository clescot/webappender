package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class BodyFormatter extends AbstractFormatter<Row> {

    public static final String REQUEST_HEADER_IDENTIFIER = "X-BodyLogger";
    public static final String RESPONSE_BODY_LOGGER_HEADER = "Bodylogger-";
    private static final String SCRIPT_TYPE_TEXT_JAVASCRIPT_START = "<script type=\"text/javascript\">";
    private static final String SCRIPT_END = "</script>";

    @Override
    public String getJSON(List<Row> rows) {
        List<Row> formattedRows = getFormatterRows(rows);
        StringBuilder result = new StringBuilder();
        try {
            result.append(SCRIPT_TYPE_TEXT_JAVASCRIPT_START);
            for (Row row : formattedRows) {
                String rowInJSON = objectMapper.writeValueAsString(row);
                result.append("console.dir(");
                result.append(rowInJSON);
                result.append(");");
            }
            result.append(SCRIPT_END);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result.toString();
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
        rowsSerialized.put(RESPONSE_BODY_LOGGER_HEADER, encodeBase64(getJSON(rows)));
        return rowsSerialized;
    }

    @Override
    public Location getLocation() {
        return Location.BODY;
    }
}
