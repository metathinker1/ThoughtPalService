package com.thoughtpal.model.tag;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class BaseTag implements Tag {

	private String	summaryText;
	private int		startTextPosn;
	private int		endTextPosn;
	private int		objId;
	private Map<String, Object> 	nameValues = new HashMap<String, Object>();

    private static Logger logger = Logger.getLogger(BaseTag.class);

    
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
	public int getEndTextPosn() {
		return endTextPosn;
	}
	public void setEndTextPosn(int endTextPosn) {
		this.endTextPosn = endTextPosn;
	}
	public int getTagLength() {
		return endTextPosn - startTextPosn + 1;
	}

	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	
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
	}
	
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
	}
	
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(summaryText);
		strBuf.append(", ");
		strBuf.append(startTextPosn);
		strBuf.append(", ");
		strBuf.append(endTextPosn);
		strBuf.append(", ");
		strBuf.append(objId);

		return strBuf.toString();
	}
	

	

}
