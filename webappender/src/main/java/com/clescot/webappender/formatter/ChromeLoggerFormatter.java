package com.clescot.webappender.formatter;

import com.clescot.webappender.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChromeLoggerFormatter extends AbstractFormatter<ChromeRow> {

    public final static String RESPONSE_CHROME_LOGGER_HEADER = "X-ChromeLogger-Data";
    public static final String HTTP_USER_AGENT = "user-agent";
    private static Pattern chromeUserAgentPattern = Pattern.compile("like Gecko\\) (.)*Chrome/");

    protected String getJSON(List<Row> rows) throws JsonProcessingException {
       StringBuilder json = new StringBuilder("{\"version\": \"1.0\",\"columns\": [\"log\", \"backtrace\", \"type\"],\"rows\": [");
        int i=0;
        for (Row row : rows) {
            if(i>0){
                json.append(",");
            }
            json.append("[");


            //args
            json.append("[");

            json.append("\""+row.getMessage()+"\"");

            json.append("]");
            json.append(",\"");
            if(row.getPathName()!=null){
                json.append(row.getPathName());
            }
            json.append(":");
            if(row.getLineNumber()!=null){
                json.append(row.getLineNumber());
                json.append("\"");
            }
            json.append(",\"");
            if(ChromeRow.LogType.getChromeLoggerLevel(row.getLevel())!=null){
                json.append(ChromeRow.LogType.getChromeLoggerLevel(row.getLevel()));
            }
            json.append("\"");
            json.append("");
            json.append("]");
            i++;
        }

        json.append("]}");
        return json.toString();
    }

    @Override
    protected ChromeRow newFormatterRow(Row row) {
        return new ChromeRow(row);
    }

    @Override
    public boolean isActive(Map <String, List<String>> headers) {
        //active on chrome browsers and derivated one
        List<String> userAgentHeaders = headers.get(HTTP_USER_AGENT);
        if(userAgentHeaders!=null&&!userAgentHeaders.isEmpty()){
            String userAgent = userAgentHeaders.get(0);
            return chromeUserAgentPattern.matcher(userAgent).find();
        }
        return false;
    }

    @Override
    public Map<String, String> serializeRows(List<com.clescot.webappender.Row> rows) throws JsonProcessingException {
        Map<String,String> rowsSerialized = Maps.newHashMap();
        rowsSerialized.put(RESPONSE_CHROME_LOGGER_HEADER, format(rows));
        return rowsSerialized;
    }




}
