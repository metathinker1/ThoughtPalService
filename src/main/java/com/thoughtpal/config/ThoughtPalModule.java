package com.thoughtpal.config;

import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.thoughtpal.client.NoteDocsClient;
import com.thoughtpal.endpoints.DefaultResource;
import com.thoughtpal.func.NoteDocSummaryRenderer;
import com.thoughtpal.func.NoteDocumentParser;
import com.thoughtpal.func.NoteDocumentSummarizer;
import org.eclipse.jetty.servlet.DefaultServlet;

import javax.inject.Singleton;
import java.util.Map;

public class ThoughtPalModule extends ServletModule {

    @Override
    protected void configureServlets() {

        // Jetty Configuration
        bind(DefaultServlet.class).in(Singleton.class);
        Map<String, String> options = Maps.newHashMap();
        serve("/*").with(GuiceContainer.class, options);
    }

    @Provides
    @Singleton
    public DefaultResource getDefaultResource(NoteDocsClient noteDocsClient, NoteDocumentSummarizer noteDocSummarizer) {
        return new DefaultResource(noteDocsClient, noteDocSummarizer);
    }

    @Provides
    @Singleton
    public NoteDocumentSummarizer getNoteDocumentSummarizer(NoteDocumentParser noteDocParser, NoteDocSummaryRenderer noteDocSummRenderer) {
        return new NoteDocumentSummarizer(noteDocParser, noteDocSummRenderer);
    }
}
