package com.app.brainmap.services;

import com.app.brainmap.domain.dto.EventDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventService {

    EventDto createEvent(EventDto eventDto);

    EventDto updateEvent(UUID eventId, EventDto eventDto);

    void deleteEvent(UUID eventId);

    EventDto getEventById(UUID eventId);

    List<EventDto> getAllEventsByUser(UUID userId);

    List<EventDto> getEventsByDate(LocalDate date);

    List<EventDto> getEventsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);

    @Transactional(readOnly = true)
    List<EventDto> getEventsByDateRange(LocalDate startDate, LocalDate endDate);

    Page<EventDto> getEventsByUserPaginated(Pageable pageable);

    Page<EventDto> searchEvents(UUID userId, String keyword, Pageable pageable);

    long getTotalEventsCount(UUID userId);

    long getEventsCountByDate(UUID userId, LocalDate date);
}