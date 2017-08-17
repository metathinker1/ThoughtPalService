package com.thoughtpal.func;

import com.thoughtpal.model.notedoc.Note;
import com.thoughtpal.model.notedoc.NoteDocumentText;

import java.util.List;

public interface NoteParser {
    List<Note> parse(NoteDocumentText noteDocText);
}
