package com.clescot.webappender;

import com.google.common.collect.Lists;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class WebAppenderFilterTest {

    private ServletTester tester;
    private HttpTester.Request request;

    public static void main(String[] args) throws Exception {
        System.setProperty(WebAppenderFilter.SYSTEM_PROPERTY_KEY,"true");
        Server server = new Server(8080);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        connector.setName("serverConnector");
        ArrayList<ConnectionFactory> factories = Lists.newArrayList();
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setOutputBufferSize(161920);
        httpConfiguration.setResponseHeaderSize(161920);
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);
        factories.add(0, httpConnectionFactory);
        connector.setConnectionFactories(factories);

        server.setConnectors(new Connector[]{connector});
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(HelloServlet.class, "/*");
        FilterHolder filterHolder = new FilterHolder(WebAppenderFilter.class);
        filterHolder.setName("webAppender");
        FilterMapping filterMapping = new FilterMapping();
        filterMapping.setFilterName("webAppender");
        filterMapping.setPathSpec("/*");
        handler.addFilter(filterHolder, filterMapping);

        server.start();
        server.join();
    }

    @SuppressWarnings("serial")
    public static class HelloServlet extends HttpServlet {
        public static Logger LOGGER = LoggerFactory.getLogger(HelloServlet.class);

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            LOGGER.info("test template {}*{}", "a", "b");

            try {
                throw new RuntimeException(" runtimeException");
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello SimpleServlet</h1>");
        }
    }


}
