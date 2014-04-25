package com.clescot.webappender.formatter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class Row {
    private String message;
    private String template;


    private List<Object> args;
    private Level level;
    private long timestamp;
    private String relativeTime;
    private String threadName;
    private String classOfCaller;
    private String methodOfCaller;
    private String mdc;
    private String throwableProxy;
    private String contextName;
    private String callerData;
    private String marker;


    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    private String time;
    private String name;
    private String pathName;
    private String lineNumber;


    public Row(ILoggingEvent event) {
        if(event==null){
            throw new IllegalArgumentException("event cannot be null");
        }
        this.template = event.getMessage();
        this.args = Lists.newArrayList();
        if (event.getArgumentArray() != null) {
            args.addAll(Arrays.asList(event.getArgumentArray()));
        }

        this.message = event.getFormattedMessage();
        this.level = Objects.firstNonNull(event.getLevel(),Level.ALL);
        this.timestamp = event.getTimeStamp();
        this.name = event.getLoggerName();
    }


    public String getMessage() {
        return message;
    }

    public String getTemplate() {
        return template;
    }

    public List<Object> getArgs() {
        return args;
    }

    public Level getLevel() {
        return level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getPathName() {
        return pathName;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setRelativeTime(String relativeTime) {
        this.relativeTime = relativeTime;
    }

    public String getRelativeTime() {
        return relativeTime;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setClassOfCaller(String classOfCaller) {
        this.classOfCaller = classOfCaller;
    }

    public String getClassOfCaller() {
        return classOfCaller;
    }

    public void setMethodOfCaller(String methodOfCaller) {
        this.methodOfCaller = methodOfCaller;
    }

    public String getMethodOfCaller() {
        return methodOfCaller;
    }

    public void setMDC(String mdc) {
        this.mdc = mdc;
    }

    public String getMDC() {
        return mdc;
    }

    public void setThrowableProxy(String throwableProxy) {
        this.throwableProxy = throwableProxy;
    }

    public String getThrowableProxy() {
        return throwableProxy;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getContextName() {
        return contextName;
    }

    public void setCallerData(String callerData) {
        this.callerData = callerData;
    }

    public String getCallerData() {
        return callerData;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getMarker() {
        return marker;
    }


}


