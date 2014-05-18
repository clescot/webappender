package com.clescot.webappender.jee;

import com.clescot.webappender.filter.FiltersModule;
import com.clescot.webappender.formatter.FormattersModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.annotation.WebListener;

@WebListener
public class WebappenderServletContextListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new WebappenderServletModule(),new FiltersModule(),new FormattersModule());
    }
}
