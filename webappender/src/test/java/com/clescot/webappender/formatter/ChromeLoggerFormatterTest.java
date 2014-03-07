package com.clescot.webappender.formatter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.clescot.webappender.Row;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class ChromeLoggerFormatterTest {
    private static Logger LOGGER = LoggerFactory.getLogger(ChromeLoggerFormatterTest.class);

    @Ignore
    public static class AbstractTestClass {

        protected ArrayList<Row> getiLoggingEvents() {
            Row iLoggingEvent = new Row(new LoggingEvent(this.getClass().getName(), (ch.qos.logback.classic.Logger) LOGGER, Level.ERROR, "dummy message", null, null));
            Row iLoggingEvent2 =  new Row(new LoggingEvent(this.getClass().getName(), (ch.qos.logback.classic.Logger) LOGGER, Level.ERROR, "dummy message", null, null));
            ArrayList<Row> iLoggingEvents = Lists.newArrayList();
            iLoggingEvents.add(iLoggingEvent);
            iLoggingEvents.add(iLoggingEvent2);
            return iLoggingEvents;
        }
    }

    @Ignore
    public static class TestFormat extends AbstractTestClass {
        @Test
        public void testFormat_nominal_case() throws Exception {
            ArrayList<Row> iLoggingEvents = getiLoggingEvents();
            ChromeLoggerFormatter chromeLoggerFormatter = new ChromeLoggerFormatter();
            String formattedLogs = chromeLoggerFormatter.format(iLoggingEvents);
            assertThat(formattedLogs).isEqualTo("expected");

        }


    }

    public static class TestGetJSON extends AbstractTestClass {
        @InjectMocks
        private ChromeLoggerFormatter chromeLoggerFormatterMocked;

        @Mock
        private ObjectMapper objectMapper;

        @Before
        public void setUp() throws Exception {
            MockitoAnnotations.initMocks(this);
        }

        @Test
        public void test_get_json_nominal_case() throws Exception {
            ChromeLoggerFormatter chromeLoggerFormatter = new ChromeLoggerFormatter();
            String json = chromeLoggerFormatter.getJSON(getiLoggingEvents());
            assertThat(json).isEqualTo("{\"columns\":[\"log\",\"backtrace\",\"type\"],\"rows\":[{\"logData\":{\"message\":\"dummy message\",\"___class_name\":\"com.clescot.webappender.formatter.ChromeLoggerFormatterTest\"},\"backtraceData\":\"null:null\",\"logType\":\"error\"},{\"logData\":{\"message\":\"dummy message\",\"___class_name\":\"com.clescot.webappender.formatter.ChromeLoggerFormatterTest\"},\"backtraceData\":\"null:null\",\"logType\":\"error\"}],\"version\":\"1.0\"}");
        }

        @Test(expected = RuntimeException.class)
        public void test_get_json_when_object_mapper_throw_a_json_processing_exception() throws Exception {

            //given
            when(objectMapper.writeValueAsString(anyObject())).thenThrow(new JsonGenerationException(""));


            //when
            chromeLoggerFormatterMocked.getJSON(getiLoggingEvents());

            //then

        }
    }


}
