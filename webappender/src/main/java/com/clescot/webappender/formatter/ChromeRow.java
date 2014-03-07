package com.clescot.webappender.formatter;

import ch.qos.logback.classic.Level;
import com.clescot.webappender.Row;
import com.google.common.collect.Maps;

import java.util.Map;

class ChromeRow {
    private Map<String, String> logData = Maps.newHashMap();// an array of all arguments converted to the proper data structure
    private String backtraceData;//he filename and line number where this call originated from
    private String logType;//log, warn, error, info, group, groupEnd, groupCollapsed, and table

    ChromeRow(Row event) {
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

