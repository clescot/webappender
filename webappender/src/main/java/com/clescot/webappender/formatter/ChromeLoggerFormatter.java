package com.clescot.webappender.formatter;

import ch.qos.logback.classic.Level;
import com.clescot.webappender.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChromeLoggerFormatter extends AbstractFormatter {


    protected String getJSON(List<com.clescot.webappender.Row> rows) throws JsonProcessingException {
        Map<String, Object> globalStructure = Maps.newHashMap();
        globalStructure.put("version", "0.1");
        globalStructure.put("columns", Arrays.asList("log", "backtrace", "type"));
        ImmutableList<ChromeRow> chromeLoggerRows = FluentIterable.from(rows).transform(new Function<Row, ChromeRow>() {
            @Override
            public ChromeRow apply(Row input) {
                return new ChromeRow(input);
            }

        }).toList();
        globalStructure.put("rows", chromeLoggerRows);
        return objectMapper.writeValueAsString(globalStructure);
    }

    @Override
    public String getRequestHeaderId() {
        return "NO_REQUEST_HEADER_ID";
    }

    @Override
    public Map<String, String> serializeRows(List<com.clescot.webappender.Row> rows) {
        return null;
    }


    private class ChromeRow {
        private Map<String, String> logData = Maps.newHashMap();// an array of all arguments converted to the proper data structure
        private String backtraceData;//he filename and line number where this call originated from
        private String logType;//log, warn, error, info, group, groupEnd, groupCollapsed, and table

        private ChromeRow(Row event) {
            this.logData.put("___class_name", event.getName());
            this.logData.put("message", event.getMessage());

            this.backtraceData = event.getPathName() + ':' + event.getLineNumber();
            this.logType = LogType.getChromeLoggerLevel(event.getLevel()).getChromeLoggerLevel();
        }


        public Map<String, String> getLogData() {
            return logData;
        }

        public String getBacktraceData() {
            return backtraceData;
        }

        public String getLogType() {
            return logType;
        }
    }

    private enum LogType {

        LOG("log", Level.ALL),
        WARN("warn", Level.WARN),
        ERROR("error", Level.ERROR),
        INFO("info", Level.INFO),
        GROUP("group", Level.DEBUG),
        GROUP_END("groupEnd", Level.DEBUG),
        GROUP_COLLAPSED("groupCollapsed", Level.DEBUG),
        TABLE("table", Level.DEBUG);

        private String chromeLoggerLevel;
        private Level logbackLevel;

        LogType(String chromeLoggerLevel, Level logbackLevel) {
            this.chromeLoggerLevel = chromeLoggerLevel;
            this.logbackLevel = logbackLevel;
        }

        public String getChromeLoggerLevel() {
            return chromeLoggerLevel;
        }

        public Level getLogbackLevel() {
            return logbackLevel;
        }

        public static LogType getChromeLoggerLevel(Level logbackLevel) {
            for (LogType logType : values()) {
                if (logType.getLogbackLevel().equals(logbackLevel)) {
                    return logType;
                }
            }
            return LogType.INFO;
        }
    }
}
