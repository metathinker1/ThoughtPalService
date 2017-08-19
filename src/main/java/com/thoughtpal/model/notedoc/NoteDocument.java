package com.thoughtpal.model.notedoc;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  TODO: Refactor as Bean; move display functions and state to another class; remove reference to parser
 */
@Data
public class NoteDocument {
    private String  id;
    private String  workspaceId;

    private String  category;
    private String  subject;
    private NoteDocumentStructure   noteDocumentStructure;

    private List<Note>          notes = new ArrayList<>();
    private List<Tag>           tags = new ArrayList<>();
    private Map<String, Note>   noteMap = new HashMap<>();
    private Map<String, Tag>    tagMap = new HashMap<>();
    private List<NoteDocItem>   noteDocItems = new ArrayList<>();

    // DRY Violation Optimization: to support scoped search in persistent store -- Maybe not; maybe double pointer
    private NoteDocumentText    noteDocumentText;

    /* TODO: Move
    private String				noteDocumentLocation;
    private boolean				autoOpenEditor;

    private int				outlineLevelDisplay = -1;
    private boolean			isDisplayTags = true;

    // NOTE: DESIGN: This is a hack until Note.objectId is persisted; Any Outline structure changes could make this stale
    private Map<String, Integer> 	overrideOutlineLevelDisplayMap	= new HashMap<String, Integer>();
    */

    //private static Logger logger = log.getLogger(NoteDocumentText.class);

    public enum NoteDocumentStructure {
        Outline, MeetingJournal, WorkJournal, InfoSource
    }

    @Builder
    public NoteDocument(String category, String subject, NoteDocumentStructure structure, String workspaceId) {
        this.category = category;
        this.subject = subject;
        this.noteDocumentStructure = structure;
        this.workspaceId = workspaceId;
    }

    public void setNotesAndTags(List<Note> notes, List<Tag> tags) {
        setNotes(notes);
        setTags(tags);
        List<NoteDocItem> noteDocItems = new ArrayList<>();
        noteDocItems.addAll(notes);
        noteDocItems.addAll(tags);
        setNoteDocItems(noteDocItems);
    }

    /* TODO: Move
    public String getKey() {
        return getName() + ":" + noteDocumentStructure.toString();
    }*/


    /* TODO: Move
    public int getOutlineLevelDisplay() {
        return outlineLevelDisplay;
    }

    // TODO: Cleanup
    public void setOutlineLevelDisplay(int outlineLevelDisplay) {
        if (noteDocumentStructure == NoteDocumentStructure.NoteOutline) {
            this.outlineLevelDisplay = outlineLevelDisplay;
            overrideOutlineLevelDisplayMap.clear();
        }
    }

    public void expandOutlineLevelDisplay() {
        if (noteDocumentStructure == NoteDocumentStructure.NoteOutline) {
//			String location = AppContext.getInstance().getSelectedNote().getLocationTag();
//			overrideOutlineLevelDisplayMap.put(location, location.split("\\.").length + 1);
        }
    }
    */

