package com.clescot.webappender.formatter;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Enclosed.class)
public class BodyFormatterTest {


    public static class GetJson{

        @Test
        public void testWithEmptyList(){
            //given
            BodyFormatter bodyFormatter = new BodyFormatter();

            //when
            String json = bodyFormatter.getJSON(Lists.<Row>newArrayList());

            //then
            assertThat(json).isEmpty();
        }

        @Test
        public void testWithNull(){
            //given
            BodyFormatter bodyFormatter = new BodyFormatter();

            //when
            String json = bodyFormatter.getJSON(null);

            //then
            assertThat(json).isEmpty();
        }

        @Test
        public void testWithOneRow(){
            //given
            BodyFormatter bodyFormatter = new BodyFormatter();
            ArrayList<Row> rows = Lists.newArrayList();
            ILoggingEvent event = new LoggingEvent();
            Row row = new Row(event);
            rows.add(row);
            //when
            String json = bodyFormatter.getJSON(rows);

            //then
            assertThat(json).isEqualTo("<script type=\"text/javascript\"></script>");
        }
    }
}
