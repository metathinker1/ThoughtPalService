package com.thoughtpal.model.note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtpal.model.tag.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import lombok.extern.slf4j.Slf4j;

// TODO: Move to Functor
//@Slf4j
public class NoteBodyTextParser {
	

	private Pattern beginActiveTaskTagPtrn = Pattern.compile("\\{TODO:");
	private Pattern beginTextLinkTagPtrn = Pattern.compile("\\{TextLink:");
	private Pattern beginTextTagPtrn = Pattern.compile("\\{TextTag:");
	private Pattern beginDataLinkTagPtrn = Pattern.compile("\\{DataLink:");
	private Pattern beginDataTagPtrn = Pattern.compile("\\{DataTag:");
	private Pattern beginSourceTagPtrn = Pattern.compile("\\{SourceTag:");
	private Pattern beginNameValuePairsPtrn = Pattern.compile("\\{NVP:");

	private Pattern endMultiLineTagPtrn = Pattern.compile("}\\\\");
	
	
	private ParseOpenTextFSM 						parseOpenTextFSM = new ParseOpenTextFSM();
	private List<Pattern> 							beginPatternList = new ArrayList<Pattern>();
	private Map<Pattern, Class<? extends NoteTextParserFSM>> 	beginPatternMap = new HashMap<Pattern, Class<? extends NoteTextParserFSM>>();
	private ParserState								parserState = new ParserState();

    private static Logger log = LogManager.getLogger();

    
    public NoteBodyTextParser() {
    	
    }
    
    public void initialize() {
    	beginPatternList.add(beginActiveTaskTagPtrn);
    	beginPatternList.add(beginTextTagPtrn);
    	beginPatternList.add(beginTextLinkTagPtrn);
    	beginPatternList.add(beginDataTagPtrn);
    	beginPatternList.add(beginDataLinkTagPtrn);
    	beginPatternList.add(beginSourceTagPtrn);
    	beginPatternList.add(beginNameValuePairsPtrn);
    	
    	beginPatternMap.put(beginActiveTaskTagPtrn, ParseActiveTaskTagFSM.class);
    	beginPatternMap.put(beginTextTagPtrn, ParseTextTagFSM.class);
    	beginPatternMap.put(beginTextLinkTagPtrn, ParseTextLinkTagFSM.class);
    	beginPatternMap.put(beginDataTagPtrn, ParseDataTagFSM.class);
    	beginPatternMap.put(beginDataLinkTagPtrn, ParseDataLinkTagFSM.class);
    	beginPatternMap.put(beginSourceTagPtrn, ParseSourceTagFSM.class);
    	beginPatternMap.put(beginNameValuePairsPtrn, ParseNameValuePairsFSM.class);
    }
    
	public void parseNoteBodyText(Note note, String noteBodyText) {		
		parserState.reset(note, noteBodyText);
		
		NoteTextParserFSM parser = parseOpenTextFSM;
		while (parserState.ix_lines < parserState.lines.length - 1) {
			parserState.nextLine();
			while (parserState.ix_words < parserState.words.length - 1) {
				parserState.nextWord();
				parser = parser.parseNextWord();
			}
		}

	}
	
	private class ParserState {
		String[] lines = null;
		String[] words = null;
		int ix_lines = -1;
		int ix_words = -1;	
		Note	note;
		int		noteBodyTextPosn;
		int 	tagObjId = 0;	// Unique only to Note
		
		StringBuffer tagText;
		
		public void reset(Note note, String noteDocText) {
			this.note = note;
			this.tagObjId = 0;
			//lines = noteDocText.split("\\n");
			lines = lineSplit(noteDocText);
			this.noteBodyTextPosn = note.getStartSummaryTextPosn() + note.getSummaryText().length();  // Add 1 for '\n' ?
			ix_lines = -1;
			/* RW: 2014.11.29: I believe this was a bad idea because this line will never be processed
			if (lines.length > 0) {
				words = lines[0].split(" |:");
				words = split(lines[0]);
			}*/
			ix_words = -1;	
		}
		
		public void initializeTag(Tag tag) {
			tag.setObjId(Integer.toString(tagObjId++));
			note.addTag(tag);
			tag.setStartTextPosn(noteBodyTextPosn - words[ix_words].length() + 1);
			tagText = new StringBuffer();
		}
		
		public void finishTag(Tag tag) {
			if (tag.getSummaryText() == null) {
				tag.setSummaryText("..");	// TODO: replace with logic
			}
			tag.setEndTextPosn(noteBodyTextPosn);
			if (tag instanceof TextTag) {
				((TextTag)tag).setText(tagText.toString());
			}
		}
		
		public void nextLine() {
			ix_lines++;
			//words = lines[ix_lines].split(" |:");
			words = wordSplit(lines[ix_lines]);
			ix_words = -1;
		}
		
		public void nextWord() {
			ix_words++;
			// Hack to get the location approximately correct, but need to find more accurate logic
			noteBodyTextPosn += words[ix_words].length();	// Add 1 for ' ' ?
			
			if (words[ix_words].startsWith("{DataTag")) {
				log.info("stop here");
			}
		}
		
