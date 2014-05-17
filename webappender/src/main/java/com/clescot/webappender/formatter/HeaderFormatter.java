package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public abstract class HeaderFormatter<R> extends AbstractFormatter<R,Map<String, String>>{

    public abstract Map<String, String> serializeRows(List<Row> rows) throws JsonProcessingException;
}
