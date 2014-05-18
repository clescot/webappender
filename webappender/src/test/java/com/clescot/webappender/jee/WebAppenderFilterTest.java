package com.clescot.webappender.jee;


import com.clescot.webappender.filter.FiltersModule;
import com.clescot.webappender.formatter.ChromeLoggerFormatter;
import com.clescot.webappender.formatter.FireLoggerFormatter;
import com.clescot.webappender.formatter.FormattersModule;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class WebAppenderFilterTest {
    private static Logger LOGGER = LoggerFactory.getLogger(WebAppenderFilterTest.class);

    public static class test_do_filter {

        private MockHttpServletRequest httpServletRequest;
        private HttpServletResponse httpServletResponse;
        private MockFilterChain filterChain;
        private MockFilterConfig filterConfig;
        private WebAppenderFilter webAppenderFilter;

        @Before
        public void setUp() throws ServletException {
            httpServletRequest = new MockHttpServletRequest();
            httpServletResponse = new MockHttpServletResponse();
            webAppenderFilter = new WebAppenderFilter();
            filterChain = new MockFilterChain(new MockServlet(), webAppenderFilter);
            filterConfig = new MockFilterConfig();
            Injector injector = Guice.createInjector(new FiltersModule(), new FormattersModule());
            filterConfig.getServletContext().setAttribute(Injector.class.getName(), injector);
        }

        @Test
        public void test_without_system_property_flag_to_enable_web_appender() throws Exception {
            //given
            webAppenderFilter.init(filterConfig);
            //when
            filterChain.doFilter(httpServletRequest, httpServletResponse);

            //then
            Collection<String> headerNames = httpServletResponse.getHeaderNames();
            Optional<String> optionalResult = Iterables.tryFind(headerNames, new Predicate<String>() {
                @Override
                public boolean apply(String headerKey) {
                    return headerKey.startsWith(FireLoggerFormatter.FIRELOGGER_RESPONSE_HEADER_PREFIX) || headerKey.startsWith(ChromeLoggerFormatter.RESPONSE_CHROME_LOGGER_HEADER);
                }
            });

            assertThat(optionalResult.isPresent()).isFalse();
        }

        @Test
        public void test_with_system_flag_but_without_headers_from_plugins() throws Exception {
            //given
            System.setProperty(WebAppenderFilter.SYSTEM_PROPERTY_KEY, "true");
            webAppenderFilter.init(filterConfig);

            //when
            filterChain.doFilter(httpServletRequest, httpServletResponse);

            //then
            Collection<String> headerNames = httpServletResponse.getHeaderNames();
            Optional<String> optionalResult = Iterables.tryFind(headerNames, new Predicate<String>() {
                @Override
                public boolean apply(String headerKey) {
                    return headerKey.startsWith(FireLoggerFormatter.FIRELOGGER_RESPONSE_HEADER_PREFIX) || headerKey.startsWith(ChromeLoggerFormatter.RESPONSE_CHROME_LOGGER_HEADER);
                }
            });

            assertThat(optionalResult.isPresent()).isFalse();
            System.setProperty(WebAppenderFilter.SYSTEM_PROPERTY_KEY, "disable");
        }

        @Test
        public void test_with_system_flag_with_firelogger_header() throws Exception {
            //given
            System.setProperty(WebAppenderFilter.SYSTEM_PROPERTY_KEY, "true");
            webAppenderFilter.init(filterConfig);
            httpServletRequest.setMethod("GET");

            httpServletRequest.addHeader(FireLoggerFormatter.REQUEST_HEADER_IDENTIFIER, "dummy value");
            //when
            filterChain.doFilter(httpServletRequest, httpServletResponse);

            //then
            Collection<String> headerNames = httpServletResponse.getHeaderNames();
            Optional<String> optionalResult = Iterables.tryFind(headerNames, new Predicate<String>() {
                @Override
                public boolean apply(String headerKey) {
                    return headerKey.startsWith(FireLoggerFormatter.FIRELOGGER_RESPONSE_HEADER_PREFIX);
                }
            });

            assertThat(optionalResult.isPresent()).isTrue();
        }

        @Test
        public void test_with_system_flag_with_chrome_user_agent_header() throws Exception {
            //given
            System.setProperty(WebAppenderFilter.SYSTEM_PROPERTY_KEY, "true");
            webAppenderFilter.init(filterConfig);
            httpServletRequest.setMethod("GET");

            httpServletRequest.addHeader(ChromeLoggerFormatter.REQUEST_HEADER_IDENTIFIER, "dummy value");
            //when
            filterChain.doFilter(httpServletRequest, httpServletResponse);

            //then
            Collection<String> headerNames = httpServletResponse.getHeaderNames();
            Optional<String> optionalResult = Iterables.tryFind(headerNames, new Predicate<String>() {
                @Override
                public boolean apply(String headerKey) {
                    return headerKey.startsWith(ChromeLoggerFormatter.RESPONSE_CHROME_LOGGER_HEADER);
                }
            });

            assertThat(optionalResult.isPresent()).isTrue();
        }


    }

    private static class MockServlet extends HttpServlet {

        private static Logger LOGGER = LoggerFactory.getLogger(MockServlet.class);

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
            LOGGER.error("test1");
            LOGGER.error("test2");
            LOGGER.error("test3");
        }
    }
}
