package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.UserProjectDto;
import com.app.brainmap.domain.entities.UserProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProjectMapper {
    // Define methods for mapping between UserProjectDto and UserProject entity
    // For example:
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "name", expression = "java(userProject.getUser().getFirstName() + \" \" + userProject.getUser().getLastName())")
    @Mapping(target = "role", source = "role")
    UserProjectDto toDto(UserProject userProject);
    // UserProject toEntity(UserProjectDto userProjectDto);

    // Additional methods can be added as needed for specific use cases
}
