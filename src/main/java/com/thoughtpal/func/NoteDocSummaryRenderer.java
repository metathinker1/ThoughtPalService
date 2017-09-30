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
        noteDoc.getNoteDocItems().stream()
            //.forEach(System.out::println);
            .forEach(noteDocItem -> {
                if (noteDocItem instanceof Note) {
                    Note note = (Note) noteDocItem;
                    outline.append("<h2>" + note.getLabel() + ": " + note.getSummaryText() + "</h2><br><br>");
                } else {
                    Tag tag = (Tag) noteDocItem;
                    outline.append("<h3>" + tag.getSummaryText() + "</h3><br><br>");
                }
            });
//        outline.append("\n</body></html>");
        return outline.toString();
    }
}
