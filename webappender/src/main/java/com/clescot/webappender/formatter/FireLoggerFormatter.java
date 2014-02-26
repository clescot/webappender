package com.clescot.webappender.formatter;

import ch.qos.logback.classic.pattern.DateConverter;
import ch.qos.logback.classic.pattern.FileOfCallerConverter;
import ch.qos.logback.classic.pattern.LineOfCallerConverter;
import com.clescot.webappender.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigInteger;
import java.util.*;

/**
 * @see <a href="https://github.com/binaryage/firelogger/wiki">fireLogger wiki</a>
 * @see <a href="http://firelogger-php-tests.binaryage.com/basic.php">fireLogger test page</a>
 */
public class FireLoggerFormatter extends AbstractFormatter {
    public static final int FIRE_LOGGER_CHUNK_LENGTH = 76;
    public static final int FIRELOGGER_UNIQUE_IDENTIFIER_LENGTH = 8;
    private static Random random = new Random();

    private DateConverter dateConverter = new DateConverter();
    private FileOfCallerConverter fileOfCallerConverter = new FileOfCallerConverter();
    private LineOfCallerConverter lineOfCallerConverter = new LineOfCallerConverter();
    public static final String REQUEST_HEADER_IDENTIFIER = "X-FireLogger";
    public static final String FIRELOGGER_RESPONSE_HEADER_PREFIX = "Firelogger-";


    public FireLoggerFormatter() {
        dateConverter.start();
    }

    @Override
    protected String getJSON(List<Row> rows) throws JsonProcessingException {
        Map<String, Object> globalStructure = Maps.newHashMap();

        ArrayList<Object> errors = Lists.newArrayList();
        if (!errors.isEmpty()) {
            globalStructure.put("errors", errors);
        }

        if (!rows.isEmpty()) {
            globalStructure.put("logs", rows);
        }
        return objectMapper.writeValueAsString(globalStructure);

    }


    @Override
    public String getRequestHeaderId() {
        return REQUEST_HEADER_IDENTIFIER;
    }

    @Override
    public Map<String, String> getHeadersAsMap(List<Row> rows) {
        HashMap<String, String> headers = Maps.newHashMap();
        String prefix = FIRELOGGER_RESPONSE_HEADER_PREFIX + getUniqueIdentifier() + '-';
        try {
            String rowsAsJSON = format(rows);
            List<String> chunks = Splitter.fixedLength(FIRE_LOGGER_CHUNK_LENGTH).splitToList(rowsAsJSON);
            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);
                headers.put(prefix + i, chunk);

            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }


        return headers;
    }


    private String getUniqueIdentifier() {
        return new BigInteger(130, random).toString(FIRELOGGER_UNIQUE_IDENTIFIER_LENGTH).substring(0, 9);
    }



}
