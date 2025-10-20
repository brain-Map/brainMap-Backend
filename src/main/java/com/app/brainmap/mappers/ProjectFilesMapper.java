package com.app.brainmap.mappers;
import com.app.brainmap.domain.dto.EventProjectDto;
import com.app.brainmap.domain.dto.ProjectFileDto;
import com.app.brainmap.domain.entities.EventProject;
import com.app.brainmap.domain.entities.ProjectFiles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectFilesMapper {

    @Mapping(target = "project.id", source = "projectId")
    ProjectFiles toEntity(ProjectFileDto dto);

    @Mapping(source = "project.id", target = "projectId")
    ProjectFileDto toDto(ProjectFiles entity);
}
