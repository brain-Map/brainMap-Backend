package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.EventDto;
import com.app.brainmap.domain.entities.Event;
import com.app.brainmap.domain.entities.User;

public interface EventMapper {

    static EventDto toDto(Event event) {
        if (event == null) return null;
        return EventDto.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .description(event.getDescription())
                .createdDate(event.getCreatedDate())
                .dueDate(event.getDueDate())
                .createdTime(event.getCreatedTime())
                .userId(event.getUser() != null ? (java.util.UUID) event.getUser().getUserId() : null)
                .build();
    }

    static Event toEntity(EventDto dto, User user) {
        if (dto == null) return null;
        return Event.builder()
                .eventId(dto.getEventId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdDate(dto.getCreatedDate())
                .dueDate(dto.getDueDate())
                .createdTime(dto.getCreatedTime())
                .user(user)
                .build();
    }
}