package com.clescot.warexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

@WebServlet(name="DummyTestServlet", displayName="DummyTestServlet",urlPatterns = "/test")
public class DummyTestServlet extends HttpServlet {
    private static Random random = new Random();
    private static Logger LOGGER = LoggerFactory.getLogger(DummyServlet.class);
    private Properties spiels;

    public DummyTestServlet() {
        spiels = new Properties();

        try( InputStream inputStream  =Thread.currentThread().getContextClassLoader().getResourceAsStream("spieldesjahres.properties")) {

            spiels.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String year = "" + (1979 + random.nextInt(35));
        String spiel = spiels.getProperty(year);
        LOGGER.info("into DummyTestServlet");
        LOGGER.info("in {}, spiel des jahres award has been granted to "+spiel,year);
        LOGGER.debug("test debug");
        LOGGER.warn("test warn");
        LOGGER.error("test error");
        LOGGER.trace("test trace");
        getServletConfig().getServletContext().getRequestDispatcher(
                "/test.jsp").forward(req,resp);

    }
}
