package com.clescot.webappender.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import com.google.common.base.Optional;

import java.util.List;
import java.util.Map;

public interface FilterBuilder {

     List<? extends Filter<ILoggingEvent>> buildFilters(Optional<Map<String, List<String>>> headers);
}
