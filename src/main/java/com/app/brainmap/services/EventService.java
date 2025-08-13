package com.app.brainmap.services;

import com.app.brainmap.domain.dto.EventDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventService {

    EventDto createEvent(EventDto eventDto, UUID userId);

    EventDto updateEvent(UUID eventId, EventDto eventDto, UUID userId);

    void deleteEvent(UUID eventId, UUID userId);

    EventDto getEventById(UUID eventId, UUID userId);

    List<EventDto> getAllEventsByUser(UUID userId);

    List<EventDto> getEventsByUserAndDate(UUID userId, LocalDate date);

    List<EventDto> getEventsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);

    Page<EventDto> getEventsByUserPaginated(UUID userId, Pageable pageable);

    Page<EventDto> searchEvents(UUID userId, String keyword, Pageable pageable);

    long getTotalEventsCount(UUID userId);

    long getEventsCountByDate(UUID userId, LocalDate date);
}