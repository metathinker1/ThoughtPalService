package com.thoughtpal.model.note;

import com.thoughtpal.util.ObjectNotFoundException;
import com.thoughtpal.model.tag.Tag;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 *  Purpose: Holds metadata for a Note within a NoteDocument
 *
 */

@Data
public class Note {

	// Initial implementation: Reset each time the associated Note is parsed
	//   TODO: Refactor to be immutable AFTER Metadata Aware Editor has been implemented
	private String		objId;

	private int		startNoteOffset;
	private int		endNoteOffset;
	private int		startSummaryTextOffset;
	private Map<String, Tag> 	tagMap = new HashMap<String, Tag>();

	// TODO: Review these:
	private String	locationTag;
	private int     startSummaryTextPosn;
    private int     startTextPosn;
    private int     endTextPosn;

	// DRY Violation Optimization: to support scoped search in persistent store
	private String	summaryText;
	private List<Tag>   tags = new ArrayList<Tag>();



	// TODO: When objId is persisted then move this to NoteDocument: Map<String, Boolean> noteDisplayMap
	//private boolean isDisplayInOutline;
	

	public void addTag(Tag tag) {
		tagMap.put(tag.getObjId(), tag);
	}

	public Tag getTag(String tagId) throws ObjectNotFoundException {
		Tag tag = tagMap.get(tagId);
		if (tag != null) {
			return tag;
		} else {
			throw new ObjectNotFoundException("Tag: " + tagId);
		}
	}
	
	/*
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(locationTag);
		strBuf.append(", ");
		strBuf.append(summaryText);
		strBuf.append(", ");
		strBuf.append(objId);
		strBuf.append(", ");

		return strBuf.toString();
	}*/
	
}
