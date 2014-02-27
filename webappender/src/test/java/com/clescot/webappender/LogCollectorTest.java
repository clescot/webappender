package com.clescot.webappender;

import com.clescot.webappender.collector.LogCollector;
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
        @Test
        public void test_grab_one_log() throws Exception {
            Runnable myRunnable = new Runnable() {

                public void run() {
                    LogCollector logCollector = LogCollector.newLogCollector();
                    LOGGER.error("test");
                    assertThat(logCollector.getLogs()).hasSize(1);
                }
            };


            Thread thread = new Thread(myRunnable);
            thread.start();
        }

        @Test
        public void test_grab_two_logs_() throws Exception {
            Runnable myRunnable = new Runnable() {

                public void run() {
                    LogCollector logCollector = LogCollector.newLogCollector();
                    LOGGER.error("test");
                    LOGGER.error("test2");
                    assertThat(logCollector.getLogs()).hasSize(2);
                }
            };


            Thread thread = new Thread(myRunnable);
            thread.start();
        }

        @Test
        public void test_grab_logs_3_times() throws Exception {
            Runnable myRunnable = new Runnable() {

                public void run() {
                    LogCollector logCollector = LogCollector.newLogCollector();
                    LOGGER.error("test");
                    LOGGER.error("test2");
                    assertThat(logCollector.getLogs()).hasSize(2);
                    assertThat(logCollector.getLogs()).hasSize(2);
                    assertThat(logCollector.getLogs()).hasSize(2);
                }
            };


            Thread thread = new Thread(myRunnable);
            thread.start();
        }


        @Test
        public void test_grab_3_logs_with_multiple_get_logs() throws Exception {
            Runnable myRunnable = new Runnable() {

                public void run() {
                    LogCollector logCollector = LogCollector.newLogCollector();
                    LOGGER.error("test");
                    LOGGER.error("test2");
                    assertThat(logCollector.getLogs()).hasSize(2);
                    LOGGER.error("test3");
                    assertThat(logCollector.getLogs()).hasSize(3);
                    LOGGER.error("test4");
                    assertThat(logCollector.getLogs()).hasSize(4);
                }
            };


            Thread thread = new Thread(myRunnable);
            thread.start();
        }

        @Test
        public void test_grab_one_log_in_with_100_sub_thread() throws Exception {

            for (int i = 0; i < 10; i++) {
                Runnable myRunnable = new Runnable() {

                    public void run() {
                        LogCollector logCollector = LogCollector.newLogCollector();
                        LOGGER.error("test");
                        assertThat(logCollector.getLogs()).hasSize(1);
                    }
                };


                Thread thread = new Thread(myRunnable);
                thread.start();
            }
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
}
