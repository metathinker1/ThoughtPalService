package com.thoughtpal.model.tag;


public class TextTag extends BaseTag {
	
	private String	text;			// Convenience

	public TextTag() {} 

	public String getTagType() {
		return "TextTag";
	}

	public String getTagLabel() {
		return "TextTag";
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	
	public static void main(String[] args) {
		TextTag tester = new TextTag();
		String test01 = "foo=bar";
		System.out.println("Test: " + test01);
		tester.setSummaryText(test01);

		String test02 = "foo = bar";
		System.out.println("Test: " + test02);
		tester.setSummaryText(test02);

		String test03 = "foo1=bar1 foo2=bar2";
		System.out.println("Test: " + test03);
		tester.setSummaryText(test03);

		String test04 = "foo1 = bar1 foo2 = bar2";
		System.out.println("Test: " + test04);
		tester.setSummaryText(test04);

	}
}
