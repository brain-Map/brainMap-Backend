package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.Event;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.dto.EventDto;
import com.app.brainmap.mappers.EventMapper;
import com.app.brainmap.repositories.EventRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Override
    public EventDto createEvent(EventDto eventDto, UUID userId) {
        log.info("Creating event for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Event event = eventMapper.toEntity(eventDto);
        event.setUser(user);
        event.setCreatedDate(LocalDate.now());
        event.setCreatedTime(LocalTime.now());

        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with id: {}", savedEvent.getEventId());

        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto updateEvent(UUID eventId, EventDto eventDto, UUID userId) {
        log.info("Updating event {} for user: {}", eventId, userId);

        Event existingEvent = eventRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId + " for user: " + userId));

        eventMapper.updateEntityFromDto(eventDto, existingEvent);
        Event updatedEvent = eventRepository.save(existingEvent);

        log.info("Event updated successfully: {}", eventId);
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public void deleteEvent(UUID eventId, UUID userId) {
        log.info("Deleting event {} for user: {}", eventId, userId);

        Event event = eventRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId + " for user: " + userId));

        eventRepository.delete(event);
        log.info("Event deleted successfully: {}", eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(UUID eventId, UUID userId) {
        log.info("Fetching event {} for user: {}", eventId, userId);

        Event event = eventRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId + " for user: " + userId));

        return eventMapper.toDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getAllEventsByUser(UUID userId) {
        log.info("Fetching all events for user: {}", userId);

        List<Event> events = eventRepository.findByUserIdOrderByDueDateAsc(userId);
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByUserAndDate(UUID userId, LocalDate date) {
        log.info("Fetching events for user {} on date: {}", userId, date);

        List<Event> events = eventRepository.findByUserIdAndDueDateOrderByCreatedTimeAsc(userId, date);
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching events for user {} between {} and {}", userId, startDate, endDate);

        List<Event> events = eventRepository.findEventsByUserIdAndDateRange(userId, startDate, endDate);
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDto> getEventsByUserPaginated(UUID userId, Pageable pageable) {
        log.info("Fetching paginated events for user: {} with page: {}", userId, pageable.getPageNumber());

        Page<Event> events = eventRepository.findByUserIdOrderByDueDateDesc(userId, pageable);
        return events.map(eventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> searchEvents(UUID userId, String keyword) {
        log.info("Searching events for user {} with keyword: {}", userId, keyword);

        List<Event> events = eventRepository.searchEventsByUserIdAndKeyword(userId, keyword);
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalEventsCount(UUID userId) {
        log.info("Getting total events count for user: {}", userId);
        return eventRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getEventsCountByDate(UUID userId, LocalDate date) {
        log.info("Getting events count for user {} on date: {}", userId, date);
        return eventRepository.countEventsByUserIdAndDate(userId, date);
    }
}
