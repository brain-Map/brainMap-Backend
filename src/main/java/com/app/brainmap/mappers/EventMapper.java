package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.EventDto;
import com.app.brainmap.domain.entities.Event;
import com.app.brainmap.domain.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "user.id", target = "userId")
    EventDto toDto(Event event);

    @Mapping(target = "user", source = "userId", qualifiedByName = "userFromId")
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "dueTime", ignore = true)
    Event toEntity(EventDto dto, @MappingTarget Event existingEvent);

    @Mapping(target = "user", source = "userId", qualifiedByName = "userFromId")
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "dueTime", ignore = true)
    Event toEntity(EventDto dto);

    @Named("userFromId")
    default User userFromId(java.util.UUID userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}