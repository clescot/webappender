package com.clescot.webappender.formatter;

import com.clescot.webappender.Row;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.datumedge.hamcrest.json.SameJSONAs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class ChromeLoggerFormatterTest {



    public static class TestFormat extends AbstractFormatterTest {

        public static final String NO_EVENTS_IN_BASE64 = "eyJ2ZXJzaW9uIjogIjEuMCIsImNvbHVtbnMiOiBbImxvZyIsICJiYWNrdHJhY2UiLCAidHlwZSJdLCJyb3dzIjogW119";

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
            String expected ="{\"version\": \"1.0\",\"columns\": [\"log\", \"backtrace\", \"type\"],\"rows\": [[[{\"___class_name\": \"com.clescot.webappender.formatter.AbstractFormatterTest\",\"message\":\"dummy message\",\"callerData\":\"\",\"classOfCaller\":\"\",\"contextName\":\"\",\"marker\":\"\",\"mdc\":\"\",\"methodOfCaller\":\"\",\"relativeTime\":\"\",\"template\":\"dummy message\",\"threadName\":\"\",\"throwableProxy\":\"\",\"time\":\"\"}],\":\",\"ERROR\"],[[{\"___class_name\": \"com.clescot.webappender.formatter.AbstractFormatterTest\",\"message\":\"dummy message\",\"callerData\":\"\",\"classOfCaller\":\"\",\"contextName\":\"\",\"marker\":\"\",\"mdc\":\"\",\"methodOfCaller\":\"\",\"relativeTime\":\"\",\"template\":\"dummy message\",\"threadName\":\"\",\"throwableProxy\":\"\",\"time\":\"\"}],\":\",\"ERROR\"]]}";
            JSONObject jsonObject = new JSONObject(json);
            MatcherAssert.assertThat(jsonObject, SameJSONAs.sameJSONObjectAs(new JSONObject(expected)).allowingExtraUnexpectedFields()
                    .allowingAnyArrayOrdering());
        }


    }

    public static class TestIsActive{
        @Test
        public void test_chromium_browser() throws Exception {
            //given
            ChromeLoggerFormatter chromeLoggerFormatter = new ChromeLoggerFormatter();
            String chromiumUserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/32.0.1700.107 Chrome/32.0.1700.107 Safari/537.36";
            Map<String,List<String>> headers = Maps.newHashMap();
            headers.put("user-agent", Arrays.asList(chromiumUserAgent));
            //when
            boolean active = chromeLoggerFormatter.isActive(headers);
            //then
            assertThat(active).isTrue();


        }

        @Test
        public void test_firefox_browser() throws Exception {
            ChromeLoggerFormatter chromeLoggerFormatter = new ChromeLoggerFormatter();
            String firefoxUserAgent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:27.0) Gecko/20100101 Firefox/27.0";
            Map<String,List<String>> headers = Maps.newHashMap();
            headers.put("user-agent", Arrays.asList(firefoxUserAgent));
            //when
            boolean active = chromeLoggerFormatter.isActive(headers);
            //then
            assertThat(active).isFalse();


        }

    }


}
