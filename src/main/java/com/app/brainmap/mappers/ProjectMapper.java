package com.app.brainmap.mappers;

import com.app.brainmap.domain.ProjectPrivacy;
import com.app.brainmap.domain.dto.ProjectDto;
import com.app.brainmap.domain.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.id", target = "ownerId")
    @Mapping(target = "isPublic", expression = "java(mapPrivacyToBoolean(project.getPrivacy()))")
    ProjectDto toDto(Project project);

    @Mapping(source = "ownerId", target = "user.id")
    @Mapping(target = "privacy", expression = "java(mapBooleanToPrivacy(dto.isPublic()))")
    Project toEntity(ProjectDto dto);

    default boolean mapPrivacyToBoolean(ProjectPrivacy privacy) {
        return privacy == ProjectPrivacy.PUBLIC;
    }

    default ProjectPrivacy mapBooleanToPrivacy(boolean isPublic) {
        return isPublic ? ProjectPrivacy.PUBLIC : ProjectPrivacy.PRIVATE;
    }


}
