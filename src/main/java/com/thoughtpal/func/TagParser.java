package com.thoughtpal.func;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtpal.model.notedoc.NoteDocumentText;
import com.thoughtpal.model.notedoc.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class TagParser {

	private Pattern beginActiveTaskTagPtrn = Pattern.compile("\\{TODO:");
	private Pattern beginTextLinkTagPtrn = Pattern.compile("\\{TextLink:");
	private Pattern beginTextTagPtrn = Pattern.compile("\\{TextTag:");
	private Pattern beginDataLinkTagPtrn = Pattern.compile("\\{DataLink:");
	private Pattern beginDataTagPtrn = Pattern.compile("\\{DataTag:");
	private Pattern beginSourceTagPtrn = Pattern.compile("\\{SourceTag:");
	// 2021.03.12: Disable as the implementation throws errors -- likely was not completed and tested
	//private Pattern beginNameValuePairsPtrn = Pattern.compile("\\{NVP:");

	private Pattern endMultiLineTagPtrn = Pattern.compile("}\\\\");
	
	
	private List<Pattern> 				beginPatternList = new ArrayList<Pattern>();
	private ParserState                 parserState;
    private Map<Pattern, Tag.TagType>   tagTypeMap = new HashMap<>();

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
    	//beginPatternList.add(beginNameValuePairsPtrn);

    	tagTypeMap.put(beginActiveTaskTagPtrn, Tag.TagType.Task);
        tagTypeMap.put(beginTextTagPtrn, Tag.TagType.TextTag);
        tagTypeMap.put(beginTextLinkTagPtrn, Tag.TagType.TextLink);
        tagTypeMap.put(beginDataTagPtrn, Tag.TagType.DataTag);
        tagTypeMap.put(beginDataLinkTagPtrn, Tag.TagType.DataLink);
        tagTypeMap.put(beginSourceTagPtrn, Tag.TagType.SourceTag);
    }

    public List<Tag> parse(NoteDocumentText noteDocText) {

        parserState = new ParserState(noteDocText);
        ParserFSM parser = new OpenTextParserFSM(null);

        while (parserState.ix_lines < parserState.lines.length - 1) {
            parserState.nextLine();
            while (parserState.ix_words < parserState.words.length - 1) {
                parserState.nextWord();
                parser = parser.parseNextWord();
                if (parser == null) {
                    System.out.println("stop here");
                }
            }
            // Move noteOffset to after the last word + newline
            parserState.noteOffset += parserState.words[parserState.words.length - 1].length() + 1;
        }

        return parserState.tags;
    }

    private class ParserState {
        private NoteDocumentText    noteDocText;
        private String[] lines;
        private int      ix_lines;
        private int		 noteOffset;
        private String[]  words;
        private Integer[] wordOffsets;
        private int      ix_words;
        private int 	 tagId;
        private List<Tag>   tags;

        private class Combo {
            String[]  words;
            Integer[] wordOffsets;

            public Combo(String[] words, Integer[] wordOffsets) {
                this.words = words;
                this.wordOffsets = wordOffsets;
            }
        }

        public ParserState(NoteDocumentText noteDocText) {
            this.noteDocText = noteDocText;
            lines = noteDocText.getRawText().split("\\n");
            ix_lines = -1;      // Initial condition
            noteOffset = 0;
            tagId = 0;
            tags  = new ArrayList<>();
        }


        // TODO: Move these functions to the Parser, so this class only maintains state ?

        public void startTag(Tag tag) {
            tag.setId(Integer.toString(tagId++));    // TODO: Set by Repo
            tag.setStartOffset(wordOffsets[ix_words]);
        }

        public void finishTag(Tag tag) {
            if (tag == null) {
                System.out.println("stop here");
            }
//            if (tag.getSummaryText() == null) {
//                tag.setSummaryText("..");	// TODO: replace with logic
//            }
            tag.setEndOffset(noteOffset);
            tags.add(tag);
        }

        public void nextLine() {
            ix_lines++;
            if (ix_lines == 8) {
                System.out.println("stop here");
            }
            Combo wordsAndOffsets = wordSplit(lines[ix_lines]);
            words = wordsAndOffsets.words;
            wordOffsets = wordsAndOffsets.wordOffsets;
            ix_words = -1;
        }

        public void nextWord() {
            ix_words++;
            noteOffset = wordOffsets[ix_words];
            System.out.println("ix_words (" + ix_words + "), noteOffset (" + noteOffset + "), words[] (" + words[ix_words] + "), wordsOffset[] (" + wordOffsets[ix_words] + ")");
        }


        public String getLine() {
            return lines[ix_lines];
        }

        public String getWord() {
            return words[ix_words];
        }

        // TRICKY: noteOffset will change as the line is processed in nextWord(),
        //   but it's used here before the line processing begins
        private Combo wordSplit(String line) {
            if (line.length() == 0) {
                String[] words = {""};
                Integer[] wordOffsets = {noteOffset};
                return new Combo(words, wordOffsets);
            }
            List<String> words = new ArrayList<String>();
            List<Integer> wordOffsets = new ArrayList<>();
            char[] chars = line.toCharArray();
            int lastWordBeginIx = 0;
            for (int ix = 0; ix < chars.length; ix++) {
                if (chars[ix] == ' ' && ix > lastWordBeginIx) {
                    words.add(line.substring(lastWordBeginIx, ix));
                    wordOffsets.add(noteOffset + lastWordBeginIx);
                    lastWordBeginIx = ix;  // + 1
                } else if (chars[ix] == ':') {
                    words.add(line.substring(lastWordBeginIx, ix + 1));
                    wordOffsets.add(noteOffset + lastWordBeginIx);
                    lastWordBeginIx = ix + 1;
                } else if (ix == chars.length - 1) {
                    words.add(line.substring(lastWordBeginIx, ix + 1));
                    wordOffsets.add(noteOffset + lastWordBeginIx);
                }
            }
            // EdgeCase: last char of line is ' '
            if (chars[chars.length - 1] == ' ') {
                words.add(new String(" "));
                wordOffsets.add(noteOffset + lastWordBeginIx);
            }

            Combo wordsAndOffsets = new Combo(words.toArray(new String[words.size()]), wordOffsets.toArray(new Integer[wordOffsets.size()]));
            return wordsAndOffsets;
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

    private interface ParserFSM {
        ParserFSM parseNextWord();
    }

    private abstract class BaseParserFSM implements ParserFSM {
        protected ParserFSM parentParser = null;


        public BaseParserFSM(ParserFSM parent) {
            this.parentParser = parent;
        }

        final protected ParserFSM getOrCreateParserFSM() {
            String word = parserState.getWord();
            Matcher matcher;
            for (Pattern pattern : beginPatternList) {
                matcher = pattern.matcher(word);
                if (matcher.find()) {
                    ParserFSM nextParser = createParser(pattern);
                    Tag.TagType tagType = tagTypeMap.get(pattern);
                    if (tagType != null) {
                        assert nextParser instanceof TagParserFSM;

                        Tag newTag = Tag.builder()
                                .workspaceId(parserState.noteDocText.getWorkspaceId()).tagType(tagType)
                                .startTextOffset(parserState.noteOffset).build();
                        parserState.startTag(newTag);

                        ((TagParserFSM)nextParser).setTag(newTag);
                        return nextParser;
                    }
//                    else if (pattern == beginNameValuePairsPtrn) {
//                        return new NameValuePairsParserFSM(this);
//                    }
                }
            }
            return this;
        }

        private ParserFSM createParser(Pattern pattern){
            if (pattern == beginTextTagPtrn) {
                return new TagParserFSM(this, parserState.ix_lines);
            }
            else if (pattern == beginTextLinkTagPtrn) {
                return new TagParserFSM(this, parserState.ix_lines);
            }
            else if (pattern == beginDataTagPtrn) {
                return new TagParserFSM(this, parserState.ix_lines);
            }
            else if (pattern == beginDataLinkTagPtrn) {
                return new TagParserFSM(this, parserState.ix_lines);
            }
            else if (pattern == beginSourceTagPtrn) {
                return new TagParserFSM(this, parserState.ix_lines);
            }
            else if (pattern == beginActiveTaskTagPtrn) {
                return new TagParserFSM(this, parserState.ix_lines);
            }
//            else if (pattern == beginNameValuePairsPtrn) {
//                return new NameValuePairsParserFSM(this);
//            }
            else {
                System.out.println("Error: unrecognized pattern: [" + pattern.toString() + "]");
                return null;
            }
        }


    }

    private class OpenTextParserFSM extends BaseParserFSM {

        public OpenTextParserFSM(ParserFSM parentParser) {
            super(parentParser);
        }

        public ParserFSM parseNextWord() {
            ParserFSM parser = getOrCreateParserFSM();

            return parser;
        }
    }

    private class TagParserFSM extends BaseParserFSM {

        // Design: These state variables must be kept with Parser objects because of Tag nesting
        protected Tag		    tag;
        protected boolean	    isSummaryTextCaptured = false;
        private int		        startTagLine;   // To make sure parser end condition "}\n" only applied on first line of tag

        public TagParserFSM(ParserFSM parentParser, int startTagLine) {
            super(parentParser);
            setStartTagLine(startTagLine);
        }

        public ParserFSM parseNextWord() {
            if (parserEndCondition()) {
                parserState.finishTag(tag);
                return parentParser;
            }

            ParserFSM parser = getOrCreateParserFSM();
            if (parser == this) {
                if (isSummaryTextCaptured == false) {
                    // TODO: Generalize to handle case where summary text is after ":\n"
                    if (tag == null) {
                        System.out.println("Probably found a missing multi-line tag end token at line (" + parserState.ix_lines + ")");
                        System.exit(-1);
                    }
                    tag.setSummaryText(calcTagSummaryText());
                    isSummaryTextCaptured = true;
                }
            }

            return parser;
        }

        public void setTag(Tag tag) {
            this.tag = tag;
        }
        public void setStartTagLine(int startTagLine) {
            this.startTagLine = startTagLine;
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

    private class NameValuePairsParserFSM extends BaseParserFSM {

        private StringBuffer 	nameValuesText = new StringBuffer();

        public NameValuePairsParserFSM(ParserFSM parentParser) {
            super(parentParser);
        }

        public ParserFSM parseNextWord() {
            if (parserEndCondition()) {
                String word = parserState.getWord().trim();
                if (word.endsWith("}")) {
                    word = word.substring(0, word.length() - 1);
                }
                nameValuesText.append(word);
                assert parentParser instanceof TagParserFSM;
                if (((TagParserFSM)parentParser).tag != null) {
                    parseNameValuePairs(((TagParserFSM)parentParser).tag, nameValuesText.toString());
                }
                return parentParser;
            } else {
                nameValuesText.append(parserState.getWord());
                nameValuesText.append(" ");
            }

            return this;
        }

        private void parseNameValuePairs(Tag tag, String nameValuesStr) {
            //String[] parts = nameValuesStr.split("[ ]*=[ ]*");
            String[] parts = nameValuesStr.split("\\|");
            if (parts.length != 2) {
                log.error("parseNameValuePairs: nameValuesStr is not well formed: " + nameValuesStr);
                return;
            }
            Map<String, String> nameValues = new HashMap<String, String>();
            for (int ix = 0; ix < parts.length; ix++) {
                String[] nameValue = parts[ix].split("=");
                nameValues.put(nameValue[0].trim(), nameValue[1].trim());
            }
            tag.setNameValues(nameValues);
        }

        protected boolean parserEndCondition() {
            return parserState.getWord().indexOf('}') >= 0;
        }

    }


}
