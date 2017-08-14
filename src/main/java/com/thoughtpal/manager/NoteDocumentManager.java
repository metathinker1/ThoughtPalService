package com.thoughtpal.manager;

import com.thoughtpal.func.NoteParser;
import com.thoughtpal.func.OutlineNoteParser;
import com.thoughtpal.func.TagParser;
import com.thoughtpal.model.note.Note;
import com.thoughtpal.model.note.NoteDocument;
import com.thoughtpal.model.note.NoteDocumentText;
import com.thoughtpal.model.tag.Tag;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteDocumentManager {

    private Map<NoteDocument.NoteDocumentStructure, NoteParser> noteParserMap =
            new HashMap<NoteDocument.NoteDocumentStructure, NoteParser>();
    private TagParser tagParser = new TagParser();

    public NoteDocumentManager() {
        noteParserMap.put(NoteDocument.NoteDocumentStructure.Outline, new OutlineNoteParser());

        tagParser.initialize();
    }

    public void parse(NoteDocumentText noteDocText) {

        // Stream .getNoteParser(noteDocText).parse()

        NoteParser noteParser = noteParserMap.get(noteDocText.getNoteDocument().getNoteDocumentStructure());
        // TODO: change to assert not null ...
        if (noteParser != null) {
            List<Note> notes = noteParser.parse(noteDocText);
            for (Note note: notes) {
                System.out.println("Note: " + note.getStartNoteOffset() + ": " + note.getLabel() + ": " + note.getSummaryText());
            }
            List<Tag> tags = tagParser.parse(noteDocText);
            for (Tag tag: tags) {
                System.out.println("Tag: " + tag.getStartTextOffset() + ": " + tag.getSummaryText());
                Map<String, String> nameValues = tag.getNameValues();
                if (nameValues != null) {
                    nameValues.forEach((name, value) -> {
                        System.out.println(name + ": " + value);
                    });
                }
            }
        }
        System.out.println("Finished");
    }


    // TODO: Move to unit test

    public String readFileAsString(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


    public static void main(String[] args) {
        NoteDocumentManager tester = new NoteDocumentManager();
        try {
            String filePath = "/Users/robertwood/Project.ThoughtPal/ThoughtPalService/src/main/java/com/thoughtpal/manager/AppDevFW.AWS.nodoc";
            String rawText = tester.readFileAsString(filePath, StandardCharsets.UTF_8);
            NoteDocumentText noteDocText = NoteDocumentText.builder()
                    .workspaceId("001").noteDocumentId("001").rawText(rawText).build();

            tester.parse(noteDocText);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
