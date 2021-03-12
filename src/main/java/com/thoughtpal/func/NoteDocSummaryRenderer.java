package com.thoughtpal.func;

import com.thoughtpal.model.notedoc.Note;
import com.thoughtpal.model.notedoc.NoteDocument;
import com.thoughtpal.model.notedoc.Tag;

public class NoteDocSummaryRenderer {

    public String render(NoteDocument noteDoc)
    {
        /*
        *  Design:
        *    Return HTML String
        *    <h2>Note.summaryText()</h2>; <h3>Tag.summaryText()</h3>
        */
        StringBuilder outline = new StringBuilder();
//        outline.append("<!DOCTYPE html><html lang=\"en\"><head><title>Example 01 - Outline</title><link rel=\"stylesheet\" type=\"text/css\" href=\"./css/outlinestyle.css\"></head>");
//        outline.append("<body>");
        String styleTags = "<style type = \"text/css\">h2 {font-family: \"arial\";} h3 {font-family: \"Avenir\";} p {font-family: \"Avenir\";}  .tab { margin-left: 40px; }</style>";
        outline.append("<head><title>"+noteDoc.getFilePath()+"</title>" + styleTags + "</head>");
        noteDoc.getNoteDocItems().stream()
            //.forEach(System.out::println);
            .forEach(noteDocItem -> {
                if (noteDocItem instanceof Note) {
                    Note note = (Note) noteDocItem;
                    outline.append("<p>" + note.getLabel() + ": " + note.getSummaryText() + "<p>");
                    //outline.append("<h2 style=\"font-family:verdana\">" + note.getLabel() + ": " + note.getSummaryText() + "</h2>");
                } else {
                    Tag tag = (Tag) noteDocItem;
                    outline.append("<p class=\"tab\">" + tag.getSummaryText() + "<p>");
                }
            });
//        outline.append("\n</body></html>");
        return outline.toString();
    }
}
