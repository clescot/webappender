package com.clescot.webappender.filter;

import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.EvaluatorFilter;

public class JaninoEventEvaluatorBuilder extends AbstractMatcherFilterBuilder<EvaluatorFilter<ILoggingEvent>> {

    public static final String X_JANINO_FILTER = "X-wa-janino-filter";
    private static final String FILTER_JANINO_EXPRESSION_PROPERTY = "expression";


    @Override
    protected EvaluatorFilter<ILoggingEvent> newFilter() {
        return new EvaluatorFilter<>();
    }

    @Override
    protected String getFilterHeader() {
        return X_JANINO_FILTER;
    }

    @Override
    protected void handleCustomValue(EvaluatorFilter filter, String key, String value) {
        if (key.startsWith(FILTER_JANINO_EXPRESSION_PROPERTY)) {
            JaninoEventEvaluator evaluator = new JaninoEventEvaluator();
            filter.setEvaluator(evaluator);
            evaluator.setExpression(value);
        }
    }


}
