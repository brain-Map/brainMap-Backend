package com.app.brainmap.services;

import com.app.brainmap.domain.dto.EventDto;

import java.util.List;
import java.util.UUID;

public interface EventService {
    EventDto createEvent(EventDto eventDto, UUID userId);
    EventDto getEventById(UUID eventId);
    List<EventDto> getAllEvents();
    EventDto updateEvent(UUID eventId, EventDto eventDto);
    void deleteEvent(UUID eventId);
}
