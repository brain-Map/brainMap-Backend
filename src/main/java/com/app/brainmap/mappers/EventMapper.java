package com.app.brainmap.mappers;

import com.app.brainmap.domain.entities.Event;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.dto.EventDto;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventDto toDto(Event event) {
        if (event == null) {
            return null;
        }

        return EventDto.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .description(event.getDescription())
                .createdDate(event.getCreatedDate())
                .dueDate(event.getDueDate())
                .createdTime(event.getCreatedTime())
                .userId(event.getUser() != null ? event.getUser().getId() : null)
                .build();
    }

    public Event toEntity(EventDto eventDto) {
        if (eventDto == null) {
            return null;
        }

        Event.EventBuilder eventBuilder = Event.builder()
                .eventId(eventDto.getEventId())
                .title(eventDto.getTitle())
                .description(eventDto.getDescription())
                .createdDate(eventDto.getCreatedDate())
                .dueDate(eventDto.getDueDate())
                .createdTime(eventDto.getCreatedTime());

        // Note: User entity needs to be set separately in the service layer
        return eventBuilder.build();
    }

    public void updateEntityFromDto(EventDto eventDto, Event event) {
        if (eventDto == null || event == null) {
            return;
        }

        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setDueDate(eventDto.getDueDate());
        // Note: createdDate and createdTime should not be updated
    }
}