package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.UserProjectDto;
import com.app.brainmap.domain.dto.UserProjectSaveDto;
import com.app.brainmap.domain.entities.UserProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProjectMapper {
    // Define methods for mapping between UserProjectDto and UserProject entity
    // For example:
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "name", expression = "java(userProject.getUser().getFirstName() + \" \" + userProject.getUser().getLastName())")
    @Mapping(target = "role", source = "role")
    @Mapping(target="email", source = "user.email")
    @Mapping(target = "avatar", source = "user.avatar")
    UserProjectDto toDto(UserProject userProject);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "project.id", source = "projectId")
    UserProject toEntity(UserProjectSaveDto userProjectSaveDto);




    // Additional methods can be added as needed for specific use cases
}
