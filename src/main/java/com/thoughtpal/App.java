package com.thoughtpal;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;
import com.thoughtpal.config.ThoughtPalModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

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
        System.out.println("Starting ...");

        server.start();
        server.join();
    }
}
