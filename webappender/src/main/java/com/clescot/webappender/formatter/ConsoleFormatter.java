package com.clescot.webappender.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ConsoleFormatter extends AbstractFormatter<Row>  implements BodyFormatter{

    public static final String REQUEST_HEADER_IDENTIFIER = "X-BodyLogger";



    @Override
    public LinkedHashMap<String, String> formatRows(List<Row> rows) throws JsonProcessingException {
        LinkedHashMap<String, String> result = Maps.newLinkedHashMap();
        if(rows !=null && !rows.isEmpty()) {
            List<Row> formattedRows = getFormatterRows(rows);
            try {

                for (Row row : formattedRows) {
                    if(row.getLevel().isGreaterOrEqual(ch.qos.logback.classic.Level.DEBUG)) {
                        StringBuilder rowDecorated = new StringBuilder();
                        String rowInJSON = objectMapper.writeValueAsString(row);
                        rowDecorated.append("console.");
                        rowDecorated.append(Level.getConsoleLevel(row));
                        rowDecorated.append("(");
                        rowDecorated.append(rowInJSON);
                        rowDecorated.append(");");
                        result.put(rowDecorated.toString(),rowDecorated.toString());
                    }
                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
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
