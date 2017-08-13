package com.thoughtpal.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtpal.model.note.Note;
import com.thoughtpal.model.note.NoteDocumentText;
import com.thoughtpal.model.tag.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import lombok.extern.slf4j.Slf4j;

// TODO: Move to Functor
//@Slf4j
public class TagParser {


	private Pattern beginActiveTaskTagPtrn = Pattern.compile("\\{TODO:");
	private Pattern beginTextLinkTagPtrn = Pattern.compile("\\{TextLink:");
	private Pattern beginTextTagPtrn = Pattern.compile("\\{TextTag:");
	private Pattern beginDataLinkTagPtrn = Pattern.compile("\\{DataLink:");
	private Pattern beginDataTagPtrn = Pattern.compile("\\{DataTag:");
	private Pattern beginSourceTagPtrn = Pattern.compile("\\{SourceTag:");
	private Pattern beginNameValuePairsPtrn = Pattern.compile("\\{NVP:");

	// TODO: Consider: Handle NVP as Tag.tagType == NameValuePair

	private Pattern endMultiLineTagPtrn = Pattern.compile("}\\\\");
	
	
	private List<Pattern> 							beginPatternList = new ArrayList<Pattern>();
	///private Map<Pattern, Class<? extends NoteTextParserFSM>> 	beginPatternMap = new HashMap<Pattern, Class<? extends NoteTextParserFSM>>();
	private ParserState_New								parserState;
    private Map<Pattern, Tag.TagType>        tagTypeMap = new HashMap<>();

    private static Logger log = LogManager.getLogger();

    
    public TagParser() {
    	
    }
    
    public void initialize() {
    	beginPatternList.add(beginActiveTaskTagPtrn);
    	beginPatternList.add(beginTextTagPtrn);
    	beginPatternList.add(beginTextLinkTagPtrn);
    	beginPatternList.add(beginDataTagPtrn);
    	beginPatternList.add(beginDataLinkTagPtrn);
    	beginPatternList.add(beginSourceTagPtrn);
    	beginPatternList.add(beginNameValuePairsPtrn);
    	/*
    	beginPatternMap.put(beginActiveTaskTagPtrn, ParseActiveTaskTagFSM.class);
    	beginPatternMap.put(beginTextTagPtrn, ParseTextTagFSM.class);
    	beginPatternMap.put(beginTextLinkTagPtrn, ParseTextLinkTagFSM.class);
    	beginPatternMap.put(beginDataTagPtrn, ParseDataTagFSM.class);
    	beginPatternMap.put(beginDataLinkTagPtrn, ParseDataLinkTagFSM.class);
    	beginPatternMap.put(beginSourceTagPtrn, ParseSourceTagFSM.class);
    	beginPatternMap.put(beginNameValuePairsPtrn, ParseNameValuePairsFSM.class);
        */
    	tagTypeMap.put(beginActiveTaskTagPtrn, Tag.TagType.Task);
        tagTypeMap.put(beginTextTagPtrn, Tag.TagType.TextTag);
        tagTypeMap.put(beginTextLinkTagPtrn, Tag.TagType.TextLink);
        tagTypeMap.put(beginDataTagPtrn, Tag.TagType.DataTag);
        tagTypeMap.put(beginDataLinkTagPtrn, Tag.TagType.DataLink);
        tagTypeMap.put(beginSourceTagPtrn, Tag.TagType.SourceTag);
        tagTypeMap.put(beginNameValuePairsPtrn, Tag.TagType.NameValuePairs);

        /*  Can't cache, because of nested Tags !!
        tagParserMap.put(Tag.TagType.TextTag, tagParserFSM);
        tagParserMap.put(Tag.TagType.TextLink, tagParserFSM);
        tagParserMap.put(Tag.TagType.DataTag, tagParserFSM);
        tagParserMap.put(Tag.TagType.DataLink, tagParserFSM);
        tagParserMap.put(Tag.TagType.SourceTag, tagParserFSM);
        tagParserMap.put(Tag.TagType.Task, tagParserFSM);
        tagParserMap.put(Tag.TagType.NameValuePairs, nvpTagParserFSM);
        */
    }

    public List<Tag> parse(NoteDocumentText noteDocText) {

        parserState = new ParserState_New(noteDocText);
        NoteTextParserFSM_New parser = new ParseOpenTextFSM_New(null);

        while (parserState.ix_lines < parserState.lines.length - 1) {
            parserState.nextLine();
            while (parserState.ix_words < parserState.words.length - 1) {
                parserState.nextWord();
                parser = parser.parseNextWord();
                if (parser == null) {
                    System.out.println("stop here");
                }
            }
        }

        return parserState.tags;
    }

    private class ParserState_New {
        NoteDocumentText    noteDocText;
        String[] lines;
        int      ix_lines;
        int		 noteOffset;
        String[] words;
        int      ix_words;
        int 	 tagId;
        List<Tag>   tags;

        // Current Tag
        protected Tag		tag;
        protected boolean	isSummaryTextCaptured = false;
        protected int		startTagLine;


        public ParserState_New(NoteDocumentText noteDocText) {
            this.noteDocText = noteDocText;
            lines = noteDocText.getRawText().split("\\n");
            ix_lines = 0;
            noteOffset = 0;
            tagId = 0;
            tags  = new ArrayList<>();
        }


        // TODO: Move these functions to the Parser, so this class only maintains state ?

        public void startTag(Tag tag) {
            tag.setId(Integer.toString(tagId++));    // TODO: Set by Repo
            tag.setStartTextOffset(noteOffset - words[ix_words].length() + 1);
        }

