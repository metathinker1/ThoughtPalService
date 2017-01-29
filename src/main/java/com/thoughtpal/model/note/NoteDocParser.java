package com.thoughtpal.model.note;

import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class NoteDocParser {
	
	private boolean	isTagParseEnabled = false;

	//[0-1][0-9]\\.[0-3][0-9]|[0-1][0-9]\\.[0-3][0-9]-[0-1][0-9]\\.[0-3][0-9]
	private Pattern beginJournalNoteChunkPtrn = Pattern.compile("<[1-2][0-9][0-9][0-9]\\.[0-1][0-9]\\.[0-3][0-9] [0-1][0-9]:[0-5][0-9]>");  // <2014.08.29 06:45>
	private Pattern beginOutlineNoteChunkPtrn = Pattern.compile("<Note [0-9]*>");  // <Note 1>
	private Pattern beginSourceNoteChunkPtrn = Pattern.compile("<Note>");  // <Note>
	
	private NoteBodyTextParser	noteBodyTextParser = new NoteBodyTextParser();
	
    private static Logger logger = Logger.getLogger(NoteDocParser.class);
    
    public NoteDocParser() {
    	
    }
    
    public void initialize() {
    	noteBodyTextParser.initialize();
    }

	public void parseNoteJournal(NoteDocument noteDoc, String noteDocText) {
		noteDoc.clearNotes();
		int noteObjId = 0;	// Unique only to NoteDocument
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
				note.setObjId(Integer.toString(noteObjId++));
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

	}
	
	public void parseNoteOutline_TBD(NoteDocument noteDoc, String noteDocText) {
		noteDoc.clearNotes();
		int noteObjId = 0;	// Unique only to NoteDocument
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
				note.setLocationTag(createLocationTag(outlineLocation));
				note.setStartTextPosn(notePosn);
				note.setObjId(Integer.toString(noteObjId++));
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
	}
	
	public void parseNoteOutline(NoteDocument noteDoc, String noteDocText) {
		noteDoc.clearNotes();
		int noteObjId = 0;	// Unique only to NoteDocument
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
				
				line = line.trim();
				note = new Note();
				noteBodyText = new StringBuffer();
				try {
					outlineLevel = Integer.parseInt(line.substring(6, line.length() - 1));
				} catch (NumberFormatException e) {
					logger.error("parseNoteOutline: caught [" + e.getMessage() + "] for Note: [" + note.getSummaryText() + "]");
					// Leave outlineLevel unchanged;
				}
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
				note.setLocationTag(createLocationTag(outlineLocation));
				note.setStartTextPosn(notePosn);
				note.setObjId(Integer.toString(noteObjId++));
				noteDoc.addNote(note);
				parseSummaryText = true;
			} else if (parseSummaryText) {
				parseSummaryText = false;
				note.setSummaryText(line);
				note.setStartSummaryTextPosn(notePosn);
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
	}

	public void parseNoteSource(NoteDocument noteDoc, String noteDocText) {
		noteDoc.clearNotes();
		int noteObjId = 0;	// Unique only to NoteDocument
		int notePosn = 0;
		String[] lines = noteDocText.split("\\n");
		
		Note note = null;
		StringBuffer	noteBodyText = null;
		boolean parseSummaryText = false;
		for (String line : lines) {
			Matcher matcher = beginSourceNoteChunkPtrn.matcher(line);
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
				note.setObjId(Integer.toString(noteObjId++));
				noteDoc.addNote(note);
				parseSummaryText = true;
			} else if (parseSummaryText) {
				parseSummaryText = false;
				note.setSummaryText(line);
				note.setStartSummaryTextPosn(notePosn);
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

	}	
	
	public void setTagParseEnabled(boolean isTagParseEnabled) {
		this.isTagParseEnabled = isTagParseEnabled;
	}
	
	public void toggleTagParseEnabled() {
		if (isTagParseEnabled) isTagParseEnabled = false;
		else isTagParseEnabled = true;
	}
	
	private String createLocationTag(Stack<Integer> outlineLocation) {
		StringBuffer strBuf = null; 
		Iterator<Integer> parts = outlineLocation.iterator();
		while(parts.hasNext()) {
			if (strBuf == null) {
				strBuf = new StringBuffer(String.valueOf(parts.next()));
			} else {
				strBuf.append(".");
				strBuf.append(String.valueOf(parts.next()));
			}
		}
		return strBuf.toString();
	}

}
