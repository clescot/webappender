package com.clescot.webappender.filter;


import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.filter.EvaluatorFilter;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class JaninoEventEvaluatorBuilderTest {

    public static class Test_handleCustomValue {
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
            JaninoEventEvaluatorBuilder evaluatorBuilder = new JaninoEventEvaluatorBuilder();
            HashMap<String, List<String>> headers = Maps.newHashMap();
            ArrayList<String> values = Lists.newArrayList();
            values.add("expression:return message.contains(\"billing\")");
            headers.put(JaninoEventEvaluatorBuilder.X_JANINO_FILTER.toLowerCase(), values);
            List<AbstractMatcherFilter<ILoggingEvent>> filters = evaluatorBuilder.buildFilters(Optional.<java.util.Map<String, List<String>>>of(headers));
            assertThat(filters).hasSize(1);
            AbstractMatcherFilter<ILoggingEvent> filter = filters.get(0);
            assertThat(filter).isInstanceOf(EvaluatorFilter.class);
            EvaluatorFilter evaluatorFilter = (EvaluatorFilter)filter;
            assertThat(evaluatorFilter.getEvaluator()).isInstanceOf(JaninoEventEvaluator.class);
            JaninoEventEvaluator evaluator = (JaninoEventEvaluator)evaluatorFilter.getEvaluator();
            assertThat(evaluator.getExpression()).isEqualTo("return message.contains(\"billing\")");
        }

    }


}
