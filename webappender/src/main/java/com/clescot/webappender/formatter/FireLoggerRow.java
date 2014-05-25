package com.clescot.webappender.formatter;

import java.util.List;

 class FireLoggerRow {

    private final String name;
    private final String contextName;
    private final String pathName;
    private final List<Object> args;
    private final String callerData;
    private final String classOfCaller;
    private final String methodOfCaller;
    private final String lineno;
    private final String marker;
    private final String MDC;
    private final String relativeTime;
    private final String time;
    private final String template;
    private final String message;
    private final String threadName;
    private final String throwableProxy;
    private final String level;
    private final long timestamp;


    public FireLoggerRow(Row row) {
        this.contextName = row.getContextName();
        this.name = row.getName();
        this.pathName = row.getPathName();
        this.args = row.getArgs();
        this.callerData = row.getCallerData();
        this.classOfCaller = row.getClassOfCaller();
        this.methodOfCaller = row.getMethodOfCaller();
        this.lineno = row.getLineNumber();
        this.marker = row.getMarker();
        this.MDC = row.getMDC();
        this.relativeTime = row.getRelativeTime();
        this.time = row.getTime();
        this.template = row.getTemplate();
        this.message = row.getMessage();
        this.threadName = row.getThreadName();
        this.throwableProxy = row.getThrowableProxy();
        this.level = Level.getLevel(row.getLevel());
        this.timestamp = row.getTimestamp();
    }

    public String getName() {
        return name;
    }

    public String getContextName() {
        return contextName;
    }

    public String getPathName() {
        return pathName;
    }

    public List<Object> getArgs() {
        return args;
    }

    public String getCallerData() {
        return callerData;
    }

    public String getClassOfCaller() {
        return classOfCaller;
    }

    public String getMethodOfCaller() {
        return methodOfCaller;
    }

    public String getLineno() {
        return lineno;
    }

    public String getMarker() {
        return marker;
    }

    public String getMDC() {
        return MDC;
    }

    public String getRelativeTime() {
        return relativeTime;
    }

    public String getTime() {
        return time;
    }

    public String getTemplate() {
        return template;
    }

    public String getMessage() {
        return message;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getThrowableProxy() {
        return throwableProxy;
    }

    public String getLevel() {
        return level;
    }

    public long getTimestamp() {
        return timestamp;
    }


    private enum Level {

        DEBUG("debug", ch.qos.logback.classic.Level.DEBUG),
        WARNING("warning", ch.qos.logback.classic.Level.WARN),
        ERROR("error", ch.qos.logback.classic.Level.ERROR),
        INFO("info", ch.qos.logback.classic.Level.INFO);
//        CRITICAL("critical", ???? not mapped);

        private final String fireLoggerLevel;
        private final ch.qos.logback.classic.Level logbackLevel;

        Level(String fireLoggerLevel, ch.qos.logback.classic.Level logbackLevel) {
            this.fireLoggerLevel = fireLoggerLevel;
            this.logbackLevel = logbackLevel;
        }

        public String getFireLoggerLevel() {
            return fireLoggerLevel;
        }

        public ch.qos.logback.classic.Level getLogbackLevel() {
            return logbackLevel;
        }

        public static String getLevel(ch.qos.logback.classic.Level logbackLevel) {
            for (Level level : values()) {
                if (level.getLogbackLevel().equals(logbackLevel)) {
                    return level.getFireLoggerLevel();
                }
            }
            return Level.INFO.getFireLoggerLevel();
        }
    }

}
