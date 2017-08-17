package com.thoughtpal.func;

import com.thoughtpal.model.notedoc.Note;
import com.thoughtpal.model.notedoc.NoteDocumentText;

import java.util.List;
import java.util.regex.Pattern;

public class MeetingJournalNoteParser implements  NoteParser {

    //[0-1][0-9]\\.[0-3][0-9]|[0-1][0-9]\\.[0-3][0-9]-[0-1][0-9]\\.[0-3][0-9]
    private Pattern beginJournalNoteChunkPtrn = Pattern.compile("<[1-2][0-9][0-9][0-9]\\.[0-1][0-9]\\.[0-3][0-9] [0-1][0-9]:[0-5][0-9]>");  // <2014.08.29 06:45>

    public List<Note> parse(NoteDocumentText noteDocumentText) {
        /*
        noteDoc.clearNotes();
        int noteObjId = 0;	// Unique only to NoteDocumentText
        int notePosn = 0;
        String[] lines = noteDocText.split("\\n");

        Note notedoc = null;
        StringBuffer	noteBodyText = null;
        boolean parseSummaryText = false;
        for (String line : lines) {
            Matcher matcher = beginJournalNoteChunkPtrn.matcher(line);
            if (matcher.find()) {
                if (notedoc != null) {
                    notedoc.setEndTextPosn(notePosn - 1);
                    noteBodyTextParser.parseNoteBodyText(notedoc, noteBodyText.toString());
                }
                line = line.trim();
                notedoc = new Note();
                noteBodyText = new StringBuffer();
                notedoc.setLocationTag(line.substring(1, line.length() - 1));
                notedoc.setStartTextPosn(notePosn);
                notedoc.setId(Integer.toString(noteObjId++));
                noteDoc.addNote(notedoc);
                parseSummaryText = true;
            } else if (parseSummaryText) {
                parseSummaryText = false;
                notedoc.setSummaryText(line);
                notedoc.setStartSummaryTextPosn(notePosn);

            } else {
                // Case: text preceeding first Note;  In the future this could also be "free text" between Notes
                if (noteBodyText != null) {
                    noteBodyText.append(line);
                    noteBodyText.append("\n");
                }
            }
            notePosn += line.length() + 1;
        }
        if (notedoc != null) {
            notedoc.setEndTextPosn(notePosn);
            noteBodyTextParser.parseNoteBodyText(notedoc, noteBodyText.toString());
        }
        */
        return null;
    }
}
