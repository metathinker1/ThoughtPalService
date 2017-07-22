package com.thoughtpal.model.tag;

// TODO: Confirm that these function declarations can't be replaced with Lombok annotations
public interface Tag {

    String getObjId();
    void setObjId(String objId);

    String getTagType();
	String getTagLabel();
	String getSummaryText();
	void setSummaryText(String summaryText) ;
	int getStartTextOffset() ;
	void setStartTextOffset(int startTextOffset) ;
	int getEndTextOffset() ;
	void setEndTextOffset(int endTextOffset) ;
	int getTagLength();
	String getNameValuesAsString();
	Object getValue(String name);
	void parseNameValuePairs(String nameValuesStr);

}
