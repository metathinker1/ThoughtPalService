package com.thoughtpal.controller;

import com.thoughtpal.client.NoteDocsClient;
import com.thoughtpal.model.note.NoteDocParser;
import com.thoughtpal.model.note.NoteDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by robertwood on 2/3/17.
 */
@RestController
public class NoteDocumentController {
    //@Autowired
    private NoteDocsClient  noteDocsClient = new NoteDocsClient();

    //@Autowired
    private NoteDocParser   noteDocParser = new NoteDocParser();

/*
    @RequestMapping(value = "/test", params = {"noteDocName", "bar"}, method = RequestMethod.GET)
    public String test(
            @RequestParam(value = "noteDocName", required = true) String noteDocName,
            @RequestParam(value = "bar", required = false) String bar) {
        return noteDocName + " " + bar + "\n";
    }

    @RequestMapping(value = "/test2", params = {"noteDocName"}, method = RequestMethod.GET)
    public String test2(
            @RequestParam(value = "noteDocName", required = true) String noteDocName) {
        return noteDocName + "\n";
    }

    @RequestMapping(value = "/test3", method = RequestMethod.GET)
    public String test3(
            @RequestParam Map<String,String> requestParams) {
        return requestParams.size() + "\n";
    }
*/

    @RequestMapping(value = "/notedoc", params = {"noteDocName", "workspace"}, method = RequestMethod.GET)
    public String getNoteDocument(@RequestParam("noteDocName") String noteDocName, @RequestParam("workspace") String workspace) {
        String noteDocAsString = noteDocsClient.getNoteDocAsString(noteDocName, workspace);

        //String contextName, String contextType, NoteDocumentType type, String workspaceName, boolean autoOpenEditor
        NoteDocument noteDoc = new NoteDocument(noteDocName, "Outline", NoteDocument.NoteDocumentType.NoteOutline, workspace, false);
        noteDocParser.parseNoteOutline(noteDoc, noteDocAsString);

        // Render noteDoc summaryText into HTML
        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<html><body>");
        htmlString.append("<br>");
        htmlString.append(workspace);
        htmlString.append(": ");
        htmlString.append(noteDocName);
        htmlString.append("<br><br>");
        htmlString.append(noteDoc.toXMLMetaViewString());
        htmlString.append("</body></html>");
        return htmlString.toString();
    }

}
