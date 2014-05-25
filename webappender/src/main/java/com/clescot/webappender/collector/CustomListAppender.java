package com.clescot.webappender.collector;

import ch.qos.logback.classic.pattern.*;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.clescot.webappender.formatter.Row;
import com.google.common.collect.Lists;

import java.util.List;

 public class CustomListAppender extends ListAppender<ILoggingEvent> {

    private final LineOfCallerConverter lineOfCallerConverter = new LineOfCallerConverter();
    private final FileOfCallerConverter fileOfCallerConverter = new FileOfCallerConverter();
    private final DateConverter dateConverter = new DateConverter();
    private final RelativeTimeConverter relativeTimeConverter = new RelativeTimeConverter();
    private final ThreadConverter threadConverter = new ThreadConverter();
    private final ClassOfCallerConverter classOfCallerConverter = new ClassOfCallerConverter();
    private final MethodOfCallerConverter methodOfCallerConverter = new MethodOfCallerConverter();
    private final MDCConverter mdcConverter = new MDCConverter();
    private final ThrowableProxyConverter throwableProxyConverter = new ThrowableProxyConverter();
    private final ContextNameConverter contextNameConverter = new ContextNameConverter();
    private final CallerDataConverter callerDataConverter = new CallerDataConverter();
    private final MarkerConverter markerConverter = new MarkerConverter();

    private final List<Row> rows = Lists.newArrayList();

    private boolean useConverters = true;

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
        if (useConverters) {
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
        }
        rows.add(row);

    }
    public void setUseConverters(boolean useConverters) {
        this.useConverters = useConverters;
    }

    public List<Row> getRows() {
        return Lists.newArrayList(rows);
    }
}
