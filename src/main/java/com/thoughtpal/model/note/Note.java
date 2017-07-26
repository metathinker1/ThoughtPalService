package com.thoughtpal.model.note;

import lombok.Data;


/*
 *  Purpose: Holds metadata for a Note within a NoteDocumentText
 *
 */

@Data
public class Note {

	// Initial implementation: Reset each time the associated Note is parsed
	//   TODO: Refactor to be immutable AFTER Metadata Aware Editor has been implemented
	private String id;
    private String  workspaceId;

	private int		startNoteOffset;
	private int		endNoteOffset;
	private int		startSummaryTextOffset;
	private int		endSummaryTextOffset;

	// DRY Violation Optimization: to support scoped search in persistent store
	private String	summaryText;

	
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
