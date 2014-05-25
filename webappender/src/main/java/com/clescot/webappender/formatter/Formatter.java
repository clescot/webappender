package com.clescot.webappender.formatter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface Formatter {

    boolean isActive(Map<String, List<String>> headers);

    public abstract LinkedHashMap<String, String> formatRows(List<Row> rows,int limit);


}