        public void finishTag() {
            if (tag.getSummaryText() == null) {
                tag.setSummaryText("..");	// TODO: replace with logic
            }
            tag.setEndTextOffset(noteOffset);
            tags.add(tag);
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
            noteOffset += words[ix_words].length();	// Add 1 for ' ' ?
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

    // TODO: Rename: TagParserFSM
    private interface NoteTextParserFSM_New {
        NoteTextParserFSM_New parseNextWord();
    }


    // TODO: Rename: TagParserFSM ?
    private abstract class BaseParserFSM_New implements NoteTextParserFSM_New {
        protected NoteTextParserFSM_New parentParser = null;

        public BaseParserFSM_New(NoteTextParserFSM_New parent) {
            this.parentParser = parent;
        }


        protected boolean parserEndCondition() {
            String word = parserState.getWord();
            Matcher multiLineEndMatcher = endMultiLineTagPtrn.matcher(word);
            if (/*parserState.ix_lines == parserState.startTagLine &&*/ (word.endsWith("}") || word.endsWith("}\n"))) {
                return true;
            } else if (multiLineEndMatcher.find()) {
                return true;
            } else {
                return false;
            }
        }

        protected NoteTextParserFSM_New getOrCreateParserFSM() {
            String word = parserState.getWord();
            Matcher matcher;
            for (Pattern pattern : beginPatternList) {
                // TODO: Consider putting NVP logic here; but just if parsing a Tag, not open text ...

                matcher = pattern.matcher(word);
                if (matcher.find()) {		// TODO: Or use matcher.find(arg0) ??
                    Tag.TagType tagType = tagTypeMap.get(pattern);
                    // TODO: Check for null / exception

                    parserState.tag = Tag.builder()
                            .workspaceId(parserState.noteDocText.getWorkspaceId()).tagType(tagType)
                            .startTextOffset(parserState.noteOffset).build();

                    // TODO: return correct NoteTextParserFSM_New
                    return createParser(pattern);
                }
            }
            return this;
        }

        private NoteTextParserFSM_New createParser(Pattern pattern){
            if (pattern == beginTextTagPtrn) {
                return new ParseTagFSM_New(this);
            }
            else if (pattern == beginTextLinkTagPtrn) {
                return new ParseTagFSM_New(this);
            }
            else if (pattern == beginDataTagPtrn) {
                return new ParseTagFSM_New(this);
            }
            else if (pattern == beginDataLinkTagPtrn) {
                return new ParseTagFSM_New(this);
            }
            else if (pattern == beginSourceTagPtrn) {
                return new ParseTagFSM_New(this);
            }
            else if (pattern == beginActiveTaskTagPtrn) {
                return new ParseTagFSM_New(this);
            }
            else if (pattern == beginNameValuePairsPtrn) {
                return new ParseNameValuePairsFSM_New(this);
            }
            else {
                System.out.println("Error: unrecognized pattern: [" + pattern.toString() + "]");
                return null;
            }
        }

        // TODO: This should only be available to Tag parser, not ParseOpenTextFSM_New
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

    private class ParseOpenTextFSM_New extends BaseParserFSM_New {

        public ParseOpenTextFSM_New(NoteTextParserFSM_New parentParser) {
            super(parentParser);
        }

        public NoteTextParserFSM_New parseNextWord() {
            NoteTextParserFSM_New parser = getOrCreateParserFSM();

            return parser;
        }
    }

    private class ParseTagFSM_New extends BaseParserFSM_New {

        public ParseTagFSM_New(NoteTextParserFSM_New parentParser) {
            super(parentParser);
        }

        public NoteTextParserFSM_New parseNextWord() {
            if (parserEndCondition()) {
                parserState.finishTag();
                return parentParser;
            }

            NoteTextParserFSM_New parser = getOrCreateParserFSM();
            if (parser == this) {
                if (parserState.isSummaryTextCaptured == false) {
                    // TODO: Generalize to handle case where summary text is after ":\n"
                    parserState.tag.setSummaryText(calcTagSummaryText());
                    parserState.isSummaryTextCaptured = true;
                }
                //processWord();
            }

            return parser;
        }
    }

    private class ParseNameValuePairsFSM_New extends BaseParserFSM_New {

        private StringBuffer 	nameValuesText = new StringBuffer();

        public ParseNameValuePairsFSM_New(NoteTextParserFSM_New parentParser) {
            super(parentParser);
        }

        public NoteTextParserFSM_New parseNextWord() {
            if (parserEndCondition()) {
                String word = parserState.getWord().trim();
                if (word.endsWith("}")) {
                    word = word.substring(0, word.length() - 1);
                }
                nameValuesText.append(word);
                if (parserState.tag != null) {
                    parseNameValuePairs(parserState.tag, nameValuesText.toString());
                }
                return parentParser;
            } else {
                nameValuesText.append(parserState.getWord());
                nameValuesText.append(" ");
            }

            return this;
        }

        private void parseNameValuePairs(Tag tag, String nameValuesStr) {
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
                    //TODO: implement:
                    // nameValues.put(name, value);
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

        protected boolean parserEndCondition() {
            return parserState.getWord().indexOf('}') >= 0;
        }

    }

    //===========================
/*
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
		int		noteBodyTextPosn;   // TODO: Refactor: noteOffset: same as OutlineNoteParser
		int 	tagObjId = 0;	// Unique only to Note  TODO: Refactor


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
			}*
			ix_words = -1;	
		}

		public void initializeTag(Tag tag) {
			tag.setId(Integer.toString(tagObjId++));    // TODO: Set by Repo
			note.addTag(tag);
			tag.setStartTextOffset(noteBodyTextPosn - words[ix_words].length() + 1);
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
					}*
					
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
		TagParser	parser = new TagParser();
		parser.testRegexPatterns(test01);

	}
*/
}
