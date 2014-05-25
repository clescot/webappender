package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import org.codehaus.jettison.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

public class ChromeLoggerFormatter extends AbstractFormatter<ChromeRow> implements HeaderFormatter{

    public final static String RESPONSE_CHROME_LOGGER_HEADER = "X-ChromeLogger-Data";
    public static final String REQUEST_HEADER_IDENTIFIER = "X-ChromeLogger";

    public String getJSON(List<Row> rows,int limit)  {
       StringBuilder json = new StringBuilder("{\"version\": \"1.0\",\"columns\": [\"log\", \"backtrace\", \"type\"],\"rows\": [");
        int i=0;
        for (Row row : rows) {
            if(json.length()<limit||limit<=0) {
                String str = serializeRow(i, row);
                json.append(str);
                i++;
            }else{
                break;
            }
        }

        json.append("]}");
        return json.toString();
    }

    private String serializeRow(int i, Row row) {
        StringBuilder json = new StringBuilder();
        if(i>0){
            json.append(",");
        }
        json.append("[");


        //args
        json.append("[{");
        json.append("\"___class_name\": ").append(JSONObject.quote(row.getName()));
        json.append(",");
        json.append("\"message\":").append(JSONObject.quote(row.getMessage()));
        json.append(",");
        json.append("\"callerData\":").append(JSONObject.quote(row.getCallerData()));
        json.append(",");
        json.append("\"classOfCaller\":").append(JSONObject.quote(row.getClassOfCaller()));
        json.append(",");
        json.append("\"contextName\":").append(JSONObject.quote(row.getContextName()));
        json.append(",");
        json.append("\"marker\":").append(JSONObject.quote(row.getMarker()));
        json.append(",");
        json.append("\"mdc\":").append(JSONObject.quote(row.getMDC()));
        json.append(",");
        json.append("\"methodOfCaller\":").append(JSONObject.quote(row.getMethodOfCaller()));
        json.append(",");
        json.append("\"relativeTime\":").append(JSONObject.quote(row.getRelativeTime()));
        json.append(",");
        json.append("\"template\":").append(JSONObject.quote(row.getTemplate()));
        json.append(",");
        json.append("\"threadName\":").append(JSONObject.quote(row.getThreadName()));
        json.append(",");
        json.append("\"throwableProxy\":").append(JSONObject.quote(row.getThrowableProxy()));
        json.append(",");
        json.append("\"time\":").append(JSONObject.quote(row.getTime()));
        json.append(",");
        json.append("\"timeStamp\":").append(JSONObject.quote("" + row.getTimestamp()));
        json.append("}]");
        json.append(",");
        StringBuilder pathNameAndLineNumber = new StringBuilder();
        if(row.getPathName()!=null){
            pathNameAndLineNumber.append(row.getPathName());
        }
        pathNameAndLineNumber.append(":");
        if(row.getLineNumber()!=null){
            pathNameAndLineNumber.append(row.getLineNumber());
        }
        json.append(JSONObject.quote(pathNameAndLineNumber.toString()));
        json.append(",");
        if(ChromeRow.LogType.getChromeLoggerLevel(row.getLevel())!=null){
            json.append(JSONObject.quote(ChromeRow.LogType.getChromeLoggerLevel(row.getLevel()).toString()));
        }
        json.append("]");
        return json.toString();
    }

    @Override
    protected String getRequestHeaderIdentifier() {
        return REQUEST_HEADER_IDENTIFIER;
    }

    @Override
    protected ChromeRow newFormatterRow(Row row) {
        return new ChromeRow(row);
    }



    @Override
    public LinkedHashMap<String, String> formatRows(List<Row> rows, int limit) {
        LinkedHashMap<String,String> rowsSerialized = Maps.newLinkedHashMap();
        rowsSerialized.put(RESPONSE_CHROME_LOGGER_HEADER, encodeBase64(getJSON(rows,limit)));
        return rowsSerialized;
    }


}
