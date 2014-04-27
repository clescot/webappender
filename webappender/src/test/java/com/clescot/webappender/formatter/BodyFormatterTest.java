package com.clescot.webappender.formatter;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class BodyFormatterTest {


    public static class GetJson{
        private static Logger LOGGER = LoggerFactory.getLogger(GetJson.class);
        public static final Pattern timestampPattern = Pattern.compile("\"timestamp\":\\d*");
        @Test
        public void testWithEmptyList(){
            //given
            BodyFormatter bodyFormatter = new BodyFormatter();

            //when
            String json = bodyFormatter.getJSON(Lists.<Row>newArrayList());

            //then
            assertThat(json).isEmpty();
        }

        @Test
        public void testWithNull(){
            //given
            BodyFormatter bodyFormatter = new BodyFormatter();

            //when
            String json = bodyFormatter.getJSON(null);

            //then
            assertThat(json).isEmpty();
        }

        @Test
        public void testWithOneEmptyRow(){
            //given
            BodyFormatter bodyFormatter = new BodyFormatter();
            ArrayList<Row> rows = Lists.newArrayList();
            ILoggingEvent event = new LoggingEvent();
            Row row = new Row(event);
            rows.add(row);
            //when
            String json = bodyFormatter.getJSON(rows);

            //then
            assertThat(json).isEqualTo("<script type=\"text/javascript\"></script>");
        }

        @Test
        public void testWithOneRow(){
            //given
            BodyFormatter bodyFormatter = new BodyFormatter();
            ArrayList<Row> rows = Lists.newArrayList();
            ILoggingEvent event = new LoggingEvent(this.getClass().getName(),(ch.qos.logback.classic.Logger)LOGGER, Level.DEBUG,"dummy message",null,null);

            Row row = new Row(event);
            rows.add(row);
            //when
            String json = bodyFormatter.getJSON(rows);

            Matcher matcher = timestampPattern.matcher(json);
            String replacedString = matcher.replaceFirst("\"timestamp\":1398600625252");
            //then
            assertThat(replacedString).isEqualTo("<script type=\"text/javascript\">console.debug({\"message\":\"dummy message\",\"template\":\"dummy message\",\"args\":[],\"level\":{\"levelInt\":10000,\"levelStr\":\"DEBUG\"},\"timestamp\":1398600625252,\"relativeTime\":null,\"threadName\":null,\"classOfCaller\":null,\"methodOfCaller\":null,\"mdc\":null,\"throwableProxy\":null,\"contextName\":null,\"callerData\":null,\"marker\":null,\"time\":null,\"name\":\"com.clescot.webappender.formatter.BodyFormatterTest$GetJson\",\"pathName\":null,\"lineNumber\":null});</script>");
        }
    }
}
