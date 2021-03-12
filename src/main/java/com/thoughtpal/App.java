package com.thoughtpal;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;
import com.thoughtpal.config.ThoughtPalModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class App
{
    public static void main( String[] args ) throws Exception
    {
        //SpringApplication.run(new Object[] { App.class }, args);
        int port = 5010;

        // TODO: Add Guice here as needed
        Injector injector = Guice.createInjector(
                Stage.PRODUCTION,
                new ThoughtPalModule()
        );

        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));

        context.addServlet(DefaultServlet.class, "/*");

        // https://stackoverflow.com/questions/28190198/cross-origin-filter-with-embedded-jetty
        // https://medium.com/@aruny/embedding-jetty-server-with-jersey-as-restful-container-with-cors-a3ea3c5381c9
        // Add the filter, and then use the provided FilterHolder to configure it
        FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,OPTIONS");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");


        System.out.println("Starting ...");

        server.start();
        server.join();
    }
}
