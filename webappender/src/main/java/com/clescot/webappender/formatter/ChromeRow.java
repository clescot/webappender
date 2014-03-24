package com.clescot.webappender.formatter;

import ch.qos.logback.classic.Level;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ChromeRow {

    private List<Map<String,String>> logData = Lists.newArrayList();// an array of all arguments
    private String backtraceData;//he filename and line number where this call originated from
    private String logType;//log, warn, error, info, group, groupEnd, groupCollapsed, and table

    ChromeRow(Row event) {
        HashMap<String,String> internal = Maps.newHashMap();
        internal.put("___class_name", event.getName());
        internal.put("message", event.getMessage());
        logData.add(internal);
        this.backtraceData = event.getPathName() + ':' + event.getLineNumber();
        this.logType = LogType.getChromeLoggerLevel(event.getLevel()).getChromeLoggerLevel();
    }


    public List<Map<String,String>> getLogData() {
        return logData;
    }

    public String getBacktraceData() {
        return backtraceData;
    }

    public String getLogType() {
        return logType;
    }

    public enum LogType {

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

