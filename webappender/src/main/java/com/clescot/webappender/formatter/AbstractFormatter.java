package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.util.List;

 abstract class AbstractFormatter<R> implements Formatter {

    private static final boolean IS_NOT_CHUNKED = false;
     private static final String UTF_8 = "UTF-8";
     final ObjectMapper objectMapper = new ObjectMapper();


    protected abstract String getJSON(List<Row> rows);


    public String format(List<Row> rows) throws JsonProcessingException {
        return new String(Base64.encodeBase64(getJSON(rows).getBytes(), IS_NOT_CHUNKED), Charset.forName(UTF_8));
    }


     List<R> getFormatterRows(List<Row> rows){
        return Lists.transform(rows, new Function<Row, R>() {

            @Override
            public R apply(Row input) {
                return newFormatterRow(input);
            }
        });
    }


    protected abstract R newFormatterRow(Row row);
}
