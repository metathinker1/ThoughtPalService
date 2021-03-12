package com.thoughtpal.endpoints;


import com.thoughtpal.client.NoteDocsClient;
import com.thoughtpal.func.NoteDocumentParser;
import com.thoughtpal.func.NoteDocumentSummarizer;
import com.thoughtpal.model.notedoc.NoteDocumentText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/")
///@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class DefaultResource {

    private final NoteDocsClient         noteDocsClient;
    private final NoteDocumentSummarizer noteDocSummarizer;

    @GET()
    @Path("ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String pong() {
        return "pong";
    }

    @GET()
    @Path("parse")
    @Produces(MediaType.TEXT_PLAIN) // Pass HTML snippet as plain text
    public Response parseNoteDocument(@QueryParam("dir") String directory, @QueryParam("file") String fileName)
    {
        try {
            System.out.println("parseNoteDocument: directory:" + directory + ", fileName:" + fileName);
            NoteDocumentText noteDocText = noteDocsClient.getNoteDocument(directory, fileName);
            String summaryHTML = noteDocSummarizer.summarizeAndRender(noteDocText);
            return Response.ok(summaryHTML).header("Access-Control-Allow-Origin", "*").build();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: How to add message
            return Response.serverError().build();
        }

    }

}
