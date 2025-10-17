package com.app.brainmap.services.impl;

import com.app.brainmap.domain.CreateNotesRequest;
import com.app.brainmap.domain.entities.Notes;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.NotesRepository;
import com.app.brainmap.services.NotesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;

    @Override
    public List<Notes> getAllNotes() {
        return notesRepository.findAll();
    }

    @Override
    public List<Notes> getNotesByUser(UUID userId) {
        return notesRepository.findByUserId(userId);
    }

    @Override
    public Notes getNoteById(UUID id) {
        return notesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Note not found with id: " + id));
    }

    @Override
    @Transactional
    public Notes createNote(User user, CreateNotesRequest request) {
        Notes newNote = new Notes();
        newNote.setTitle(request.getTitle());
        newNote.setDescription(request.getDescription());
        newNote.setUser(user);

        return notesRepository.save(newNote);
    }

    @Override
    public Void deleteNoteById(UUID noteId, UUID userId) throws IllegalArgumentException {
        Notes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new NoSuchElementException("Note not found with id: " + noteId));

        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this note");
        }

        notesRepository.deleteById(noteId);
        return null;
    }

    @Override
    public Notes updateNoteById(UUID noteId, UUID userId, CreateNotesRequest request) {
        Notes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new NoSuchElementException("Note not found with id: " + noteId));

        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to update this note");
        }

        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());

        return notesRepository.save(note);
    }
} 