		// CLEANUP: Either manage SummaryText state in ParserState or ..
		public void addWordToTagText() {
			tagText.append(getWord());
			tagText.append(" ");
		}
		
		public String getLine() {
			return lines[ix_lines];
		}
		
		public String getWord() {
			return words[ix_words];
		}
		
		private String[] wordSplit(String line) {
			// {TextTag:First Tag: some things}
			if (line.length() == 0) {
				String[] empty = {""};
				return empty;
			}
			List<String> words = new ArrayList<String>();
			char[] chars = line.toCharArray();
			int lastWordBeginIx = 0;
			for (int ix = 0; ix < chars.length; ix++) {
				if (chars[ix] == ' ' && ix > lastWordBeginIx) {
					words.add(line.substring(lastWordBeginIx, ix));
					lastWordBeginIx = ix;  // + 1
				} else if (chars[ix] == ':') {
					words.add(line.substring(lastWordBeginIx, ix + 1));
					lastWordBeginIx = ix + 1;
				} else if (ix == chars.length - 1) {
					words.add(line.substring(lastWordBeginIx, ix + 1));
				} 
			}
			return words.toArray(new String[words.size()]);
		}
		
		private String[] lineSplit(String noteBody) {
			if (noteBody.length() == 0) {
				String[] empty = {""};
				return empty;
			}
			List<String> lines = new ArrayList<String>();
			char[] chars = noteBody.toCharArray();
			int lastLineBeginIx = 0;
			for (int ix = 0; ix < chars.length; ix++) {
				if (chars[ix] == '\n') {
					lines.add(noteBody.substring(lastLineBeginIx, ix + 1));
					lastLineBeginIx = ix + 1;
				} else if (ix == chars.length - 1) {
					lines.add(noteBody.substring(lastLineBeginIx, ix + 1));
				} 
			}
			return lines.toArray(new String[lines.size()]);
		}
	}
	
	private interface NoteTextParserFSM {
		void initialize();
		NoteTextParserFSM parseNextWord();
		void setParentParser(NoteTextParserFSM parent);
	}
	
	
	private abstract class BaseParseFSM implements NoteTextParserFSM {
		protected NoteTextParserFSM	parentParser = null;
		// Light-weight state 
		protected boolean	isSummaryTextCaptured = false;
		
		protected Tag		tag;
		protected int		startTagLine;

		
		public void setParentParser(NoteTextParserFSM parent) {
			this.parentParser = parent;
		}
		
		public NoteTextParserFSM parseNextWord() {
			if (parserEndCondition()) {
				parserState.finishTag(tag);
				return parentParser;
			}

			NoteTextParserFSM parser = getOrCreateParserFSM();
			if (parser == this) {
				if (isSummaryTextCaptured == false) {
					// TODO: Generalize to handle case where summary text is after ":\n"
					tag.setSummaryText(calcTagSummaryText());
					isSummaryTextCaptured = true;
				}
				processWord();
			}
			
			return parser;
		}
		
		// CLEANUP ..
		protected void processWord() {
			parserState.addWordToTagText();
		}
		
		protected boolean parserEndCondition() {
			String word = parserState.getWord();
			Matcher multiLineEndMatcher = endMultiLineTagPtrn.matcher(word);
			if (parserState.ix_lines == startTagLine && (word.endsWith("}") || word.endsWith("}\n"))) {
				return true;
			} else if (multiLineEndMatcher.find()) {
				return true;
			} else {
				return false;
			}			
		}

		protected NoteTextParserFSM getOrCreateParserFSM() {
			String word = parserState.getWord();
			Matcher matcher;
			for (Pattern pattern : beginPatternList) {
				matcher = pattern.matcher(word);
				if (matcher.find()) {		// TODO: Or use matcher.find(arg0) ??
					Class<? extends NoteTextParserFSM> clzz = beginPatternMap.get(pattern);
					try {
						// This throws error: NoSuchMethod: use hack for now
						//NoteTextParserFSM newParser = clzz.getConstructor(null).newInstance();
						NoteTextParserFSM newParser = hack_createParser(clzz);
						if (newParser == null) {
							log.error("stop here");
						}
						newParser.initialize();
						newParser.setParentParser(this);
						if (newParser instanceof ParseNameValuePairsFSM) {
							((ParseNameValuePairsFSM)newParser).setTag(tag);
						}
						return newParser;

					} catch (IllegalArgumentException e) {
                        log.error("getOrCreateParserFSM: caught[" + e + "]");
					} catch (SecurityException e) {
                        log.error("getOrCreateParserFSM: caught[" + e + "]");
					} /*catch (InstantiationException e) {
						log.error("getOrCreateParserFSM: caught[" + e + "]");
					} catch (IllegalAccessException e) {
						log.error("getOrCreateParserFSM: caught[" + e + "]");
					} catch (InvocationTargetException e) {
						log.error("getOrCreateParserFSM: caught[" + e + "]");
					} catch (NoSuchMethodException e) {
						log.error("getOrCreateParserFSM: caught[" + e + "]");
					}*/
					
				}
			}
			return this;
		}
		
