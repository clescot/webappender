package com.clescot.webappender.tag;

import com.clescot.webappender.collector.LogCollector;
import com.clescot.webappender.filter.FiltersModule;
import com.clescot.webappender.formatter.FormattersModule;
import com.clescot.webappender.formatter.Row;
import com.clescot.webappender.jee.WebAppenderFilter;
import com.clescot.webappender.jee.WebAppenderTag;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
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

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class WebAppenderTagTest {

    public static class TestDoStartTag {

        private static Logger LOGGER = LoggerFactory.getLogger(TestDoStartTag.class);
        private MockServletContext mockHttpServletContext;
        private MockHttpServletRequest mockHttpServletRequest;
        private MockHttpServletResponse mockHttpServletResponse;
        private MockPageContext mockPageContext;
        private WebAppenderTag tag = new WebAppenderTag();
        private LogCollector logCollector;
        private List<Row> rows = Lists.newArrayList();

        @Before
        public void setUp() {
            mockHttpServletContext = new MockServletContext();
            mockHttpServletRequest= new MockHttpServletRequest();
            mockHttpServletResponse = new MockHttpServletResponse();
            mockPageContext= new MockPageContext(mockHttpServletContext, mockHttpServletRequest, mockHttpServletResponse);
            mockHttpServletRequest.addHeader("X-BodyLogger","true");
            Injector injector = Guice.createInjector(new FiltersModule(), new FormattersModule());
            logCollector = injector.getInstance(LogCollector.class);
            logCollector.removeCurrentThreadAppender();
            logCollector.shutdown();
            mockPageContext.getServletContext().setAttribute(WebAppenderFilter.WEBAPPENDER_LOGCOLLECTOR_SERVLET_CONTEXT_KEY, logCollector);
            tag.setPageContext(mockPageContext);
        }

        @After
        public void tearDown() {
            logCollector.removeCurrentThreadAppender();
            logCollector.shutdown();
        }



        @Test
        public void test_one_logs() throws Exception {

            LOGGER.debug("test");

            //when
            tag.doStartTag();
            //then
            String responseAsString = mockHttpServletResponse.getContentAsString();
            assertThat(responseAsString).startsWith("<script type=\"text/javascript\">console.");

        }


    }


}