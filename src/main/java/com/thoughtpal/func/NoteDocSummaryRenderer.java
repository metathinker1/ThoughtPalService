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
         noteDoc.getNoteDocItems().stream()
                //.forEach(System.out::println);
                .forEach(noteDocItem -> {
                    if (noteDocItem instanceof Note) {
                        Note note = (Note) noteDocItem;
                        outline.append(note.getLabel() + ": " + note.getSummaryText() + "\n\n");
                    } else {
                        Tag tag = (Tag) noteDocItem;
                        outline.append(tag.getSummaryText() + "\n\n");
                    }
                });
        return outline.toString();
    }
}
