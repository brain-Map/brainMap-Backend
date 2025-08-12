package com.app.brainmap.mappers;

import com.app.brainmap.domain.entities.Event;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.dto.EventDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
                .userId(extractUserId(event.getUser()))
                .build();
    }

    public Event toEntity(EventDto eventDto) {
        if (eventDto == null) {
            return null;
        }

        Event.EventBuilder eventBuilder = Event.builder()
                // Don't set eventId for new entities - let JPA handle it
                // .eventId(eventDto.getEventId())
                .title(eventDto.getTitle())
                .description(eventDto.getDescription())
                .dueDate(eventDto.getDueDate());
        // Don't set createdDate and createdTime - let entity handle these with @CreationTimestamp

        return eventBuilder.build();
    }

    public Event toEntityWithId(EventDto eventDto) {
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

        return eventBuilder.build();
    }

    public void updateEntityFromDto(EventDto eventDto, Event event) {
        if (eventDto == null || event == null) {
            return;
        }

        // Only update non-null values to avoid overwriting existing data
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getDueDate() != null) {
            event.setDueDate(eventDto.getDueDate());
        }
        // Note: createdDate and createdTime should not be updated
    }

    // Helper method for safe user ID extraction
    private UUID extractUserId(User user) {
        return user != null ? user.getId() : null;
    }

    // Additional helper method for setting user relationship
    public void setUserRelation(Event event, User user) {
        if (event != null) {
            event.setUser(user);
        }
    }
}