package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.List;

public class ConsoleFormatter extends BodyFormatter<Row>  {

    public static final String REQUEST_HEADER_IDENTIFIER = "X-BodyLogger";
    public static final String RESPONSE_BODY_LOGGER_HEADER = "Bodylogger-";
    private static final String SCRIPT_TYPE_TEXT_JAVASCRIPT_START = "<script type=\"text/javascript\">";
    private static final String SCRIPT_END = "</script>";

    @Override
    public String serializeRows(List<Row> rows) {
        StringBuilder result = new StringBuilder();
        if(rows !=null && !rows.isEmpty()) {
            List<Row> formattedRows = getFormatterRows(rows);
            try {
                result.append(SCRIPT_TYPE_TEXT_JAVASCRIPT_START);
                for (Row row : formattedRows) {
                    if(row.getLevel().isGreaterOrEqual(ch.qos.logback.classic.Level.DEBUG)) {
                        String rowInJSON = objectMapper.writeValueAsString(row);
                        result.append("console.");
                        result.append(Level.getConsoleLevel(row));
                        result.append("(");
                        result.append(rowInJSON);
                        result.append(");");
                    }
                }
                result.append(SCRIPT_END);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

    protected enum Level{
        OFF("", ch.qos.logback.classic.Level.OFF),
        DEBUG("debug", ch.qos.logback.classic.Level.DEBUG),
        INFO("info",ch.qos.logback.classic.Level.INFO),
        WARN("warn",ch.qos.logback.classic.Level.WARN),
        ERROR("error",ch.qos.logback.classic.Level.ERROR),
        ALL("error", ch.qos.logback.classic.Level.ALL);

        private final String consoleLevel;
        private final ch.qos.logback.classic.Level logbackLevel;


        Level(String consoleLevel, ch.qos.logback.classic.Level logbackLevel) {
            this.consoleLevel = consoleLevel;
            this.logbackLevel = logbackLevel;
        }

        private static String getConsoleLevel(final Row row){
            final ch.qos.logback.classic.Level rowLevel = row.getLevel();
            List<Level> levels = Arrays.asList(Level.values());
            Level level = Iterables.find(levels, new Predicate<Level>() {
                @Override
                public boolean apply(Level input) {
                    return input.getLogbackLevel().equals(rowLevel);
                }
            },Level.OFF);
            return level.getConsoleLevel();
        }

        public String getConsoleLevel() {
            return consoleLevel;
        }

        public ch.qos.logback.classic.Level getLogbackLevel() {
            return logbackLevel;
        }
    }

    @Override
    protected String getRequestHeaderIdentifier() {
        return REQUEST_HEADER_IDENTIFIER;
    }

    @Override
    protected Row newFormatterRow(Row row) {
        return row;
    }

}
