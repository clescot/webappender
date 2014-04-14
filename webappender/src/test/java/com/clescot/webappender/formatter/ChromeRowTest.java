package com.clescot.webappender.formatter;


import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class ChromeRowTest {

    public static class TestConstructor{

        @Test(expected = IllegalArgumentException.class)
        public void testNull(){

         new ChromeRow(null);
        }
    }
}
