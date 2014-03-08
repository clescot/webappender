package com.clescot.webappender.formatter;

import com.clescot.webappender.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChromeLoggerFormatter extends AbstractFormatter<ChromeRow> {

    public final static String RESPONSE_CHROME_LOGGER_HEADER = "X-ChromeLogger-Data";
    public static final String HTTP_USER_AGENT = "HTTP_USER_AGENT";
    private static Pattern chromeUserAgentPattern = Pattern.compile("like Gecko\\) Chrome/");

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
    public boolean isActive(Map<String, List<String>> headers) {
        //active on chrome browsers and derivated one
        List<String> userAgentHeaders = headers.get(HTTP_USER_AGENT);
        if(userAgentHeaders!=null&&!userAgentHeaders.isEmpty()){
            String userAgent = userAgentHeaders.get(0);
            return chromeUserAgentPattern.matcher(userAgent).find();
        }
        return false;
    }

    @Override
    public Map<String, String> serializeRows(List<com.clescot.webappender.Row> rows) {
        Map<String,String> rowsSerialized = Maps.newHashMap();
        rowsSerialized.put(RESPONSE_CHROME_LOGGER_HEADER, getJSON(rows));
        return rowsSerialized;
    }




}
