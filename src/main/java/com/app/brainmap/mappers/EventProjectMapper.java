package com.app.brainmap.mappers;
import com.app.brainmap.domain.dto.EventProjectDto;
import com.app.brainmap.domain.entities.EventProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventProjectMapper {

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "user.id", source = "userId")
    EventProject toEntity(EventProjectDto dto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "user.id", target = "userId")
    EventProjectDto toDto(EventProject entity);
}
