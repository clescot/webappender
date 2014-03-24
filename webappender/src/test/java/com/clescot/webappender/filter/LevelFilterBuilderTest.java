package com.clescot.webappender.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
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
public class LevelFilterBuilderTest {

    public static class BuildFilters {
        private static Logger LOGGER = (Logger) LoggerFactory.getLogger(BuildFilters.class);

        @Test
        public void test_with_empty_values() throws Exception {
            //given
            LevelFilterBuilder levelFilterBuilder = new LevelFilterBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            List<String> values = Lists.newArrayList();
            headers.put(LevelFilterBuilder.X_LEVEL_FILTER, values);
            //when
            List<? extends Filter<ILoggingEvent>> filters = levelFilterBuilder.buildFilters(headers);

            //then
            assertThat(filters).isEmpty();

        }


        @Test
        public void test_empty_headers() throws Exception {
            //given
            LevelFilterBuilder levelFilterBuilder = new LevelFilterBuilder();

            //when
            List<? extends Filter<ILoggingEvent>> filters = levelFilterBuilder.buildFilters(Maps.<String, List<String>>newHashMap());

            //then
            assertThat(filters).isEmpty();

        }

        @Test
        public void test_nominal_case() throws Exception {
            //given
            LevelFilterBuilder levelFilterBuilder = new LevelFilterBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            List<String> values = Lists.newArrayList();
            values.add("MATCH:ACCEPT;MISMATCH:NEUTRAL;LEVEL:INFO");
            headers.put(LevelFilterBuilder.X_LEVEL_FILTER, values);
            //when
            List<? extends Filter<ILoggingEvent>> filters = levelFilterBuilder.buildFilters(headers);

            //then
            assertThat(filters).isNotEmpty();
            assertThat(filters).hasSize(1);
            Filter<ILoggingEvent> filter = filters.get(0);
            assertThat(filter).isInstanceOf(LevelFilter.class);
            LevelFilter levelFilter = (LevelFilter) filter;
            assertThat(levelFilter.getOnMatch()).isEqualTo(FilterReply.ACCEPT);
            assertThat(levelFilter.getOnMismatch()).isEqualTo(FilterReply.NEUTRAL);
            ILoggingEvent event = new LoggingEvent("",LOGGER, Level.INFO,"message",null,null);

            assertThat(levelFilter.decide(event)).isEqualTo(FilterReply.ACCEPT);

        }

        @Test
        public void test_deny_case() throws Exception {
            //given
            LevelFilterBuilder levelFilterBuilder = new LevelFilterBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            List<String> values = Lists.newArrayList();
            values.add("MATCH:NEUTRAL;MISMATCH:DENY;LEVEL:INFO");
            headers.put(LevelFilterBuilder.X_LEVEL_FILTER, values);
            //when
            List<? extends Filter<ILoggingEvent>> filters = levelFilterBuilder.buildFilters(headers);

            //then
            assertThat(filters).isNotEmpty();
            assertThat(filters).hasSize(1);
            Filter<ILoggingEvent> filter = filters.get(0);
            assertThat(filter).isInstanceOf(LevelFilter.class);
            LevelFilter levelFilter = (LevelFilter) filter;
            assertThat(levelFilter.getOnMatch()).isEqualTo(FilterReply.NEUTRAL);
            assertThat(levelFilter.getOnMismatch()).isEqualTo(FilterReply.DENY);
            ILoggingEvent event = new LoggingEvent("",LOGGER, Level.DEBUG,"message",null,null);

            assertThat(levelFilter.decide(event)).isEqualTo(FilterReply.DENY);

        }


        @Test
        public void test_with_multiple_level_filters() throws Exception {
            //given
            LevelFilterBuilder levelFilterBuilder = new LevelFilterBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            List<String> values = Lists.newArrayList();
            values.add("MATCH:NEUTRAL;MISMATCH:DENY;LEVEL:INFO,MATCH:ACCEPT;MISMATCH:NEUTRAL;LEVEL:WARN");
            headers.put(LevelFilterBuilder.X_LEVEL_FILTER, values);
            //when
            List<? extends Filter<ILoggingEvent>> filters = levelFilterBuilder.buildFilters(headers);

            //then
            assertThat(filters).isNotEmpty();
            assertThat(filters).hasSize(2);
            Filter<ILoggingEvent> filter = filters.get(0);
            assertThat(filter).isInstanceOf(LevelFilter.class);
            LevelFilter levelFilter = (LevelFilter) filter;
            assertThat(levelFilter.getOnMatch()).isEqualTo(FilterReply.NEUTRAL);
            assertThat(levelFilter.getOnMismatch()).isEqualTo(FilterReply.DENY);
            Filter<ILoggingEvent> filter2 = filters.get(1);
            assertThat(filter2).isInstanceOf(LevelFilter.class);
            LevelFilter levelFilter2 = (LevelFilter) filter2;
            assertThat(levelFilter2.getOnMatch()).isEqualTo(FilterReply.ACCEPT);
            assertThat(levelFilter2.getOnMismatch()).isEqualTo(FilterReply.NEUTRAL);
            ILoggingEvent event = new LoggingEvent("",LOGGER, Level.DEBUG,"message",null,null);

            assertThat(levelFilter.decide(event)).isEqualTo(FilterReply.DENY);

        }
    }


}
