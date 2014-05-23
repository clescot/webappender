package com.clescot.webappender.formatter;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class ConsoleFormatterTest {


    public static class GetJson{
        private static Logger LOGGER = LoggerFactory.getLogger(GetJson.class);
        public static final Pattern timestampPattern = Pattern.compile("\"timestamp\":\\d*");
        @Test
        public void testWithEmptyList() throws JsonProcessingException {
            //given
            ConsoleFormatter consoleFormatter = new ConsoleFormatter();

            //when
            LinkedHashMap<String, String> result= consoleFormatter.formatRows(Lists.<Row>newArrayList(),0);

            //then
            assertThat(result).isEmpty();
        }

        @Test
        public void testWithNull() throws JsonProcessingException {
            //given
            ConsoleFormatter consoleFormatter = new ConsoleFormatter();

            //when
            LinkedHashMap<String, String> result= consoleFormatter.formatRows(null,0);

            //then
            assertThat(result).isEmpty();
        }

        @Test
        public void testWithOneEmptyRow() throws JsonProcessingException {
            //given
            ConsoleFormatter consoleFormatter = new ConsoleFormatter();
            ArrayList<Row> rows = Lists.newArrayList();
            ILoggingEvent event = new LoggingEvent();
            Row row = new Row(event);
            rows.add(row);
            //when
            LinkedHashMap<String, String> result = consoleFormatter.formatRows(rows,0);

            //then
            assertThat(result.isEmpty());
        }

        @Test
        public void testWithOneRow() throws JsonProcessingException {
            //given
            ConsoleFormatter consoleFormatter = new ConsoleFormatter();
            ArrayList<Row> rows = Lists.newArrayList();
            ILoggingEvent event = new LoggingEvent(this.getClass().getName(),(ch.qos.logback.classic.Logger)LOGGER, Level.DEBUG,"dummy message",null,null);

            Row row = new Row(event);
            rows.add(row);
            //when
            LinkedHashMap<String, String> result  = consoleFormatter.formatRows(rows,0);
            String json = result.keySet().iterator().next();
            Matcher matcher = timestampPattern.matcher(json);
            String replacedString = matcher.replaceFirst("\"timestamp\":1398600625252");
            //then
            assertThat(replacedString).isEqualTo("console.debug({\"message\":\"dummy message\",\"template\":\"dummy message\",\"args\":[],\"level\":{\"levelInt\":10000,\"levelStr\":\"DEBUG\"},\"timestamp\":1398600625252,\"relativeTime\":null,\"threadName\":null,\"classOfCaller\":null,\"methodOfCaller\":null,\"mdc\":null,\"throwableProxy\":null,\"contextName\":null,\"callerData\":null,\"marker\":null,\"time\":null,\"name\":\"com.clescot.webappender.formatter.ConsoleFormatterTest$GetJson\",\"pathName\":null,\"lineNumber\":null});");
        }
    }
}
