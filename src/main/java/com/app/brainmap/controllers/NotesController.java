package com.app.brainmap.controllers;

import com.app.brainmap.domain.CreateNotesRequest;
import com.app.brainmap.domain.dto.CreateNotesRequestDto;
import com.app.brainmap.domain.dto.NotesDto;
import com.app.brainmap.domain.entities.Notes;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.NotesMapper;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.NotesService;
import com.app.brainmap.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/notes")
@RequiredArgsConstructor
@Slf4j
public class NotesController {

    private final NotesMapper notesMapper;
    private final NotesService notesService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<NotesDto>> getAllNotes() {
        List<Notes> notes = notesService.getAllNotes();
        List<NotesDto> notesDtos = notes.stream().map(notesMapper::toDto).toList();

        return ResponseEntity.ok(notesDtos);
    }

    @GetMapping(path = "/user")
    public ResponseEntity<List<NotesDto>> getNotesByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        log.info("Getting notes for user: {}", userId);
        
        List<Notes> notes = notesService.getNotesByUser(userId);
        List<NotesDto> notesDtos = notes.stream().map(notesMapper::toDto).toList();

        return ResponseEntity.ok(notesDtos);
    }

    @GetMapping(path = "/{noteId}")
    public ResponseEntity<NotesDto> getNoteById(@PathVariable UUID noteId) {
        Notes note = notesService.getNoteById(noteId);
        NotesDto noteDto = notesMapper.toDto(note);
        return ResponseEntity.ok(noteDto);
    }

    @PostMapping
    public ResponseEntity<NotesDto> createNote(@RequestBody CreateNotesRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        log.info("Creating note for user: {}", userId);
        
        User user = userService.getUserById(userId);
        CreateNotesRequest createNotesRequest = notesMapper.toCreateNotesRequest(requestDto);
        Notes createdNote = notesService.createNote(user, createNotesRequest);
        NotesDto createdNoteDto = notesMapper.toDto(createdNote);

        return new ResponseEntity<>(createdNoteDto, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{noteId}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable UUID noteId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        log.info("Deleting note {} for user: {}", noteId, userId);

        notesService.deleteNoteById(noteId, userId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{noteId}")
    public ResponseEntity<NotesDto> updateNoteById(
            @PathVariable UUID noteId,
            @RequestBody CreateNotesRequestDto requestDto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        UUID userId = userDetails.getUserId();
        log.info("Updating note {} for user: {}", noteId, userId);

        CreateNotesRequest createNotesRequest = notesMapper.toCreateNotesRequest(requestDto);
        Notes updatedNote = notesService.updateNoteById(noteId, userId, createNotesRequest);
        NotesDto updatedNoteDto = notesMapper.toDto(updatedNote);

        return ResponseEntity.ok(updatedNoteDto);
    }
} 