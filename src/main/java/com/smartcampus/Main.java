package com.smartcampus;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.logging.Logger;


public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        // Mount Jersey servlet — serves everything under /api/v1/*
        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/v1/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter(
                "javax.ws.rs.Application",
                SmartCampusApplication.class.getCanonicalName()
        );

        server.setHandler(context);
        server.start();

        LOGGER.info("========================================");
        LOGGER.info(" Smart Campus API running on port " + PORT);
        LOGGER.info(" Base URL: http://localhost:" + PORT + "/api/v1");
        LOGGER.info("========================================");

        server.join();
    }
}
