package com.thoughtpal.func;

import com.thoughtpal.model.note.Note;
import com.thoughtpal.model.note.NoteDocumentText;

import java.util.List;
import java.util.regex.Matcher;
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

        Note note = null;
        StringBuffer	noteBodyText = null;
        boolean parseSummaryText = false;
        for (String line : lines) {
            Matcher matcher = beginJournalNoteChunkPtrn.matcher(line);
            if (matcher.find()) {
                if (note != null) {
                    note.setEndTextPosn(notePosn - 1);
                    noteBodyTextParser.parseNoteBodyText(note, noteBodyText.toString());
                }
                line = line.trim();
                note = new Note();
                noteBodyText = new StringBuffer();
                note.setLocationTag(line.substring(1, line.length() - 1));
                note.setStartTextPosn(notePosn);
                note.setId(Integer.toString(noteObjId++));
                noteDoc.addNote(note);
                parseSummaryText = true;
            } else if (parseSummaryText) {
                parseSummaryText = false;
                note.setSummaryText(line);
                note.setStartSummaryTextPosn(notePosn);

            } else {
                // Case: text preceeding first Note;  In the future this could also be "free text" between Notes
                if (noteBodyText != null) {
                    noteBodyText.append(line);
                    noteBodyText.append("\n");
                }
            }
            notePosn += line.length() + 1;
        }
        if (note != null) {
            note.setEndTextPosn(notePosn);
            noteBodyTextParser.parseNoteBodyText(note, noteBodyText.toString());
        }
        */
        return null;
    }
}
