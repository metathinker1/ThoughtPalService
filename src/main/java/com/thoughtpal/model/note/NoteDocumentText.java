package com.thoughtpal.model.note;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/*
 *  Purpose: Holds NoteDocumentText's raw text.
 *    Future: manage raw text in Solr
 *
 */

@Data
//@Slf4j
public class NoteDocumentText {
	private String  workspaceId;
	private String  noteDocumentId;

	private String 	rawText;

	// DRY Violation Optimization: to support scoped search in persistent store
	private NoteDocument	noteDocument;

	@Builder
	public NoteDocumentText(String workspaceId, String noteDocumentId, String rawText) {
	    this.workspaceId = workspaceId;
	    this.noteDocumentId = noteDocumentId;
	    this.rawText = rawText;

	    // TODO: Cleanup
        // String category, String subject, NoteDocumentStructure structure, String workspaceId
        this.noteDocument = NoteDocument.builder().
                subject("Test Subject").structure(NoteDocument.NoteDocumentStructure.Outline).workspaceId(workspaceId).build();
    }

}
