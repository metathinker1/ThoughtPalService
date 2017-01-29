package com.thoughtpal.model.note;

import com.thoughtpal.model.tag.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Note {
	  
	private String	summaryText;
	private int		startTextPosn;
	private int		startSummaryTextPosn;
	private int		endTextPosn;
	private String	locationTag;	// TODO: Refactor as class
	private String		objId;
	
	// TODO: When objId is persisted then move this to NoteDocument: Map<String, Boolean> noteDisplayMap
	private boolean isDisplayInOutline;
	
	private List<Tag> 	tags = new ArrayList<Tag>();
	
	public String getSummaryText() {
		return summaryText;
	}
	public void setSummaryText(String summaryText) {
		this.summaryText = summaryText;
	}
	public int getStartTextPosn() {
		return startTextPosn;
	}
	public void setStartTextPosn(int startTextPosn) {
		this.startTextPosn = startTextPosn;
	}
	public int getStartSummaryTextPosn() {
		return startSummaryTextPosn;
	}
	public void setStartSummaryTextPosn(int startSummaryTextPosn) {
		this.startSummaryTextPosn = startSummaryTextPosn;
	}
	public int getEndTextPosn() {
		return endTextPosn;
	}
	public void setEndTextPosn(int endTextPosn) {
		this.endTextPosn = endTextPosn;
	}
	public int getNoteLength() {
		return endTextPosn - startTextPosn + 1;
	}
	public String getLocationTag() {
		return locationTag;
	}
	public void setLocationTag(String locationTag) {
		this.locationTag = locationTag;
	}
	
	public String getObjId() {
		return objId;
	}
	public void setObjId(String objId) {
		this.objId = objId;
	}
	
	public void addTag(Tag tag) {
		tags.add(tag);
	}
	public List<Tag> getTags() {
		return tags;
	}
	
	public Tag getTag(int tagId) {
		assert tagId >= 0;
		assert tagId < tags.size();
		return tags.get(tagId);
	}
	
	
	public boolean isDisplayInOutline() {
		return isDisplayInOutline;
	}
	
	public void setDisplayInOutline(boolean isDisplayInOutline) {
		this.isDisplayInOutline = isDisplayInOutline;
	}
	
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(locationTag);
		strBuf.append(", ");
		strBuf.append(summaryText);
		strBuf.append(", ");
		strBuf.append(objId);
		strBuf.append(", ");

		return strBuf.toString();
	}
	
}
