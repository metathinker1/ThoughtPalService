package com.thoughtpal.model.tag;

import lombok.Data;

@Data
public class TextLinkTag extends BaseTag {
	public String getTagType() {
		return "TextLink";
	}

	public String getTagLabel() {
		return "TextLink";
	}

}
