package com.app.brainmap.mappers;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.dto.CreateUserDto;
import com.app.brainmap.domain.dto.UserDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.services.UserService;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "userRole", source = "userRole", qualifiedByName = "stringToUserRoleType")
    @Mapping(target = "socialLinks", source = "socialLinks")
    CreateUser toCreateUser(CreateUserDto dto);

    @Named("stringToUserRoleType")
    default UserRoleType stringToUserRoleType(String role) {
        if (role == null) {
            return null;
        }
        try {
            return UserRoleType.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user role: " + role);
        }
    }
}