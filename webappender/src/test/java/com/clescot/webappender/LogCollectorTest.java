package com.clescot.webappender;

import com.clescot.webappender.collector.LogCollector;
import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class LogCollectorTest {


    private static Logger LOGGER = LoggerFactory.getLogger(LogCollectorTest.class);


    public static class TestGetLogs {
        private LogCollector logCollector;

        @Rule
        public ConcurrentRule concurrentRule = new ConcurrentRule();

        @Before
        public void before(){
             logCollector = LogCollector.newLogCollector();
        }

        @After
        public void after(){
            logCollector.shutdown();
        }

        @Test
        @Concurrent(count = 10)
        public void test_grab_one_log() throws Exception {
                    LOGGER.error("test");
                    assertThat(logCollector.getLogs()).hasSize(1);


        }

        @Test
        @Concurrent(count = 10)
        public void test_grab_two_logs_() throws Exception {
                    LOGGER.error("test");
                    LOGGER.error("test2");
                    assertThat(logCollector.getLogs()).hasSize(2);
        }

        @Test
        @Concurrent(count = 10)
        public void test_grab_logs_3_times() throws Exception {
                    LOGGER.error("test");
                    LOGGER.error("test2");
                    assertThat(logCollector.getLogs()).hasSize(2);
                    assertThat(logCollector.getLogs()).hasSize(2);
                    assertThat(logCollector.getLogs()).hasSize(2);

        }


        @Test
        @Concurrent(count = 10)
        public void test_grab_3_logs_with_multiple_get_logs() throws Exception {

                    LOGGER.error("test");
                    LOGGER.error("test2");
                    assertThat(logCollector.getLogs()).hasSize(2);
                    LOGGER.error("test3");
                    assertThat(logCollector.getLogs()).hasSize(3);
                    LOGGER.error("test4");
                    assertThat(logCollector.getLogs()).hasSize(4);
        }

        @Test
        @Concurrent(count = 10)
        public void test_grab_one_log_in_with_10_sub_thread() throws Exception {
                        LOGGER.error("test");
                        assertThat(logCollector.getLogs()).hasSize(1);
        }


        @Test
        @Concurrent(count = 100)
        public void test_grab_3_logs_in_with_10_sub_thread() throws Exception {
            LOGGER.error("test");
            LOGGER.error("test");
            LOGGER.error("test");
            assertThat(logCollector.getLogs()).hasSize(3);
        }
    }

    public static class TestRemoveCurrentThreadAppender {

        public static final int TIMEOUT_TO_REACH_STALE_STATE_IN_LINGERED_MAP_OF_APPENDER_TRACKER = 11000;

        @Test
        public void nominal_case() throws InterruptedException {
            LogCollector logCollector = LogCollector.newLogCollector();
            LOGGER.error("test");
            logCollector.getLogs();
            assertThat(logCollector.getLogs()).hasSize(1);
            Thread.sleep(TIMEOUT_TO_REACH_STALE_STATE_IN_LINGERED_MAP_OF_APPENDER_TRACKER);
            logCollector.removeCurrentThreadAppender();
            assertThat(logCollector.getLogs()).hasSize(0);
        }

    }

    public static class Test_shutdown{

        @Test
        public void nominal_case() throws InterruptedException {
            //given
            LogCollector logCollector = LogCollector.newLogCollector();

            //when
            logCollector.shutdown();

            //then
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getILoggerFactory().getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            assertThat(rootLogger.getAppender(LogCollector.SIFTING_APPENDER_KEY)).isNull();
        }

        @Test
        public void test_before_shutdown() throws InterruptedException {
            //when
            LogCollector logCollector = LogCollector.newLogCollector();

            //then
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getILoggerFactory().getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            assertThat(rootLogger.getAppender(LogCollector.SIFTING_APPENDER_KEY)).isNotNull();
        }
    }
}
