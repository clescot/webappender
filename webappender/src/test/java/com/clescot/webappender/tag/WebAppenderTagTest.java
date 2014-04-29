package com.clescot.webappender.tag;

import com.clescot.webappender.collector.LogCollector;
import com.clescot.webappender.formatter.Row;
import com.clescot.webappender.jee.WebAppenderFilter;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import java.util.List;
import java.util.regex.Pattern;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class WebAppenderTagTest {

    public static class TestDoStartTag {

        private static Logger LOGGER = LoggerFactory.getLogger(TestDoStartTag.class);
        private MockServletContext mockHttpServletContext = new MockServletContext();
        private MockHttpServletRequest mockHttpServletRequest;
        private MockHttpServletResponse mockHttpServletResponse;
        private MockPageContext mockPageContext;
        private WebAppenderTag tag = new WebAppenderTag();
        private LogCollector logCollector;
        private List<Row> rows = Lists.newArrayList();
        public static final Pattern lineNumberPattern = Pattern.compile("\"lineNumber\":\"\\d*\"");
        public static final Pattern timestampPattern = Pattern.compile("\"timestamp\":\\d*");
        public static final Pattern relativeTimePattern = Pattern.compile("\"relativeTime\":\"\\d*\"");
        public static final Pattern timePattern = Pattern.compile("\"time\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}\"");

        @Before
        public void setUp() {
            mockHttpServletRequest= new MockHttpServletRequest();
            mockHttpServletResponse = new MockHttpServletResponse();
            mockPageContext= new MockPageContext(mockHttpServletContext, mockHttpServletRequest, mockHttpServletResponse);
            mockHttpServletRequest.addHeader("X-BodyLogger","true");
            logCollector = LogCollector.newLogCollector();
            mockPageContext.getServletContext().setAttribute(WebAppenderFilter.WEBAPPENDER_LOGCOLLECTOR_SERVLET_CONTEXT_KEY, logCollector);
            tag.setPageContext(mockPageContext);
        }

        @After
        public void tearDown() {
            logCollector.removeCurrentThreadAppender();
            logCollector.shutdown();
        }

        @Test
        public void test_no_logs() throws Exception {
            //when
            tag.doStartTag();
            //then
            String responseAsString = mockHttpServletResponse.getContentAsString();
            assertThat(responseAsString.trim()).isEmpty();

        }

        @Test
        public void test_one_logs() throws Exception {

            LOGGER.debug("test");

            //when
            tag.doStartTag();
            //then
            String responseAsString = mockHttpServletResponse.getContentAsString();
            responseAsString = lineNumberPattern.matcher(responseAsString).replaceFirst("\"lineNumber\":\"1\"");
            responseAsString = timestampPattern.matcher(responseAsString).replaceFirst("\"timestamp\":1");
            responseAsString = relativeTimePattern.matcher(responseAsString).replaceFirst("\"relativeTime\":\"1\"");
            responseAsString = timePattern.matcher(responseAsString).replaceFirst("\"time\":\"1\"");
            assertThat(responseAsString).isEqualTo("<script type=\"text/javascript\">console.debug({\"message\":\"test\",\"template\":\"test\",\"args\":[],\"level\":{\"levelInt\":10000,\"levelStr\":\"DEBUG\"},\"timestamp\":1,\"relativeTime\":\"1\",\"threadName\":\"main\",\"classOfCaller\":\"com.clescot.webappender.tag.WebAppenderTagTest$TestDoStartTag\",\"methodOfCaller\":\"test_one_logs\",\"mdc\":\"\",\"throwableProxy\":\"\",\"contextName\":\"default\",\"callerData\":\"Caller+0\\t at com.clescot.webappender.tag.WebAppenderTagTest$TestDoStartTag.test_one_logs(WebAppenderTagTest.java:72)\\nCaller+1\\t at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\\nCaller+2\\t at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\\nCaller+3\\t at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\\nCaller+4\\t at java.lang.reflect.Method.invoke(Method.java:601)\\n\",\"marker\":\"\",\"time\":\"1\",\"name\":\"com.clescot.webappender.tag.WebAppenderTagTest$TestDoStartTag\",\"pathName\":\"WebAppenderTagTest.java\",\"lineNumber\":\"1\"});</script>\n");

        }


    }


}