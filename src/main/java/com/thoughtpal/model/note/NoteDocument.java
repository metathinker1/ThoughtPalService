package com.thoughtpal.model.note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtpal.model.tag.SourceTag;
import com.thoughtpal.model.tag.Tag;
import org.apache.log4j.Logger;



public class NoteDocument {

	private String				contextName;
	private String				contextType;
	private NoteDocumentType	noteDocumentType;
	private String				workspaceName;
	private List<Note> 			notes = new ArrayList<Note>();
	private Map<String, Note> 	noteMap = new HashMap<String, Note>();

	private String				noteDocumentLocation;
	private boolean				autoOpenEditor;
	
	private int				outlineLevelDisplay = -1;
	private boolean			isDisplayTags = true;
	// NOTE: DESIGN: This is a hack until Note.objectId is persisted; Any Outline structure changes could make this stale
	private Map<String, Integer> 	overrideOutlineLevelDisplayMap	= new HashMap<String, Integer>();
	
	private NoteDocParser	noteDocParser = new NoteDocParser();

    private static Logger logger = Logger.getLogger(NoteDocument.class);

	public enum NoteDocumentType {
		NoteJournal, NoteOutline, NoteSource
	}
	
	public NoteDocument(String contextName, String contextType, NoteDocumentType type, String workspaceName, boolean autoOpenEditor) {
		this.contextName = contextName;
		this.contextType = contextType;
		this.noteDocumentType = type;
		this.workspaceName = workspaceName;
		this.autoOpenEditor = autoOpenEditor;
	}
	
	public void initialize() {
		noteDocParser.initialize();
	}
	
	public String getKey() {
		return getName() + ":" + noteDocumentType.toString();
	}

	public String getName() {
		return contextType + "." + contextName;
	}
	
	public String getContextName() {
		return contextName;
	}
	
	public String getContextType() {
		return contextType;
	}
	
	public NoteDocumentType getNoteDocumentType() {
		return noteDocumentType;
	}
	
	public String getNoteDocumentTypeAbbreviation() {
		switch(noteDocumentType) {
			case NoteJournal: return "Journal";
			case NoteOutline: return "Outline";
			case NoteSource: return "Source";
			default: return "";
		}
	}
	
	public String getWorkspaceName() {
		return workspaceName;
	}
	
	public void addNote(Note note) {
		notes.add(note);
		noteMap.put(note.getObjId(), note);
	}
	
	public List<Note> getNotes() {
		return notes;
	}
	
	public Note getNote(String noteObjId) {
		return noteMap.get(noteObjId);
	}
	
	public void clearNotes() {
		notes.clear();
	}
	
	public String getNoteDocumentLocation() {
		return noteDocumentLocation;
	}
	
	public boolean getAutoOpenEditor() {
		return autoOpenEditor;
	}
	
	public boolean hasNoteDocumentLocation() {
		return noteDocumentLocation != null;
	}

	public void setNoteDocumentLocation(String noteDocumentLocation) {
		this.noteDocumentLocation = noteDocumentLocation;
	}

	public void setTagParseEnabled(boolean isTagParseEnabled) {
		noteDocParser.setTagParseEnabled(isTagParseEnabled);
	}
	
	public void toggleTagParseEnabled() {
		noteDocParser.toggleTagParseEnabled();
	}
	
	public void toggleIsDisplayTags() {
		if (isDisplayTags) isDisplayTags = false;
		else isDisplayTags = true;		
	}

	public void parseNoteJournal(String noteDocText) {
		noteDocParser.parseNoteJournal(this, noteDocText);
	}

	public void parseNoteOutline(String noteDocText) {
		noteDocParser.parseNoteOutline(this, noteDocText);
	}
	
	public void parseNoteSource(String noteDocText) {
		noteDocParser.parseNoteSource(this, noteDocText);
	}
	
	public int getOutlineLevelDisplay() {
		return outlineLevelDisplay;
	}

	// TODO: Cleanup
	public void setOutlineLevelDisplay(int outlineLevelDisplay) {
		if (noteDocumentType == NoteDocumentType.NoteOutline) {
			this.outlineLevelDisplay = outlineLevelDisplay;
			overrideOutlineLevelDisplayMap.clear();
		}
	}
	
	public void expandOutlineLevelDisplay() {
		if (noteDocumentType == NoteDocumentType.NoteOutline) {
//			String location = AppContext.getInstance().getSelectedNote().getLocationTag();
//			overrideOutlineLevelDisplayMap.put(location, location.split("\\.").length + 1);
		}
	}

	public String toXMLMetaViewString() {
		if (noteDocumentType == NoteDocumentType.NoteJournal) {
			return toXMLMetaViewStringJournal();
		} else if (noteDocumentType == NoteDocumentType.NoteOutline) {
			return toXMLMetaViewStringOutline();
		} else if (noteDocumentType == NoteDocumentType.NoteSource) {
			return toXMLMetaViewStringSource();
		} else {
			assert false;
			return null;
		}
	}

