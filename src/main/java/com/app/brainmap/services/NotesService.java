package com.app.brainmap.services;

import com.app.brainmap.domain.CreateNotesRequest;
import com.app.brainmap.domain.entities.Notes;
import com.app.brainmap.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface NotesService {
    List<Notes> getAllNotes();
    List<Notes> getNotesByUser(UUID userId);
    Notes getNoteById(UUID id);
    Notes createNote(User user, CreateNotesRequest request);
    Void deleteNoteById(UUID noteId, UUID userId) throws IllegalArgumentException;
    Notes updateNoteById(UUID noteId, UUID userId, CreateNotesRequest request);
} 