		private NoteTextParserFSM hack_createParser(Class<? extends NoteTextParserFSM> clzz) {
			if (clzz == ParseActiveTaskTagFSM.class) {
				return new ParseActiveTaskTagFSM();
			} else if (clzz == ParseTextTagFSM.class) {
				return new ParseTextTagFSM();
			} else if (clzz == ParseTextLinkTagFSM.class) {
				return new ParseTextLinkTagFSM();
			} else if (clzz == ParseDataTagFSM.class) {
				return new ParseDataTagFSM();
			} else if (clzz == ParseDataLinkTagFSM.class) {
				return new ParseDataLinkTagFSM();
			} else if (clzz == ParseSourceTagFSM.class) {
				return new ParseSourceTagFSM();
			} else if (clzz == ParseNameValuePairsFSM.class) {
				return new ParseNameValuePairsFSM();
			} else {
				assert false;
				return null;
			}
		}
		
		protected String calcTagSummaryText() {
			String line = parserState.getLine();
			int startPosn = line.indexOf(parserState.getWord());
			int nextTagEndPosn = line.indexOf("}");
			int endPosn = nextTagEndPosn >= 0 ? Math.min(nextTagEndPosn, line.length()) : line.length();
			String summary = null;
			try {
				summary = line.substring(startPosn, endPosn);
			} catch (StringIndexOutOfBoundsException e) {
                log.error("calcTagSummaryText: caught [" + e + "]");
				summary = "";
			}
			return summary;
		}
		
	}
	
	private class ParseOpenTextFSM extends BaseParseFSM {
		public void initialize() {}
		public NoteTextParserFSM parseNextWord() {
			NoteTextParserFSM parser = getOrCreateParserFSM();
			
			return parser;
		}
	}
	
	private class ParseActiveTaskTagFSM extends BaseParseFSM {

		public void initialize() {
			tag = new TaskTag();
			startTagLine = parserState.ix_lines;
			parserState.initializeTag(tag);
		}	

	}
	
	private class ParseTextTagFSM extends BaseParseFSM {
		
		public ParseTextTagFSM() {}
		
		public void initialize() {
			tag = new TextTag();
			startTagLine = parserState.ix_lines;
			parserState.initializeTag(tag);
		}
		
		
	}
	
	private class ParseTextLinkTagFSM extends BaseParseFSM {
		
		public ParseTextLinkTagFSM() {}
		
		public void initialize() {
			tag = new TextLinkTag();
			startTagLine = parserState.ix_lines;
			parserState.initializeTag(tag);
		}
		
	}
	
	private class ParseDataTagFSM extends BaseParseFSM {
		
		public ParseDataTagFSM() {}
		
		public void initialize() {
			tag = new DataTag();
			startTagLine = parserState.ix_lines;
			parserState.initializeTag(tag);
		}
		
	}	

	
	private class ParseDataLinkTagFSM extends BaseParseFSM {
		
		public ParseDataLinkTagFSM() {}
		
		public void initialize() {
			tag = new DataLinkTag();
			startTagLine = parserState.ix_lines;
			parserState.initializeTag(tag);
		}
		
	}

	private class ParseSourceTagFSM extends BaseParseFSM {
		
		public ParseSourceTagFSM() {}
		
		public void initialize() {
			tag = new SourceTag();
			startTagLine = parserState.ix_lines;
			parserState.initializeTag(tag);
		}
		
	}
	
	private class ParseNameValuePairsFSM extends BaseParseFSM {

		private Tag				tag;
		private StringBuffer 	nameValuesText = new StringBuffer();
		
		public void initialize() {
		}
		
		public void setTag(Tag tag) {
			if (tag == null) {
                log.warn("setTag: tag is null");
			}
			this.tag = tag;
		}
		
		public NoteTextParserFSM parseNextWord() {
			if (parserEndCondition()) {
				String word = parserState.getWord().trim();
				if (word.endsWith("}")) {
					word = word.substring(0, word.length() - 1);
				}
				nameValuesText.append(word);
				if (tag != null) {
					tag.parseNameValuePairs(nameValuesText.toString());
				}
				return parentParser;
			} else {
				nameValuesText.append(parserState.getWord());
				nameValuesText.append(" ");
			}
			
			return this;
		}
		
		protected boolean parserEndCondition() {
			return parserState.getWord().indexOf('}') >= 0;
		}
		
	}
	
	private void testRegexPatterns(String word) {
		Matcher matcher = beginDataTagPtrn.matcher(word);
		if (matcher.find()) {
			System.out.println("success");
		}

	}
	public static void main(String[] args) {
		String test01 = "{DataTag:Chapter=1 | Section=3 | Page=22}";
		NoteBodyTextParser	parser = new NoteBodyTextParser();
		parser.testRegexPatterns(test01);

	}
}
