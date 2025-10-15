package com.app.brainmap.services;

import com.app.brainmap.domain.dto.EventDto;
import com.app.brainmap.domain.entities.Event;
import com.app.brainmap.domain.entities.Project;
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

    List<Event> listEvent();

    List<EventDto> getEventsByDate(LocalDate date);

    List<EventDto> getEventsByDateRange(LocalDate startDate, LocalDate endDate);//CHECK

    long getTotalEventsCount();

    long getEventsCountByDate(LocalDate date);
}