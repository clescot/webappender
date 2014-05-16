package com.clescot.webappender.formatter;

import java.util.List;
import java.util.Map;

public interface Formatter {

    boolean isActive(Map<String, List<String>> headers);

}
