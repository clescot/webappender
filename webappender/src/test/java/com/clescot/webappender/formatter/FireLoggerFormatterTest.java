package com.clescot.webappender.formatter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class FireLoggerFormatterTest {
private static Logger LOGGER = (Logger) LoggerFactory.getLogger(FireLoggerFormatterTest.class);


    public static class Test_Format extends AbstractFormatterTest{


        public static final String NO_LOGS = "e30=";


        @Test
        public void testFormat_with_no_events() throws Exception {
            //given
            ArrayList<Row> iLoggingEvents = Lists.newArrayList();
            FireLoggerFormatter fireLoggerFormatter = new FireLoggerFormatter();
            //when
            String formattedLogs = fireLoggerFormatter.encodeBase64(fireLoggerFormatter.getJSON(iLoggingEvents));
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

    public static class TestIsActive{
        @Test
        public void test_is_active_nominal_case() throws Exception {
            //given
            FireLoggerFormatter fireLoggerFormatter = new FireLoggerFormatter();
            Map<String, List<String>> headers = Maps.newHashMap();
            headers.put(FireLoggerFormatter.REQUEST_HEADER_IDENTIFIER, Arrays.asList(""));

            //when
            boolean active = fireLoggerFormatter.isActive(headers);
            //then
            assertThat(active).isTrue();

        }

        @Test
        public void test_is_active_with_bad_case() throws Exception {
            //given
            FireLoggerFormatter fireLoggerFormatter = new FireLoggerFormatter();
            Map<String, List<String>> headers = Maps.newHashMap();
            headers.put("X-fiReLoGger", Arrays.asList(""));

            //when
            boolean active = fireLoggerFormatter.isActive(headers);
            //then
            assertThat(active).isTrue();

        }


    }


}
