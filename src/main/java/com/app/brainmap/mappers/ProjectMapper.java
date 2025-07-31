package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.ProjectDto;
import com.app.brainmap.domain.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.id", target = "ownerId")
    ProjectDto toDto(Project project);

    @Mapping(source = "ownerId", target = "user.id")
    Project toEntity(ProjectDto dto);
}
