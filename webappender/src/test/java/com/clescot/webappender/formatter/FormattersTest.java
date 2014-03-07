package com.clescot.webappender.formatter;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class FormattersTest {
    public static class TestFindFormatter{

        @Test
        public void test_with_firelogger_header() throws Exception {
            //given
            Map<String,String> headers = Maps.newHashMap();
            headers.put(FireLoggerFormatter.REQUEST_HEADER_IDENTIFIER,"");
            //when
            Optional<? extends Formatter> optionalFound = Formatters.findFormatter(headers);

            //then
            assertThat(optionalFound.isPresent()).isTrue();
            Formatter formatter = optionalFound.get();
            assertThat(formatter instanceof FireLoggerFormatter);

        }

        @Test
        public void test_with_chrome_logger_header() throws Exception {
            //given
            Map<String,String> headers = Maps.newHashMap();
            headers.put(ChromeLoggerFormatter.REQUEST_HEADER_IDENTIFIER,"");
            //when
            Optional<? extends Formatter> optionalFound = Formatters.findFormatter(headers);

            //then
            assertThat(optionalFound.isPresent()).isTrue();
            Formatter formatter = optionalFound.get();
            assertThat(formatter instanceof ChromeLoggerFormatter);

        }

        @Test
        public void test_with_unknown_header() throws Exception {
            //given
            Map<String,String> headers = Maps.newHashMap();
            headers.put("unknown","");
            //when
            Optional<? extends Formatter> optionalFound = Formatters.findFormatter(headers);

            //then
            assertThat(optionalFound.isPresent()).isFalse();

        }

        @Test
        public void test_with_chrome_logger_and_firelogger_headers() throws Exception {
            //given
            Map<String,String> headers = Maps.newTreeMap();
            headers.put(ChromeLoggerFormatter.REQUEST_HEADER_IDENTIFIER,"");
            headers.put(FireLoggerFormatter.REQUEST_HEADER_IDENTIFIER,"");
            //when
            Optional<? extends Formatter> optionalFound = Formatters.findFormatter(headers);

            //then
            assertThat(optionalFound.isPresent()).isTrue();
            Formatter formatter = optionalFound.get();
            assertThat(formatter instanceof ChromeLoggerFormatter);

        }


    }


}
