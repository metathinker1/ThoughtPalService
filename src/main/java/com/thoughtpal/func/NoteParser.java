package com.thoughtpal.func;

import com.thoughtpal.model.note.Note;
import com.thoughtpal.model.note.NoteDocumentText;

import java.util.List;

public interface NoteParser {
    List<Note> parse(NoteDocumentText noteDocumentText);
}
