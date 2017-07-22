package com.thoughtpal.model.tag;

import lombok.Data;

@Data
public class DataTag extends BaseTag {
	public String getTagType() {
		return "DataTag";
	}
	public String getTagLabel() {
		return "DataTag";
	}
}
