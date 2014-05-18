package com.clescot.webappender.formatter;

import java.util.List;

public abstract class BodyFormatter<R> extends AbstractFormatter<R,String> {
    public abstract String formatRows(List<Row> rows);
}
