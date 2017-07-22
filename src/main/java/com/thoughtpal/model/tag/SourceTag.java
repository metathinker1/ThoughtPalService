package com.thoughtpal.model.tag;

import lombok.Data;

@Data
public class SourceTag extends BaseTag {
	public String getTagType() {
		return "SourceTag";
	}
	public String getTagLabel() {
		return "SourceTag";
	}
}
