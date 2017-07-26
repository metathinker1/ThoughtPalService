package com.thoughtpal.model.note;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/*
 *  Purpose: Holds NoteDocumentText's raw text.
 *    Future: manage raw text in Solr
 *
 */

@Data
@Slf4j
public class NoteDocumentText {
	private String  id;		// Same as NoteDocument.id
	private String  workspaceId;

	private String rawText;

}
