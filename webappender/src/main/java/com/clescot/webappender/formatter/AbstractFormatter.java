package com.clescot.webappender.formatter;

import com.clescot.webappender.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.util.List;

public abstract class AbstractFormatter implements Formatter {

    public static final boolean NO_CR_LF_TRAILING_CHARACTERS = false;
    public static final String UTF_8 = "UTF-8";
    protected ObjectMapper objectMapper = new ObjectMapper();


    protected abstract String getJSON(List<Row> rows) throws JsonProcessingException;


    public String format(List<Row> rows) throws JsonProcessingException {
        return new String(Base64.encodeBase64(getJSON(rows).getBytes(), NO_CR_LF_TRAILING_CHARACTERS), Charset.forName(UTF_8));
    }
}
