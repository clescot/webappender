package com.clescot.webappender.filter;

import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.filter.Filter;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;


@RunWith(Enclosed.class)
public class FiltersTest {

    @Ignore
    public static class AbstractTest{
        protected Filters filtersBuilder;

        @Before
        public void setUp(){
            Injector injector = Guice.createInjector(new FiltersModule());
            filtersBuilder = injector.getInstance(Filters.class);
        }
    }

    public static class EmptyTest extends AbstractTest{
    @Test
    public void testGetFilters_without_any_headers() throws Exception {
        //when
        Collection<? extends Filter<ILoggingEvent>> filters = filtersBuilder.buildFilters(
                Maps.<String, List<String>>newHashMap());

        //then
        assertThat(filters).isEmpty();
    }
    }

    public static class JaninoTest extends AbstractTest{
        @Test
        public void testGetFilters_with_janinoEventEvaluator_header() throws Exception {
            //given
            HashMap<String, List<String>> headers = Maps.newHashMap();
            headers.put(JaninoEventEvaluatorBuilder.X_JANINO_FILTER, Arrays.asList("expression:return message.contains(\"199\""));

            //when
            Collection<? extends Filter<ILoggingEvent>> filters = filtersBuilder.buildFilters(headers);

            //then
            assertThat(filters).hasSize(1);
            Filter<ILoggingEvent> filter = filters.iterator().next();
            assertThat(filter).isInstanceOf(EvaluatorFilter.class);
            EvaluatorFilter evaluatorFilter = (EvaluatorFilter)filter;
            assertThat(evaluatorFilter.getEvaluator()).isInstanceOf(JaninoEventEvaluator.class);
        }

        @Test
        public void testGetFilters_with_janinoEventEvaluator_header_lower_case() throws Exception {
            //given
            HashMap<String, List<String>> headers = Maps.newHashMap();
            headers.put(JaninoEventEvaluatorBuilder.X_JANINO_FILTER.toLowerCase(), Arrays.asList("expression:return message.contains(\"199\""));

            //when
            Collection<? extends Filter<ILoggingEvent>> filters = filtersBuilder.buildFilters(headers);

            //then
            assertThat(filters).hasSize(1);
            Filter<ILoggingEvent> filter = filters.iterator().next();
            assertThat(filter).isInstanceOf(EvaluatorFilter.class);
            EvaluatorFilter evaluatorFilter = (EvaluatorFilter)filter;
            assertThat(evaluatorFilter.getEvaluator()).isInstanceOf(JaninoEventEvaluator.class);
        }
    }

    public static class LevelFilterTest extends AbstractTest{
        @Test
        public void testGetFilters_with_level_filter_header() throws Exception {
            //given
            HashMap<String, List<String>> headers = Maps.newHashMap();
            headers.put(LevelFilterBuilder.X_LEVEL_FILTER, Arrays.asList("\"MATCH:ACCEPT;MISMATCH:NEUTRAL;LEVEL:INFO\""));

            //when
            Collection<? extends Filter<ILoggingEvent>> filters = filtersBuilder.buildFilters(headers);

            //then
            assertThat(filters).hasSize(1);
            Filter<ILoggingEvent> filter = filters.iterator().next();
            assertThat(filter).isInstanceOf(LevelFilter.class);
        }
    }
    public static class ThresHoldTest extends AbstractTest{
        @Test
        public void testGetFilters_with_janinoEventEvaluator_header() throws Exception {
            //given
            HashMap<String, List<String>> headers = Maps.newHashMap();
            headers.put(ThresholdFilterBuilder.X_THRESHOLD_FILTER, Arrays.asList("WARN"));

            //when
            Collection<? extends Filter<ILoggingEvent>> filters = filtersBuilder.buildFilters(headers);

            //then
            assertThat(filters).hasSize(1);
            Filter<ILoggingEvent> filter = filters.iterator().next();
            assertThat(filter).isInstanceOf(ThresholdFilter.class);
        }
    }

}
