package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.EventDto;
import com.app.brainmap.domain.entities.Event;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.EventMapper;
import com.app.brainmap.repositories.EventRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EventDto createEvent(EventDto eventDto, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Event event = EventMapper.toEntity(eventDto, user);
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toDto(savedEvent);
    }

    @Override
    public EventDto getEventById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found"));
        return EventMapper.toDto(event);
    }

    @Override
    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto updateEvent(UUID eventId, EventDto eventDto) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found"));
        existingEvent.setTitle(eventDto.getTitle());
        existingEvent.setDescription(eventDto.getDescription());
        existingEvent.setDueDate(eventDto.getDueDate());
        // Not updating createdDate, createdTime, or user
        Event updatedEvent = eventRepository.save(existingEvent);
        return EventMapper.toDto(updatedEvent);
    }

    @Override
    public void deleteEvent(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NoSuchElementException("Event not found");
        }
        eventRepository.deleteById(eventId);
    }
}