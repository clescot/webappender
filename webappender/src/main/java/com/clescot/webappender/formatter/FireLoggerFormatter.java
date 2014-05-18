package com.clescot.webappender.formatter;

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
public class FireLoggerFormatter extends HeaderFormatter<FireLoggerRow> {
    private static final int FIRE_LOGGER_CHUNK_LENGTH = 76;
    private static final int FIRELOGGER_UNIQUE_IDENTIFIER_LENGTH = 8;
    private static final String ERRORS = "errors";
    private static final String LOGS = "logs";
    private static Random random = new Random();

    public static final String REQUEST_HEADER_IDENTIFIER = "X-FireLogger";
    public static final String FIRELOGGER_RESPONSE_HEADER_PREFIX = "Firelogger-";


    public String getJSON(List<Row> rows) {
        Map<String, Object> globalStructure = Maps.newHashMap();

        ArrayList<Object> errors = Lists.newArrayList();
        if (!errors.isEmpty()) {
            globalStructure.put(ERRORS, errors);
        }

        if (!rows.isEmpty()) {
            globalStructure.put(LOGS, getFormatterRows(rows));
        }
        try {
            return objectMapper.writeValueAsString(globalStructure);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected String getRequestHeaderIdentifier() {
        return REQUEST_HEADER_IDENTIFIER;
    }

    @Override
    protected FireLoggerRow newFormatterRow(Row row) {
        return new FireLoggerRow(row);
    }




    @Override
    public LinkedHashMap<String, String> formatRows(List<Row> rows) throws JsonProcessingException {
        LinkedHashMap<String, String> headers = Maps.newLinkedHashMap();
        String prefix = FIRELOGGER_RESPONSE_HEADER_PREFIX + getUniqueIdentifier() + '-';
        String rowsAsJSON = encodeBase64(getJSON(rows));
        List<String> chunks = Splitter.fixedLength(FIRE_LOGGER_CHUNK_LENGTH).splitToList(rowsAsJSON);
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            headers.put(prefix + i, chunk);
        }


        return headers;
    }


    private String getUniqueIdentifier() {
        return new BigInteger(130, random).toString(FIRELOGGER_UNIQUE_IDENTIFIER_LENGTH).substring(0, 9);
    }


}