	/*	
	buf.append("<a href=\"");
	//buf.append(attrWrpr.getTypeName());
	buf.append("FocusPoint");
	buf.append("=");
	buf.append(attrWrpr.getObjId());		
	buf.append("\"");
	//buf.append(" nowrap=\"true\"");
	buf.append(">");
	buf.append(text);
	buf.append("</a>");
	*/
	
	private String toXMLMetaViewStringOutline() {
		StringBuffer buf = new StringBuffer();
		buf.append("<form>");
        buf.append("<p>");  
        for (Note note : getNotes()) {       	
        	if (isDisplayableOutlineLevel(note.getLocationTag())) {
	        	buf.append("<a href=\"");
	        	buf.append("Expand");
	        	buf.append("=");
	        	buf.append(note.getObjId());		
	        	buf.append("\"");
	        	buf.append(">");
	        	buf.append("+ ");
	        	buf.append("</a>");
	        	
	        	buf.append("<a href=\"");
	        	buf.append("Note");
	        	buf.append("=");
	        	buf.append(note.getObjId());		
	        	buf.append("\"");
	        	//buf.append(" nowrap=\"true\"");
	        	buf.append(">");
	        	buf.append(note.getLocationTag());
	        	buf.append("</a>");
	
	        	//buf.append(note.getLocationTag());
	        	buf.append(": ");
	        	buf.append(convertToSafeText(note.getSummaryText()));
	    		buf.append("<BR></BR>");
	    		buf.append("<BR></BR>");
	    		if (isDisplayTags) {
		    		List<Tag> tags = note.getTags();
		    		for (Tag tag: tags) {
		    			buf.append("    ");
			        	buf.append("<a href=\"");
			        	buf.append("Tag");
			        	buf.append("=");
			        	buf.append(note.getObjId());
			        	buf.append(":");
			        	buf.append(tag.getObjId());
			        	buf.append("\"");
			        	//buf.append(" nowrap=\"true\"");
			        	buf.append(">");
		    			buf.append(tag.getTagLabel());
			        	buf.append("</a>");	
		    			buf.append(": ");
		    			buf.append(convertToSafeText(tag.getSummaryText()));
		    			String nameValuesStr = tag.getNameValuesAsString();
		    			if (nameValuesStr != null && nameValuesStr.length() > 0) {
				    		buf.append("<BR></BR>");
				    		buf.append(nameValuesStr);
				    		buf.append("<BR></BR>");
		    			}
		    			
		    			/*
		    			buf.append(": {");
		    			buf.append(tag.getStartTextPosn());
		    			buf.append(", ");
		    			buf.append(tag.getEndTextPosn());
		    			buf.append("}");
		    			*/
		    			
			    		buf.append("<BR></BR>");
		    		}
	    		}
	    		buf.append("<BR></BR>");
        	}
        }
		buf.append("</p>");
    	buf.append("\n");
		buf.append("</form>");
		return buf.toString();		
	}
	
	private boolean isDisplayableOutlineLevel(String location) {
		String[] levels = location.split("\\.");
		if (overrideOutlineLevelDisplayMap.size() > 0) {
			String key = "";
			int maxOverrideLevel = -1;
			for (int ix = 0; ix < levels.length; ix++) {
				if (ix > 0) key += ".";
				key += levels[ix];
				if (overrideOutlineLevelDisplayMap.containsKey(key)) {
					int overrideLevel = overrideOutlineLevelDisplayMap.get(key);
					maxOverrideLevel = Math.max(maxOverrideLevel, overrideLevel);
				}
			}
			if (maxOverrideLevel > -1) {
				return levels.length <= maxOverrideLevel;
			}
		}
		return outlineLevelDisplay == -1 || levels.length <= outlineLevelDisplay;
	}

	private String toXMLMetaViewStringJournal() {
		StringBuffer buf = new StringBuffer();
		buf.append("<form>");
        buf.append("<p>");  
        for (Note note : getNotes()) {
        	buf.append("<a href=\"");
        	buf.append("Note");
        	buf.append("=");
        	buf.append(note.getObjId());		
        	buf.append("\"");
        	//buf.append(" nowrap=\"true\"");
        	buf.append(">");
        	buf.append(note.getLocationTag());
        	buf.append("</a>");

        	//buf.append(note.getLocationTag());
        	buf.append(": ");
        	buf.append(convertToSafeText(note.getSummaryText()));
    		buf.append("<BR></BR>");
    		buf.append("<BR></BR>");
    		List<Tag> tags = note.getTags();
    		for (Tag tag: tags) {
    			buf.append("    ");
	        	buf.append("<a href=\"");
	        	buf.append("Tag");
	        	buf.append("=");
	        	buf.append(note.getObjId());
	        	buf.append(":");
	        	buf.append(tag.getObjId());
	        	buf.append("\"");
	        	//buf.append(" nowrap=\"true\"");
	        	buf.append(">");
    			buf.append(tag.getTagType());
	        	buf.append("</a>");

    			buf.append(": ");
    			buf.append(convertToSafeText(tag.getSummaryText()));
    			String nameValuesStr = tag.getNameValuesAsString();
    			if (nameValuesStr != null && nameValuesStr.length() > 0) {
		    		buf.append("<BR></BR>");
		    		buf.append(nameValuesStr);
		    		buf.append("<BR></BR>");
    			}

    			/*
    			buf.append(": {");
    			buf.append(tag.getStartTextPosn());
    			buf.append(", ");
    			buf.append(tag.getEndTextPosn());
    			buf.append("}");
    			*/
    			
	    		buf.append("<BR></BR>");
    		}
    		buf.append("<BR></BR>");
        }
		buf.append("</p>");
    	buf.append("<BR></BR>");
		buf.append("</form>");
		return buf.toString();		
	}
	
