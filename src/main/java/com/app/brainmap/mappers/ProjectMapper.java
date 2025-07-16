package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.ProjectDto;
import com.app.brainmap.domain.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto dto);
}