    /* TODO: Move
    public String toXMLMetaViewString() {
        if (noteDocumentStructure == NoteDocumentStructure.NoteJournal) {
            return toXMLMetaViewStringJournal();
        } else if (noteDocumentStructure == NoteDocumentStructure.NoteOutline) {
            return toXMLMetaViewStringOutline();
        } else if (noteDocumentStructure == NoteDocumentStructure.NoteSource) {
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
	buf.append(attrWrpr.getId());
	buf.append("\"");
	//buf.append(" nowrap=\"true\"");
	buf.append(">");
	buf.append(text);
	buf.append("</a>");
	*

    // TODO: Refactor: collapse / expand functionality: into the HTML / Javascript paradigm
    // TODO: Rename: toHTMLSnippet() ?
    private String toXMLMetaViewStringOutline() {
        StringBuffer buf = new StringBuffer();
        buf.append("<form>");
        buf.append("<p>");
        for (Note notedoc : getNotes()) {
            if (isDisplayableOutlineLevel(notedoc.getLocationTag())) {
                buf.append("<a href=\"");
                buf.append("Expand");
                buf.append("=");
                buf.append(notedoc.getId());
                buf.append("\"");
                buf.append(">");
                buf.append("+ ");
                buf.append("</a>");

                buf.append("<a href=\"");
                buf.append("Note");
                buf.append("=");
                buf.append(notedoc.getId());
                buf.append("\"");
                //buf.append(" nowrap=\"true\"");
                buf.append(">");
                buf.append(notedoc.getLocationTag());
                buf.append("</a>");

                //buf.append(notedoc.getLocationTag());
                buf.append(": ");
                buf.append(convertToSafeText(notedoc.getSummaryText()));
                buf.append("<BR></BR>");
                ///buf.append("<BR></BR>");
                if (isDisplayTags) {
                    List<Tag> tags = notedoc.getTags();
                    for (Tag tag: tags) {
                        buf.append("    ");
                        buf.append("<a href=\"");
                        buf.append("Tag");
                        buf.append("=");
                        buf.append(notedoc.getId());
                        buf.append(":");
                        buf.append(tag.getId());
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
		    			*

                        buf.append("<BR></BR>");
                    }
                }
                ///buf.append("<BR></BR>");
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
        for (Note notedoc : getNotes()) {
            buf.append("<a href=\"");
            buf.append("Note");
            buf.append("=");
            buf.append(notedoc.getId());
            buf.append("\"");
            //buf.append(" nowrap=\"true\"");
            buf.append(">");
            buf.append(notedoc.getLocationTag());
            buf.append("</a>");

            //buf.append(notedoc.getLocationTag());
            buf.append(": ");
            buf.append(convertToSafeText(notedoc.getSummaryText()));
            buf.append("<BR></BR>");
            buf.append("<BR></BR>");
            List<Tag> tags = notedoc.getTags();
            for (Tag tag: tags) {
                buf.append("    ");
                buf.append("<a href=\"");
                buf.append("Tag");
                buf.append("=");
                buf.append(notedoc.getId());
                buf.append(":");
                buf.append(tag.getId());
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
    			*

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
        for (Note notedoc : getNotes()) {
            buf.append("<a href=\"");
            buf.append("Note");
            buf.append("=");
            buf.append(notedoc.getId());
            buf.append("\"");
            //buf.append(" nowrap=\"true\"");
            buf.append(">");
            buf.append(notedoc.getLocationTag());
            buf.append("</a>");

            //buf.append(notedoc.getLocationTag());
            buf.append(": ");
            buf.append(convertToSafeText(notedoc.getSummaryText()));
            buf.append("<BR></BR>");
            buf.append("<BR></BR>");
            List<Tag> tags = notedoc.getTags();
            for (Tag tag: tags) {
                buf.append("    ");
                buf.append("<a href=\"");
                buf.append("Tag");
                buf.append("=");
                buf.append(notedoc.getId());
                buf.append(":");
                buf.append(tag.getId());
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
    			*
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
        strBuf.append(category);
        strBuf.append(", ");
        strBuf.append(noteDocumentStructure);
        strBuf.append(", ");
        strBuf.append(subject);
        strBuf.append(", ");
        strBuf.append(workspaceId);
        strBuf.append(", ");
        strBuf.append(notes.size());
        if (noteDocumentLocation != null) {
            strBuf.append(", ");
            strBuf.append(noteDocumentLocation);
        }

        return strBuf.toString();
    }


    public static NoteDocumentStructure convertFromFileType(String fileType) {
        if (fileType.equals("njdoc")) {
            return NoteDocumentStructure.NoteJournal;
        } else if (fileType.equals("nodoc")) {
            return NoteDocumentStructure.NoteOutline;
        } else if (fileType.equals("nsdoc")) {
            return NoteDocumentStructure.NoteSource;
        } else {
            assert false;
            return null;
        }
    }

    public static String getFileExtension(NoteDocumentStructure type) {
        switch (type) {
            case NoteJournal: return ".njdoc";
            case NoteOutline: return ".nodoc";
            case NoteSource: return ".nsdoc";
            default:
                assert false;
                return "";
        }
    }
    */
}
