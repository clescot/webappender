package com.clescot.webappender.jee;

import com.google.inject.servlet.ServletModule;

public class WebappenderServletModule extends ServletModule{
    @Override
    protected void configureServlets() {
        filter("/*").through(WebAppenderFilter.class);
    }
}
