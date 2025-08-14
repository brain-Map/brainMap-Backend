package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.EventDto;
import com.app.brainmap.domain.entities.Event;
import com.app.brainmap.domain.entities.User;
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
    public EventDto createEvent(EventDto eventDto) {
        UUID userId = eventDto.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventMapper.toEntity(eventDto);
        event.setUser(user);
        event.setCreatedDate(LocalDate.now());
        event.setCreatedTime(LocalTime.now());
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto updateEvent(UUID eventId, EventDto eventDto) {
        UUID userId = eventDto.getUserId();

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId + " for user: " + userId));

        eventMapper.toEntity(eventDto, existingEvent);
        Event updatedEvent = eventRepository.save(existingEvent);

        log.info("Event updated successfully: {}", eventId);
        return eventMapper.toDto(updatedEvent);
    }

    @Override
    public void deleteEvent(UUID eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId ));

        eventRepository.delete(event);
        log.info("Event deleted successfully: {}", eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(UUID eventId) {
        log.info("Fetching event {} }", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId ));

        return eventMapper.toDto(event);
    }
//Done--------------------------------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getAllEventsByUser(UUID userId) {
        log.info("Fetching all events for user: {}", userId);

        List<Event> events = eventRepository.findByUser_IdOrderByDueDateAsc(userId);
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByUserAndDate(UUID userId, LocalDate date) {
        log.info("Fetching events for user {} on date: {}", userId, date);

        List<Event> events = eventRepository.findByUser_IdAndDueDateOrderByCreatedTimeAsc(userId, date);
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

        Page<Event> events = eventRepository.findByUser_IdOrderByDueDateDesc(userId, pageable);
        return events.map(eventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDto> searchEvents(UUID userId, String keyword, Pageable pageable) {
        log.info("Searching events for user {} with keyword: {}", userId, keyword);

        Page<Event> events = eventRepository.searchEventsByUserIdAndKeyword(userId, keyword, null);
        return events.map(eventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalEventsCount(UUID userId) {
        log.info("Getting total events count for user: {}", userId);
        return eventRepository.countByUser_Id(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getEventsCountByDate(UUID userId, LocalDate date) {
        log.info("Getting events count for user {} on date: {}", userId, date);
        return eventRepository.countEventsByUserIdAndDate(userId, date);
    }
}