package com.thoughtpal.func;

import com.thoughtpal.model.note.Note;
import com.thoughtpal.model.note.NoteDocumentText;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutlineNoteParser implements  NoteParser {

    private Pattern beginOutlineNoteChunkPtrn = Pattern.compile("<Note [0-9]*>");  // <Note 1>


    public List<Note> parse(NoteDocumentText noteDocText) {
        int noteObjId = 0;    // Unique only to NoteDocumentText
        int noteOffset = 0;
        String[] lines = noteDocText.getRawText().split("\\n");

        List<Note> notes = new ArrayList<Note>();
        Note note = null;
        StringBuffer noteBodyText = null;
        boolean parseSummaryText = false;
        Stack<Integer> outlineLocation = new Stack<Integer>();
        int outlineLevel = 0;
        for (String line : lines) {
            Matcher matcher = beginOutlineNoteChunkPtrn.matcher(line);
            if (matcher.find()) {
                if (note != null) {
                    note.setEndNoteOffset(noteOffset - 1);
                    //noteBodyTextParser.parseNoteBodyText(note, noteBodyText.toString());
                }

                line = line.trim();
                note = Note.builder().workspaceId(noteDocText.getWorkspaceId())
                        .startNoteOffset(noteOffset).summaryText(line).build();
                noteBodyText = new StringBuffer();
                try {
                    outlineLevel = Integer.parseInt(line.substring(6, line.length() - 1));
                } catch (NumberFormatException e) {
                    String msg = "parseNoteOutline: caught [" + e.getMessage() + "] for Note: [" + note.getSummaryText() + "]";
                    System.out.println("ERROR: " + msg);
                    // Leave outlineLevel unchanged;
                }
                if (outlineLevel > outlineLocation.size()) {
                    outlineLocation.push(1);
                } else if (outlineLevel < outlineLocation.size()) {
                    int numLevels = outlineLocation.size() - outlineLevel;
                    for (int ix = 0; ix < numLevels; ix++) outlineLocation.pop();
                    Integer val = outlineLocation.pop();
                    outlineLocation.push(val + 1);
                } else {
                    Integer val = outlineLocation.pop();
                    outlineLocation.push(val + 1);
                }
                note.setLabel(createLabel(outlineLocation));
                note.setId(Integer.toString(noteObjId++));
                notes.add(note);
                parseSummaryText = true;
            } else if (parseSummaryText) {
                parseSummaryText = false;
                note.setSummaryText(line);
                //note.setStartSummaryTextPosn(noteOffset);
                // note.setStartTextPosn(endTextPosn)

            } else {
                // Case: text preceeding first Note;  In the future this could also be "free text" between Notes
                if (noteBodyText != null) {
                    noteBodyText.append(line);
                    noteBodyText.append("\n");
                }
            }
            noteOffset += line.length() + 1;
        }
        if (note != null) {
            note.setEndNoteOffset(noteOffset);
            //noteBodyTextParser.parseNoteBodyText(note, noteBodyText.toString());
        }
        return notes;
    }

    // TODO: Rename: noteLabel ??
    private String createLabel(Stack<Integer> outlineLocation) {
        StringBuffer strBuf = null;
        Iterator<Integer> parts = outlineLocation.iterator();
        while (parts.hasNext()) {
            if (strBuf == null) {
                strBuf = new StringBuffer(String.valueOf(parts.next()));
            } else {
                strBuf.append(".");
                strBuf.append(String.valueOf(parts.next()));
            }
        }
        return strBuf.toString();
    }

	/*
	public void parseNoteOutline_TBD(NoteDocumentText noteDoc, String noteDocText) {
		noteDoc.clearNotes();
		int noteObjId = 0;	// Unique only to NoteDocumentText
		int notePosn = 0;
		String[] lines = noteDocText.split("\\n");

		Note note = null;
		StringBuffer	noteBodyText = null;
		boolean parseSummaryText = false;
		Stack<Integer> outlineLocation = new Stack<Integer>();
		int outlineLevel = 0;
		for (String line : lines) {
			Matcher matcher = beginOutlineNoteChunkPtrn.matcher(line);
			if (matcher.find()) {
				if (note != null) {
					note.setEndTextPosn(notePosn - 1);
					noteBodyTextParser.parseNoteBodyText(note, noteBodyText.toString());
				}
				note = new Note();
				noteBodyText = new StringBuffer();
				outlineLevel = Integer.parseInt(line.substring(6, line.length() - 1));
				if (outlineLevel > outlineLocation.size()) {
					outlineLocation.push(1);
				} else if (outlineLevel < outlineLocation.size()) {
					int numLevels = outlineLocation.size() - outlineLevel;
					for (int ix = 0; ix < numLevels; ix++) outlineLocation.pop();
					Integer val = outlineLocation.pop();
					outlineLocation.push(val+1);
				} else {
					Integer val = outlineLocation.pop();
					outlineLocation.push(val+1);
				}
				note.setLocationTag(createLabel(outlineLocation));
				note.setStartTextPosn(notePosn);
				note.setId(Integer.toString(noteObjId++));
				noteDoc.addNote(note);
				parseSummaryText = true;
			} else if (parseSummaryText) {
				parseSummaryText = false;
				note.setSummaryText(line);
				// note.setStartTextPosn(endTextPosn)

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
	}*/

}