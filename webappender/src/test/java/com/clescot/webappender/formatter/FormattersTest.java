package com.clescot.webappender.formatter;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class FormattersTest {

    @Ignore
    public static class AbstractTest{
        protected Formatters formattersBuilder;
        private Injector injector = Guice.createInjector(new FormattersModule());
        @Before
        public void setUp(){
            formattersBuilder = injector.getInstance(Formatters.class);
        }
    }

    public static class TestFindFormatter extends AbstractTest{


        @Test
        public void test_with_firelogger_header() throws Exception {
            //given
            Map<String,List<String>> headers = Maps.newHashMap();
            headers.put(FireLoggerFormatter.REQUEST_HEADER_IDENTIFIER,Arrays.asList(""));
            //when
            Optional<? extends Formatter> optionalFound = formattersBuilder.findFormatter(headers);

            //then
            assertThat(optionalFound.isPresent()).isTrue();
            Formatter formatter = optionalFound.get();
            assertThat(formatter instanceof FireLoggerFormatter);

        }

        @Test
        public void test_wit_chrome_logger_header() throws Exception {
            //given
            Map<String,List<String>> headers = Maps.newHashMap();
            headers.put(ChromeLoggerFormatter.REQUEST_HEADER_IDENTIFIER,Arrays.asList("dummy value") );
            //when
            Optional<? extends Formatter> optionalFound = formattersBuilder.findFormatter(headers);

            //then
            assertThat(optionalFound.isPresent()).isTrue();
            Formatter formatter = optionalFound.get();
            assertThat(formatter instanceof ChromeLoggerFormatter);

        }

        @Test
        public void test_with_unknown_header() throws Exception {
            //given
            Map<String,List<String>> headers = Maps.newHashMap();
            headers.put("unknown",Arrays.asList(""));
            //when
            Optional<? extends Formatter> optionalFound = formattersBuilder.findFormatter(headers);

            //then
            assertThat(optionalFound.isPresent()).isFalse();

        }

        @Test
        public void test_with_firelogger_headers() throws Exception {
            //given
            Map<String,List<String>> headers = Maps.newTreeMap();
            headers.put(FireLoggerFormatter.REQUEST_HEADER_IDENTIFIER,Arrays.asList(""));
            //when
            Optional<? extends Formatter> optionalFound = formattersBuilder.findFormatter(headers);

            //then
            assertThat(optionalFound.isPresent()).isTrue();
            Formatter formatter = optionalFound.get();
            assertThat(formatter instanceof ChromeLoggerFormatter);

        }


    }


}
