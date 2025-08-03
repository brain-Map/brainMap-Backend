package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.ProjectMemberDto;
import com.app.brainmap.domain.entities.ProjectMember;
import com.app.brainmap.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMemberMapper {


    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.bio", target = "about")
    @Mapping(source = "user.address", target = "address")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.mobileNumber", target = "mobileNumber")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.userRole", target = "userRole")
    @Mapping(source = "user.workPlace", target = "workPlace")
    ProjectMemberDto toDto(ProjectMember projectMember);

    ProjectMember toEntity(ProjectMemberDto dto);
}
