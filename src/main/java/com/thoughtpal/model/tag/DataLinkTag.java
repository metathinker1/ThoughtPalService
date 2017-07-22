package com.thoughtpal.model.tag;

import lombok.Data;

@Data
public class DataLinkTag extends BaseTag {
	public String getTagType() {
		return "DataLink";
	}
	public String getTagLabel() {
		return "DataLink";
	}
}
