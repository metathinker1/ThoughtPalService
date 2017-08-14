package com.thoughtpal.model.tag;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;


@Data
public class Tag {

	// Initial implementation: Reset each time the associated Note is parsed
	//   TODO: Refactor to be immutable AFTER Metadata Aware Editor has been implemented
	private String  id;
	private String  workspaceId;

    private TagType tagType;
	private int		startTextOffset;
	private int		endTextOffset;

    // DRY Violation Optimization: to support scoped search in persistent store
	private String	summaryText;

	private Map<String, String> 	nameValues = new HashMap<String, String>();

	//private static Logger logger = Logger.getLogger(Tag.class);

    public enum TagType {
        DataTag, DataLink, TextTag, TextLink, Task, SourceTag
    }

    @Builder
	public Tag(String workspaceId, TagType tagType, int startTextOffset) {
    	this.workspaceId = workspaceId;
    	this.tagType = tagType;
    	this.startTextOffset = startTextOffset;
	}

	/* Backlog:
	public Object getValue(String name) {
		return nameValues.get(name);
	}

	public String getNameValuesAsString() {
		StringBuffer strBuf = new StringBuffer();
		if (nameValues.size() > 0) {
			strBuf.append("[");
			boolean isFirst = true;
			for (Map.Entry<String, Object> entry : nameValues.entrySet()) {
				if (isFirst) {
					isFirst = false;
				} else {
					strBuf.append(",");
				}
				strBuf.append(entry.getKey());
				strBuf.append(" = ");
				strBuf.append(entry.getValue());
			}
			strBuf.append("]");
		}
		return strBuf.toString();
	}*/


	/* TODO: Move:
	public void parseNameValuePairs(String nameValuesStr) {
		String[] parts = nameValuesStr.split("[ ]*=[ ]*");
		String name = null;
		String value = null;
		for (int ix = 0; ix < parts.length; ix++) {
			if (ix > 0) {
				int pipePosnValue = parts[ix].lastIndexOf('|');
				int spacePosnValue = parts[ix].lastIndexOf(' ');
				if (pipePosnValue > 0) {
					value = parts[ix].substring(0, pipePosnValue).trim();
				} else if (spacePosnValue > 0) {
					value = parts[ix].substring(0, spacePosnValue).trim();
				} else {
					value = parts[ix].trim();
				}
			}
			if (name != null && value != null) {
				nameValues.put(name, value);
				System.out.println(ix + ": name (" + name + ") value (" + value + ")");
			}
			int spacePosnName = parts[ix].lastIndexOf(' ');
			if (spacePosnName > 0) {
				name = parts[ix].substring(spacePosnName, parts[ix].length()).trim();
			} else {
				name = parts[ix].trim();
			}

			//System.out.println(ix + " (" + parts[ix] + ")");
		}
	}*/
	


	/*
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(summaryText);
		strBuf.append(", ");
		strBuf.append(startTextPosn);
		strBuf.append(", ");
		strBuf.append(endTextPosn);
		strBuf.append(", ");
		strBuf.append(id);

		return strBuf.toString();
	}
	*/

	

}
