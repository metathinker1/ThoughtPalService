package com.thoughtpal.model.tag;

public interface Tag {

	String getTagType();
	String getTagLabel();
	String getSummaryText();
	void setSummaryText(String summaryText) ;
	int getStartTextPosn() ;
	void setStartTextPosn(int startTextPosn) ;
	int getEndTextPosn() ;
	void setEndTextPosn(int endTextPosn) ;
	int getTagLength();
	int getObjId();
	void setObjId(int objId);
	String getNameValuesAsString();
	Object getValue(String name);
	void parseNameValuePairs(String nameValuesStr);
}
