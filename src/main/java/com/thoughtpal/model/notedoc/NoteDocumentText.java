package com.thoughtpal.model.notedoc;

import lombok.Builder;
import lombok.Data;


/*
 *  Purpose: Holds NoteDocumentText's raw text.
 *    Future: manage raw text in Solr
 *
 */

@Data
//@Slf4j
public class NoteDocumentText {
	private final String  workspaceId;
	private final String  noteDocumentId;

	private final String 	rawText;

	// DRY Violation Optimization: to support scoped search in persistent store
	// TODO: Design: Consider: This breaks Functional Paradigm, so is it useful?
    //         Better to use common noteDocumentId in related NoteDocumentText and NoteDocument objects ?
    //         Allows NoteDocumentText to be Immutable after initial construction
	private NoteDocument	noteDocument;

	@Builder
	public NoteDocumentText(String filePath, String workspaceId, String noteDocumentId, String rawText) {
	    this.workspaceId = workspaceId;
	    this.noteDocumentId = noteDocumentId;
	    this.rawText = rawText;

	    // TODO: Cleanup
        // String category, String subject, NoteDocumentStructure structure, String workspaceId
        this.noteDocument = NoteDocument.builder()
				.filePath(filePath)
				.subject("Test Subject")
				.structure(NoteDocument.NoteDocumentStructure.Outline)
				.workspaceId(workspaceId)
				.build();
    }

}
