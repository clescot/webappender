package com.clescot.webappender.formatter;

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



    public static class TestFormat extends AbstractFormatterTest {

        public static final String NO_EVENTS_IN_BASE64 = "eyJjb2x1bW5zIjpbImxvZyIsImJhY2t0cmFjZSIsInR5cGUiXSwicm93cyI6W10sInZlcnNpb24iOiIxLjAifQ==";
        public static final String EVENTS_SERIALIZED = "eyJjb2x1bW5zIjpbImxvZyIsImJhY2t0cmFjZSIsInR5cGUiXSwicm93cyI6W3sibG9nRGF0YSI6eyJtZXNzYWdlIjoiZHVtbXkgbWVzc2FnZSIsIl9fX2NsYXNzX25hbWUiOiJjb20uY2xlc2NvdC53ZWJhcHBlbmRlci5mb3JtYXR0ZXIuQ2hyb21lTG9nZ2VyRm9ybWF0dGVyVGVzdCJ9LCJiYWNrdHJhY2VEYXRhIjoibnVsbDpudWxsIiwibG9nVHlwZSI6ImVycm9yIn0seyJsb2dEYXRhIjp7Im1lc3NhZ2UiOiJkdW1teSBtZXNzYWdlIiwiX19fY2xhc3NfbmFtZSI6ImNvbS5jbGVzY290LndlYmFwcGVuZGVyLmZvcm1hdHRlci5DaHJvbWVMb2dnZXJGb3JtYXR0ZXJUZXN0In0sImJhY2t0cmFjZURhdGEiOiJudWxsOm51bGwiLCJsb2dUeXBlIjoiZXJyb3IifV0sInZlcnNpb24iOiIxLjAifQ==";

        @Test
        @Ignore
        public void testFormat_nominal_case() throws Exception {
            //given
            ArrayList<Row> iLoggingEvents = getILoggingEvents();
            ChromeLoggerFormatter chromeLoggerFormatter = new ChromeLoggerFormatter();
            //when
            String formattedLogs = chromeLoggerFormatter.format(iLoggingEvents);
            //then
            assertThat(formattedLogs).isEqualTo(EVENTS_SERIALIZED);
        }
        @Test
        public void testFormat_with_no_events() throws Exception {
            //given
            ArrayList<Row> iLoggingEvents = Lists.newArrayList();
            ChromeLoggerFormatter chromeLoggerFormatter = new ChromeLoggerFormatter();
            //when
            String formattedLogs = chromeLoggerFormatter.format(iLoggingEvents);
            //then
            assertThat(formattedLogs).isEqualTo(NO_EVENTS_IN_BASE64);
        }


    }

    public static class TestGetJSON extends AbstractFormatterTest {
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
            String json = chromeLoggerFormatter.getJSON(getILoggingEvents());
            assertThat(json).isEqualTo("{\"columns\":[\"log\",\"backtrace\",\"type\"],\"rows\":[{\"logData\":{\"message\":\"dummy message\",\"___class_name\":\"com.clescot.webappender.formatter.AbstractFormatterTest\"},\"backtraceData\":\"null:null\",\"logType\":\"error\"},{\"logData\":{\"message\":\"dummy message\",\"___class_name\":\"com.clescot.webappender.formatter.AbstractFormatterTest\"},\"backtraceData\":\"null:null\",\"logType\":\"error\"}],\"version\":\"1.0\"}");
        }

        @Test(expected = RuntimeException.class)
        public void test_get_json_when_object_mapper_throw_a_json_processing_exception() throws Exception {

            //given
            when(objectMapper.writeValueAsString(anyObject())).thenThrow(new JsonGenerationException(""));


            //when
            chromeLoggerFormatterMocked.getJSON(getILoggingEvents());

            //then

        }
    }


}