	private String toXMLMetaViewStringSource() {
		StringBuffer buf = new StringBuffer();
		buf.append("<form>");
        buf.append("<p>");  
        for (Note note : getNotes()) {
        	buf.append("<a href=\"");
        	buf.append("Note");
        	buf.append("=");
        	buf.append(note.getObjId());		
        	buf.append("\"");
        	//buf.append(" nowrap=\"true\"");
        	buf.append(">");
        	buf.append(note.getLocationTag());
        	buf.append("</a>");

        	//buf.append(note.getLocationTag());
        	buf.append(": ");
        	buf.append(convertToSafeText(note.getSummaryText()));
    		buf.append("<BR></BR>");
    		buf.append("<BR></BR>");
    		List<Tag> tags = note.getTags();
    		for (Tag tag: tags) {
    			buf.append("    ");
	        	buf.append("<a href=\"");
	        	buf.append("Tag");
	        	buf.append("=");
	        	buf.append(note.getObjId());
	        	buf.append(":");
	        	buf.append(tag.getObjId());
	        	buf.append("\"");
	        	//buf.append(" nowrap=\"true\"");
	        	buf.append(">");
    			buf.append(tag.getTagType());
	        	buf.append("</a>");

    			buf.append(": ");
    			buf.append(convertToSafeText(tag.getSummaryText()));
    			String nameValuesStr = tag.getNameValuesAsString();
    			if (nameValuesStr != null && nameValuesStr.length() > 0) {
		    		buf.append("<BR></BR>");
		    		buf.append(nameValuesStr);
		    		buf.append("<BR></BR>");
    			}

    			/*
    			buf.append(": {");
    			buf.append(tag.getStartTextPosn());
    			buf.append(", ");
    			buf.append(tag.getEndTextPosn());
    			buf.append("}");
    			*/
	    		buf.append("<BR></BR>");
	    		if (tag instanceof SourceTag) {
	    			buf.append("<BR></BR>");
	    			buf.append("<BR></BR>");
	    		}
    		}
    		buf.append("<BR></BR>");

        }
		buf.append("</p>");
    	buf.append("\n");
		buf.append("</form>");
		return buf.toString();		
	}
	
	public void export() {
		// Need Tags to convert LaTeX math chunks
		
	}

    private String convertToSafeText(String noteText) {
    	// Hack
    	if (noteText == null) {
    		noteText = "";
    	}
        char[] textAsChars = noteText.toCharArray();
        StringBuffer buf = new StringBuffer();
        for (int ix = 0; ix < textAsChars.length; ix++) {
        	char nextChar = textAsChars[ix];
        	if (nextChar == '\n') {
        		buf.append("<BR></BR>");
        	} else if (nextChar == '<') {
        		buf.append("&lt;");
        	} else if (nextChar == '>') {
        		buf.append("&gt;");
        	} else if (nextChar == '&') {
        		buf.append("&amp;");
        	} else{
        		buf.append(nextChar);
        	}       	
        }
        return buf.toString();
    }
    
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(getName());
		strBuf.append(", ");
		strBuf.append(contextName);
		strBuf.append(", ");
		strBuf.append(noteDocumentType);
		strBuf.append(", ");
		strBuf.append(contextType);
		strBuf.append(", ");
		strBuf.append(workspaceName);
		strBuf.append(", ");
		strBuf.append(notes.size());
		if (noteDocumentLocation != null) {
			strBuf.append(", ");
			strBuf.append(noteDocumentLocation);
		}

		return strBuf.toString();
	}


	public static NoteDocumentType convertFromFileType(String fileType) {
		if (fileType.equals("njdoc")) {
			return NoteDocumentType.NoteJournal;
		} else if (fileType.equals("nodoc")) {
			return NoteDocumentType.NoteOutline;
		} else if (fileType.equals("nsdoc")) {
			return NoteDocumentType.NoteSource;
		} else {
			assert false;
			return null;
		}
	}
	
	public static String getFileExtension(NoteDocumentType type) {
		switch (type) {
		case NoteJournal: return ".njdoc";
		case NoteOutline: return ".nodoc";
		case NoteSource: return ".nsdoc";
		default:
			assert false;
			return "";
		}
	}

}
