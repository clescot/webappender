package com.clescot.webappender.collector;

import ch.qos.logback.classic.pattern.*;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.clescot.webappender.Row;
import com.google.common.collect.Lists;

import java.util.List;

public class CustomListAppender extends ListAppender<ILoggingEvent> {

    private LineOfCallerConverter lineOfCallerConverter = new LineOfCallerConverter();
    private FileOfCallerConverter fileOfCallerConverter = new FileOfCallerConverter();
    private DateConverter dateConverter = new DateConverter();
    private RelativeTimeConverter relativeTimeConverter = new RelativeTimeConverter();
    private ThreadConverter threadConverter = new ThreadConverter();
    private ClassOfCallerConverter classOfCallerConverter = new ClassOfCallerConverter();
    private MethodOfCallerConverter methodOfCallerConverter = new MethodOfCallerConverter();
    private MDCConverter mdcConverter = new MDCConverter();
    private ThrowableProxyConverter throwableProxyConverter = new ThrowableProxyConverter();
    private ContextNameConverter contextNameConverter = new ContextNameConverter();
    private CallerDataConverter callerDataConverter = new CallerDataConverter();
    private MarkerConverter markerConverter = new MarkerConverter();



    private List<Row> rows = Lists.newArrayList();

    @Override
    public void start() {
        super.start();
        dateConverter.start();
        relativeTimeConverter.start();
        threadConverter.start();
        classOfCallerConverter.start();
        methodOfCallerConverter.start();
        mdcConverter.start();
        lineOfCallerConverter.start();
        fileOfCallerConverter.start();
        throwableProxyConverter.start();
        contextNameConverter.start();
        callerDataConverter.start();
        markerConverter.start();
    }

    @Override
    protected void append(ILoggingEvent e) {
        Row row = new Row(e);
        row.setLineNumber(lineOfCallerConverter.convert(e));
        row.setPathName(fileOfCallerConverter.convert(e));
        row.setTime(dateConverter.convert(e));
        row.setRelativeTime(relativeTimeConverter.convert(e));
        row.setThreadName(threadConverter.convert(e));
        row.setClassOfCaller(classOfCallerConverter.convert(e));
        row.setMethodOfCaller(methodOfCallerConverter.convert(e));
        row.setMDC(mdcConverter.convert(e));
        row.setThrowableProxy(throwableProxyConverter.convert(e));
        row.setContextName(contextNameConverter.convert(e));
        row.setCallerData(callerDataConverter.convert(e));
        row.setMarker(markerConverter.convert(e));
        rows.add(row);

    }


    public List<Row> getRows() {
        return Lists.newArrayList(rows);
    }
}
