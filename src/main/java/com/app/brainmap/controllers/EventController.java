package com.app.brainmap.controllers;

    import com.app.brainmap.domain.dto.EventDto;
    import com.app.brainmap.domain.dto.MessageResponse;
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
        public ResponseEntity<EventDto> createEvent(
                @Valid @RequestBody EventDto eventDto) {
            EventDto createdEvent = eventService.createEvent(eventDto);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        }

        @PutMapping("/{eventId}")
        public ResponseEntity<EventDto> updateEvent(@PathVariable UUID eventId,
                                                    @Valid @RequestBody EventDto eventDto) {
            EventDto updatedEvent = eventService.updateEvent(eventId, eventDto);
            return ResponseEntity.ok(updatedEvent);
        }

        @DeleteMapping("/{eventId}")
        public ResponseEntity<MessageResponse> deleteEvent(@PathVariable UUID eventId) {
            eventService.deleteEvent(eventId);
            return ResponseEntity.ok(new MessageResponse("Event deleted successfully"));
        }

        @GetMapping("/{eventId}")
        public ResponseEntity<EventDto> getEvent(@PathVariable UUID eventId) {
            EventDto event = eventService.getEventById(eventId);
            return ResponseEntity.ok(event);
        }
        //Done----------

        @GetMapping
        public ResponseEntity<List<EventDto>> getAllEvents(@RequestHeader("User-Id") UUID userId) {
            log.info("Request to get all events for user: {}", userId);
            List<EventDto> events = eventService.getAllEventsByUser(userId);
            return ResponseEntity.ok(events);
        }

        @GetMapping("/date/{date}")
        public ResponseEntity<List<EventDto>> getEventsByDate(@PathVariable
                                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                              LocalDate date) {
            log.info("Request to get events on date: {}",  date);
            List<EventDto> events = eventService.getEventsByDate(date);
            return ResponseEntity.ok(events);
        }

        @GetMapping("/date-range")
        public ResponseEntity<List<EventDto>> getEventsByDateRange(@RequestParam
                                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                   LocalDate startDate,
                                                                   @RequestParam
                                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                   LocalDate endDate) {
            log.info("Request to get events between {} and {}",  startDate, endDate);
            List<EventDto> events = eventService.getEventsByDateRange(startDate, endDate);
            return ResponseEntity.ok(events);
        }

        @GetMapping("/paginated")
        public ResponseEntity<Page<EventDto>> getEventsPaginated(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
            log.info("Request to get paginated events (page: {}, size: {})",  page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<EventDto> events = eventService.getEventsByUserPaginated( pageable);
            return ResponseEntity.ok(events);
        }

        @GetMapping("/search")
        public ResponseEntity<Page<EventDto>> searchEvents(@RequestParam String keyword,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestHeader("User-Id") UUID userId) {
            log.info("Request to search events for user {} with keyword: {}", userId, keyword);
            Pageable pageable = PageRequest.of(page, size);
            Page<EventDto> events = eventService.searchEvents(userId, keyword, pageable);
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