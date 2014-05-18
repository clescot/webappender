package com.clescot.webappender.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class JaninoEventEvaluatorBuilderTest {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(JaninoEventEvaluatorBuilderTest.class);

    public static class Test_handleCustomValue {
        private Filters filtersBuilder;

        @Before
        public void prepareTest(){
            Injector myInjector = Guice.createInjector(new FiltersModule());
            filtersBuilder = myInjector.getInstance(Filters.class);
        }

        @Test
        public void test_empty_headers() throws Exception {
            JaninoEventEvaluatorBuilder evaluatorBuilder = new JaninoEventEvaluatorBuilder();
            List<AbstractMatcherFilter<ILoggingEvent>> filters = evaluatorBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>of(Maps.<String, List<String>>newHashMap()));
            assertThat(filters).isEmpty();
        }

        @Test
        public void test_null_headers() throws Exception {
            JaninoEventEvaluatorBuilder evaluatorBuilder = new JaninoEventEvaluatorBuilder();
            List<AbstractMatcherFilter<ILoggingEvent>> filters = evaluatorBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>absent());
            assertThat(filters).isEmpty();
        }

        @Test
        public void test_empty_string() throws Exception {
            JaninoEventEvaluatorBuilder evaluatorBuilder = new JaninoEventEvaluatorBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            headers.put(JaninoEventEvaluatorBuilder.X_JANINO_FILTER, Lists.<String>newArrayList());
            List<AbstractMatcherFilter<ILoggingEvent>> filters = evaluatorBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>of(headers));
            assertThat(filters).isEmpty();
        }

        @Test
        public void test_non_conform_header_value() throws Exception {
            JaninoEventEvaluatorBuilder evaluatorBuilder = new JaninoEventEvaluatorBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            ArrayList<String> values = Lists.newArrayList();
            values.add("return message.contains(\"billing\")");
            headers.put(JaninoEventEvaluatorBuilder.X_JANINO_FILTER, values);
            List<AbstractMatcherFilter<ILoggingEvent>> filters = evaluatorBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>of(headers));
            assertThat(filters).isEmpty();
        }

        @Test
        public void test_nominal_case() throws Exception {

            //given
            JaninoEventEvaluatorBuilder evaluatorBuilder = new JaninoEventEvaluatorBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            ArrayList<String> values = Lists.newArrayList();
            values.add("expression:return message.contains(\"billing\");");
            headers.put(JaninoEventEvaluatorBuilder.X_JANINO_FILTER.toLowerCase(), values);


            //when
            List<AbstractMatcherFilter<ILoggingEvent>> filters = evaluatorBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>of(headers));

            //then
            assertThat(filters).hasSize(1);
            AbstractMatcherFilter<ILoggingEvent> filter = filters.get(0);
            assertThat(filter).isInstanceOf(EvaluatorFilter.class);
            EvaluatorFilter evaluatorFilter = (EvaluatorFilter)filter;
            assertThat(evaluatorFilter.getEvaluator()).isInstanceOf(JaninoEventEvaluator.class);
            JaninoEventEvaluator evaluator = (JaninoEventEvaluator)evaluatorFilter.getEvaluator();
            assertThat(evaluator.getExpression()).isEqualTo("return message.contains(\"billing\");");
            assertThat(evaluator.isStarted()).isTrue();


        }


        @Test
        public void test_filtering_true() throws Exception {

            //given
            JaninoEventEvaluatorBuilder evaluatorBuilder = new JaninoEventEvaluatorBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            ArrayList<String> values = Lists.newArrayList();
            values.add("MATCH:ACCEPT;MISMATCH:DENY;expression:return message.contains(\"billing\");");
            headers.put(JaninoEventEvaluatorBuilder.X_JANINO_FILTER.toLowerCase(), values);
            List<Filter<ILoggingEvent>> filters = Lists.newArrayList(filtersBuilder.buildFilters(headers));
            ILoggingEvent event = new LoggingEvent("com.clescot.webappender.filter",LOGGER, Level.INFO,"message containing billing",null,null);
            final Filter<ILoggingEvent> filter = filters.get(0);
            FilterAttachableImpl filterAttachable = new FilterAttachableImpl();
            filterAttachable.addFilter(filter);

            //when
            final FilterReply filterChainDecision = filterAttachable.getFilterChainDecision(event);

            //then
            assertThat(filterChainDecision).isEqualTo(FilterReply.ACCEPT);

        }


        @Test
               public void test_filtering_false() throws Exception {

                   //given
                   JaninoEventEvaluatorBuilder evaluatorBuilder = new JaninoEventEvaluatorBuilder();
                   HashMap<String, List<String>> headers = Maps.newHashMap();
                   ArrayList<String> values = Lists.newArrayList();
                   values.add("MATCH:ACCEPT;MISMATCH:DENY;expression:return message.contains(\"billing\");");
                   headers.put(JaninoEventEvaluatorBuilder.X_JANINO_FILTER.toLowerCase(), values);
                   List<Filter<ILoggingEvent>> filters = Lists.newArrayList(filtersBuilder.buildFilters(headers));
                   ILoggingEvent event = new LoggingEvent("com.clescot.webappender.filter",LOGGER, Level.INFO,"message containing nothing",null,null);
                   final Filter<ILoggingEvent> filter = filters.get(0);
                   FilterAttachableImpl filterAttachable = new FilterAttachableImpl();
                   filterAttachable.addFilter(filter);

                   //when
                   final FilterReply filterChainDecision = filterAttachable.getFilterChainDecision(event);

                   //then
                   assertThat(filterChainDecision).isEqualTo(FilterReply.DENY);

               }



    }


}
