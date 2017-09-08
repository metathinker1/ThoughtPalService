package com.thoughtpal.func;

import com.thoughtpal.model.notedoc.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NoteDocumentParser {

    private Map<NoteDocument.NoteDocumentStructure, NoteParser> noteParserMap =
            new HashMap<NoteDocument.NoteDocumentStructure, NoteParser>();
    private TagParser tagParser = new TagParser();

    public NoteDocumentParser() {
        noteParserMap.put(NoteDocument.NoteDocumentStructure.Outline, new OutlineNoteParser());

        tagParser.initialize();
    }

    public void parse(NoteDocumentText noteDocText) {

        // Idea: Stream .getNoteParser(noteDocText).parse()

        NoteParser noteParser = noteParserMap.get(noteDocText.getNoteDocument().getNoteDocumentStructure());
        // TODO: change to assert not null ...
        if (noteParser != null) {
            List<Note> notes = noteParser.parse(noteDocText);
            List<Tag> tags = tagParser.parse(noteDocText);
            NoteDocument noteDoc = noteDocText.getNoteDocument();
            noteDoc.setNotesAndTags(notes, tags);
            noteDocText.setNoteDocument(noteDoc);
        }
        System.out.println("Finished");
    }


    // TODO: Move to unit test

    public String readFileAsString(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    // TODO: Move to Unit Test
    public static void main(String[] args) {
        NoteDocumentParser tester = new NoteDocumentParser();
        try {
            String filePath = "/Users/robertwood/Project.ThoughtPal/ThoughtPalService/src/main/java/com/thoughtpal/func/AppDevFW_1.AWS.nodoc";
            String rawText = tester.readFileAsString(filePath, StandardCharsets.UTF_8);
            NoteDocumentText noteDocText = NoteDocumentText.builder()
                    .workspaceId("001").noteDocumentId("001").rawText(rawText).build();

            tester.parse(noteDocText);

            // TODO: stream from getNoteDocItems; sort; print note and tag differently
            /*
            for (Note note: notes) {
                System.out.println("Note: " + note.getStartOffset() + ": " + note.getLabel() + ": " + note.getSummaryText());
            }
            for (Tag tag: tags) {
                System.out.println("Tag: " + tag.getStartOffset() + ": " + tag.getSummaryText());
                Map<String, String> nameValues = tag.getNameValues();
                if (nameValues != null) {
                    nameValues.forEach((name, value) -> {
                        System.out.println(name + ": " + value);
                    });
                }
            }*/

            List<NoteDocItem> sortedItems = noteDocText.getNoteDocument().getNoteDocItems().stream()
                .sorted((NoteDocItem item1, NoteDocItem item2) -> item1.getStartOffset().compareTo(item2.getStartOffset()))
                .collect(Collectors.toList());

            sortedItems.stream()
                //.forEach(System.out::println);
                .forEach(noteDocItem -> {
                    if (noteDocItem instanceof Note) {
                        Note note = (Note) noteDocItem;
                        System.out.println("Note: " + note.getStartOffset() + ": " + note.getLabel() + ": " + note.getSummaryText());
                    } else {
                        Tag tag = (Tag) noteDocItem;
                        System.out.println("Tag: " + tag.getStartOffset() + ": " + tag.getSummaryText());
                    }
                });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
