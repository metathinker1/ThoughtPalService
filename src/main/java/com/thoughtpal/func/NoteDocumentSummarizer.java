package com.thoughtpal.func;

import com.thoughtpal.model.notedoc.NoteDocItem;
import com.thoughtpal.model.notedoc.NoteDocumentText;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class NoteDocumentSummarizer {

    private final NoteDocumentParser      noteDocParser;
    private final NoteDocSummaryRenderer  noteDocSummRenderer;

    public String summarizeAndRender(NoteDocumentText noteDocText)
    {
        noteDocParser.parse(noteDocText);
        noteDocText.getNoteDocument().sortNoteDocItems();
        return noteDocSummRenderer.render(noteDocText.getNoteDocument());
    }

}
