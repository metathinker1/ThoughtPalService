package com.thoughtpal.endpoints;


import com.thoughtpal.client.NoteDocsClient;
import com.thoughtpal.func.NoteDocumentParser;
import com.thoughtpal.func.NoteDocumentSummarizer;
import com.thoughtpal.model.notedoc.NoteDocumentText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
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
    @Produces(MediaType.TEXT_XML)
    public String parseNoteDocument(@QueryParam("dir") String directory, @QueryParam("file") String fileName)
    {
        try {
            NoteDocumentText noteDocText = noteDocsClient.getNoteDocument(directory, fileName);
            return noteDocSummarizer.summarizeAndRender(noteDocText);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }

    }

}
