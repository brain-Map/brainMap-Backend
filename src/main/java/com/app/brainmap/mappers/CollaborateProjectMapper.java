package com.app.brainmap.mappers;

import com.app.brainmap.domain.ProjectPrivacy;
import com.app.brainmap.domain.dto.Project.AllProjectUserDto;
import com.app.brainmap.domain.entities.UserProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CollaborateProjectMapper {
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "project.isPublic", expression = "java(mapPrivacyToBoolean(project.getPrivacy()))")
    @Mapping(target = "avatar", source = "user.avatar")
    @Mapping(target = "projectStatus", source = "project.status")
    @Mapping(target = "priority", source = "project.priority")
    @Mapping(target = "createdAt", source = "project.createdAt")
    @Mapping(target = "dueDate", source = "project.dueDate")
    @Mapping(target = "title", source = "project.title")
    @Mapping(target = "description", source = "project.description")
    @Mapping(target = "projectId", source = "project.id")
    AllProjectUserDto toDto(UserProject userProject);

//    @Mapping(source = "ownerId", target = "user.id")
//    @Mapping(target = "privacy", expression = "java(mapBooleanToPrivacy(dto.isPublic()))")
//    Project toEntity(ProjectDto dto);

    default boolean mapPrivacyToBoolean(ProjectPrivacy privacy) {
        return privacy == ProjectPrivacy.PUBLIC;
    }

    default ProjectPrivacy mapBooleanToPrivacy(boolean isPublic) {
        return isPublic ? ProjectPrivacy.PUBLIC : ProjectPrivacy.PRIVATE;
    }

}
