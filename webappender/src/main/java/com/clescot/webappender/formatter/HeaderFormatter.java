package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface HeaderFormatter {

    Map<String, String> serializeRows(List<Row> rows) throws JsonProcessingException;
}
