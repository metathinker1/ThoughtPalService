package com.thoughtpal.model.tag;

public class TaskTag extends BaseTag {
	public String getTagType() {
		return "TaskTag";
	}
	
	public String getTagLabel() {
		// TODO: use Task status ..
		return "TODO";
	}

}
