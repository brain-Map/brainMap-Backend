package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.EventDto;
import com.app.brainmap.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventDto eventDto,
                                                @RequestHeader("User-Id") UUID userId) {
        log.info("Request to create event for user: {}", userId);
        EventDto createdEvent = eventService.createEvent(eventDto, userId);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable UUID eventId,
                                                @Valid @RequestBody EventDto eventDto,
                                                @RequestHeader("User-Id") UUID userId) {
        log.info("Request to update event {} for user: {}", eventId, userId);
        EventDto updatedEvent = eventService.updateEvent(eventId, eventDto, userId);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId,
                                            @RequestHeader("User-Id") UUID userId) {
        log.info("Request to delete event {} for user: {}", eventId, userId);
        eventService.deleteEvent(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable UUID eventId,
                                             @RequestHeader("User-Id") UUID userId) {
        log.info("Request to get event {} for user: {}", eventId, userId);
        EventDto event = eventService.getEventById(eventId, userId);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents(@RequestHeader("User-Id") UUID userId) {
        log.info("Request to get all events for user: {}", userId);
        List<EventDto> events = eventService.getAllEventsByUser(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<EventDto>> getEventsByDate(@PathVariable
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                          LocalDate date,
                                                          @RequestHeader("User-Id") UUID userId) {
        log.info("Request to get events for user {} on date: {}", userId, date);
        List<EventDto> events = eventService.getEventsByUserAndDate(userId, date);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<EventDto>> getEventsByDateRange(@RequestParam
                                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                               LocalDate startDate,
                                                               @RequestParam
                                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                               LocalDate endDate,
                                                               @RequestHeader("User-Id") UUID userId) {
        log.info("Request to get events for user {} between {} and {}", userId, startDate, endDate);
        List<EventDto> events = eventService.getEventsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<EventDto>> getEventsPaginated(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestHeader("User-Id") UUID userId) {
        log.info("Request to get paginated events for user: {} (page: {}, size: {})", userId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<EventDto> events = eventService.getEventsByUserPaginated(userId, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvents(@RequestParam String keyword,
                                                       @RequestHeader("User-Id") UUID userId) {
        log.info("Request to search events for user {} with keyword: {}", userId, keyword);
        List<EventDto> events = eventService.searchEvents(userId, keyword);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalEventsCount(@RequestHeader("User-Id") UUID userId) {
        log.info("Request to get total events count for user: {}", userId);
        long count = eventService.getTotalEventsCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/date/{date}")
    public ResponseEntity<Long> getEventsCountByDate(@PathVariable
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                     LocalDate date,
                                                     @RequestHeader("User-Id") UUID userId) {
        log.info("Request to get events count for user {} on date: {}", userId, date);
        long count = eventService.getEventsCountByDate(userId, date);
        return ResponseEntity.ok(count);
    }
}