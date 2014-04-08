package com.clescot.webappender.filter;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class ThresholdFilterBuilderTest {

    public static class BuildFilters {

        private static Logger LOGGER = (Logger) LoggerFactory.getLogger(BuildFilters.class);

        @Test
        public void test_with_empty_values() throws Exception {
            //given
            ThresholdFilterBuilder thresholdFilterBuilder = new ThresholdFilterBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            List<String> values = Lists.newArrayList();
            headers.put(ThresholdFilterBuilder.X_THRESHOLD_FILTER, values);
            //when
            List<? extends Filter<ILoggingEvent>> filters = thresholdFilterBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>of(headers));

            //then
            assertThat(filters).isEmpty();
        }


        @Test
        public void test_empty_headers() throws Exception {
            //given
            ThresholdFilterBuilder thresholdFilterBuilder = new ThresholdFilterBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            //when
            List<? extends Filter<ILoggingEvent>> filters = thresholdFilterBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>of(headers));

            //then
            assertThat(filters).isEmpty();

        }

        @Test
        public void test_neutral() throws Exception {
            //given
            ThresholdFilterBuilder thresholdFilterBuilder = new ThresholdFilterBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            List<String> values = Lists.newArrayList();
            values.add("INFO");
            headers.put(ThresholdFilterBuilder.X_THRESHOLD_FILTER.toLowerCase(), values);
            //when
            List<? extends Filter<ILoggingEvent>> filters = thresholdFilterBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>of(headers));

            //then
            assertThat(filters).isNotEmpty();
            assertThat(filters).hasSize(1);
            Filter<ILoggingEvent> filter = filters.get(0);
            assertThat(filter).isInstanceOf(ThresholdFilter.class);
            ThresholdFilter thresholdFilter = (ThresholdFilter) filter;
            ILoggingEvent event = new LoggingEvent("", LOGGER, Level.INFO, "message", null, null);

            assertThat(thresholdFilter.decide(event)).isEqualTo(FilterReply.NEUTRAL);

        }

        @Test
        public void test_deny() throws Exception {
            //given
            ThresholdFilterBuilder thresholdFilterBuilder = new ThresholdFilterBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            List<String> values = Lists.newArrayList();
            values.add("WARN");
            headers.put(ThresholdFilterBuilder.X_THRESHOLD_FILTER.toLowerCase(), values);
            //when
            List<? extends Filter<ILoggingEvent>> filters = thresholdFilterBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>of(headers));

            //then
            assertThat(filters).isNotEmpty();
            assertThat(filters).hasSize(1);
            Filter<ILoggingEvent> filter = filters.get(0);
            assertThat(filter).isInstanceOf(ThresholdFilter.class);
            ThresholdFilter thresholdFilter = (ThresholdFilter) filter;
            ILoggingEvent event = new LoggingEvent("", LOGGER, Level.INFO, "message", null, null);

            assertThat(thresholdFilter.decide(event)).isEqualTo(FilterReply.DENY);

        }
    }
}
