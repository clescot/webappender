package com.clescot.webappender.formatter;

import java.util.List;

public interface BodyFormatter extends Formatter {
    String getJSON(List<Row> rows);
}
