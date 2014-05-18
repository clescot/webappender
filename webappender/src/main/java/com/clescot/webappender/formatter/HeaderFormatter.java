package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedHashMap;
import java.util.List;

public abstract class HeaderFormatter<R> extends AbstractFormatter<R,LinkedHashMap<String, String>>{

    public abstract LinkedHashMap<String, String> formatRows(List<Row> rows) throws JsonProcessingException;
}
