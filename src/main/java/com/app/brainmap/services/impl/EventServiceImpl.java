package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.EventDto;
import com.app.brainmap.domain.entities.Event;
import com.app.brainmap.domain.entities.Project;
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
        // Set dueDate and dueTime from DTO, since mapper ignores them
        event.setDueDate(eventDto.getDueDate());
        event.setDueTime(eventDto.getDueTime());
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto updateEvent(UUID eventId, EventDto eventDto) {
        UUID userId = eventDto.getUserId();

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId + " for user: " + userId));

        eventMapper.toEntity(eventDto, existingEvent);
        // Set dueDate and dueTime from DTO, since mapper ignores them
        existingEvent.setDueDate(eventDto.getDueDate());
        existingEvent.setDueTime(eventDto.getDueTime());

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

    @Override
    public List<Event> listEvent() {
        return eventRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByDate(LocalDate date) {
        log.info("Fetching events on date: {}", date);

        List<Event> events = eventRepository.findByDueDateOrderByDueTimeAsc(date);
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventDto> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching events between {} and {}", startDate, endDate);

        List<Event> events = eventRepository.findByDueDateBetweenOrderByDueDateAscDueTimeAsc(startDate, endDate);
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalEventsCount() {
        long count = eventRepository.count();
        log.info("Total events count: {}", count);
        return count;
    }

    @Override
    @Transactional(readOnly = true)//CHECK
    public long getEventsCountByDate(LocalDate date) {
        log.info("Getting events count on date: {}",date);
        return eventRepository.countByDueDate(date);
    }
}