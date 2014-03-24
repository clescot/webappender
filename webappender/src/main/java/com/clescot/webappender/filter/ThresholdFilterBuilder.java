package com.clescot.webappender.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.ThresholdFilter;
import com.google.common.base.Optional;

import java.util.List;

public class ThresholdFilterBuilder {

    public static Optional<ThresholdFilter> checkThresholdFilter(List<String> headers) {
        Optional<ThresholdFilter> optional = Optional.absent();

        if (headers!=null&&!headers.isEmpty()) {
            Level threshold = Level.toLevel(headers.get(0));
            ThresholdFilter thresholdFilter = new ThresholdFilter();
            thresholdFilter.setLevel(threshold.toString());
            thresholdFilter.start();
            optional = Optional.of(thresholdFilter);
        }
        return optional;
    }
}
