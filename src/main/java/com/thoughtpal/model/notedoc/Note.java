package com.thoughtpal.model.notedoc;

import lombok.Builder;
import lombok.Data;


/*
 *  Purpose: Holds metadata for a Note within a NoteDocumentText
 *
 */

@Data
public class Note implements NoteDocItem {

	// Initial implementation: Reset each time the associated Note is parsed
	//   TODO: Refactor to be immutable AFTER Metadata Aware Editor has been implemented
	private String  id;
    private String  workspaceId;

	private Integer 	startOffset;
	private Integer 	endOffset;
	private String  label;

	// DRY Violation Optimization: to support scoped search in persistent store
	private String	summaryText;

	// Usage: NoteParser
    @Builder
    public Note(String workspaceId, int startNoteOffset, String summaryText) {
        this.workspaceId = workspaceId;
        this.startOffset = startNoteOffset;
        this.summaryText = summaryText;
    }

    @Builder
	public Note(String workspaceId, int startNoteOffset, int endNoteOffset, String summaryText) {
	    this.workspaceId = workspaceId;
	    this.startOffset = startNoteOffset;
	    this.endOffset = endNoteOffset;
	    this.summaryText = summaryText;
    }
	
	/*
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(locationTag);
		strBuf.append(", ");
		strBuf.append(summaryText);
		strBuf.append(", ");
		strBuf.append(id);
		strBuf.append(", ");

		return strBuf.toString();
	}*/
	
}
