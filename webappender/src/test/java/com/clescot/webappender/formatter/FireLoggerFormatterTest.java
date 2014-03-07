package com.clescot.webappender.formatter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.clescot.webappender.Row;
import com.google.common.collect.Lists;
import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class FireLoggerFormatterTest {
private static Logger LOGGER = (Logger) LoggerFactory.getLogger(FireLoggerFormatterTest.class);


    public static class Test_Format extends AbstractFormatterTest{

        public static final String EXPECTED = "eyJsb2dzIjpbeyJuYW1lIjoiY29tLmNsZXNjb3Qud2ViYXBwZW5kZXIuZm9ybWF0dGVyLkFic3RyYWN0Rm9ybWF0dGVyVGVzdCIsImNvbnRleHROYW1lIjpudWxsLCJwYXRoTmFtZSI6bnVsbCwiYXJncyI6W10sImNhbGxlckRhdGEiOm51bGwsImNsYXNzT2ZDYWxsZXIiOm51bGwsIm1ldGhvZE9mQ2FsbGVyIjpudWxsLCJsaW5lbm8iOm51bGwsIm1hcmtlciI6bnVsbCwicmVsYXRpdmVUaW1lIjpudWxsLCJ0aW1lIjpudWxsLCJ0ZW1wbGF0ZSI6ImR1bW15IG1lc3NhZ2UiLCJtZXNzYWdlIjoiZHVtbXkgbWVzc2FnZSIsInRocmVhZE5hbWUiOm51bGwsInRocm93YWJsZVByb3h5IjpudWxsLCJsZXZlbCI6ImVycm9yIiwidGltZXN0YW1wIjoxMzk0MjI0NjU2Nzg1LCJtZGMiOm51bGx9LHsibmFtZSI6ImNvbS5jbGVzY290LndlYmFwcGVuZGVyLmZvcm1hdHRlci5BYnN0cmFjdEZvcm1hdHRlclRlc3QiLCJjb250ZXh0TmFtZSI6bnVsbCwicGF0aE5hbWUiOm51bGwsImFyZ3MiOltdLCJjYWxsZXJEYXRhIjpudWxsLCJjbGFzc09mQ2FsbGVyIjpudWxsLCJtZXRob2RPZkNhbGxlciI6bnVsbCwibGluZW5vIjpudWxsLCJtYXJrZXIiOm51bGwsInJlbGF0aXZlVGltZSI6bnVsbCwidGltZSI6bnVsbCwidGVtcGxhdGUiOiJkdW1teSBtZXNzYWdlIiwibWVzc2FnZSI6ImR1bW15IG1lc3NhZ2UiLCJ0aHJlYWROYW1lIjpudWxsLCJ0aHJvd2FibGVQcm94eSI6bnVsbCwibGV2ZWwiOiJlcnJvciIsInRpbWVzdGFtcCI6MTM5NDIyNDY1Njc4OSwibWRjIjpudWxsfV19";

        public static final String NO_LOGS = "e30=";

//        @Test
//        public void testFormat_nominal_case() throws Exception {
//            //given
//            ArrayList<Row> iLoggingEvents = getILoggingEvents();
//            FireLoggerFormatter fireLoggerFormatter = new FireLoggerFormatter();
//            //when
//            String formattedLogs = fireLoggerFormatter.format(iLoggingEvents);
//            System.out.println(formattedLogs.length());
//            System.out.println(EXPECTED.length());
//            //then
//            assertThat(formattedLogs).isEqualTo(EXPECTED);
//        }
        @Test
        public void testFormat_with_no_events() throws Exception {
            //given
            ArrayList<Row> iLoggingEvents = Lists.newArrayList();
            FireLoggerFormatter fireLoggerFormatter = new FireLoggerFormatter();
            //when
            String formattedLogs = fireLoggerFormatter.format(iLoggingEvents);
            //then
            assertThat(formattedLogs).isEqualTo(NO_LOGS);
        }
    }

    public static class Test_getJSON extends AbstractFormatterTest{

        public static final String EMPTY_JSON_OBJECT = "{}";

        @Test
        public void test_no_rows() throws Exception {
            //given
            FireLoggerFormatter fireLoggerFormatter = new FireLoggerFormatter();
            List<Row> rows = Lists.newArrayList();
            //when
            String json = fireLoggerFormatter.getJSON(rows);

            //then
            assertThat(json).isEqualTo(EMPTY_JSON_OBJECT);

        }

        @Test
        public void test_one_row() throws Exception {
            //given
            FireLoggerFormatter fireLoggerFormatter = new FireLoggerFormatter();
            List<Row> rows = Lists.newArrayList();
            ILoggingEvent iLoggingEvent = new LoggingEvent(FireLoggerFormatterTest.class.getName(),LOGGER, Level.INFO,"test message",null,new Object[]{});

            Row row = new Row(iLoggingEvent);
            rows.add(row);
            //when
            String json = fireLoggerFormatter.getJSON(rows);
            String expected = "{\"logs\":[{\"name\":\"com.clescot.webappender.formatter.FireLoggerFormatterTest\",\"contextName\":null,\"pathName\":null,\"args\":[],\"callerData\":null,\"classOfCaller\":null,\"methodOfCaller\":null,\"lineno\":null,\"marker\":null,\"relativeTime\":null,\"time\":null,\"template\":\"test message\",\"message\":\"test message\",\"threadName\":null,\"throwableProxy\":null,\"level\":\"info\",\"mdc\":null}]}";
            //then
            MatcherAssert.assertThat(new JSONObject(json), SameJSONAs.sameJSONObjectAs(new JSONObject(expected)).allowingExtraUnexpectedFields()
                    .allowingAnyArrayOrdering());
        }

    }


}
