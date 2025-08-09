package com.app.brainmap.mappers;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.UpdateUser;
import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.dto.AdminUserListDto;
import com.app.brainmap.domain.dto.CreateUserDto;
import com.app.brainmap.domain.dto.UpdateUserDto;
import com.app.brainmap.domain.dto.UserDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.services.UserService;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "userRole", source = "userRole", qualifiedByName = "stringToUserRoleType")
    @Mapping(target = "userId", expression = "java(java.util.UUID.fromString(dto.getUserId()))")
    CreateUser toCreateUser(CreateUserDto dto);

    @Mapping(target = "socialLinks", source = "socialLinks")
    UpdateUser toUpdateUser(UpdateUserDto dto);

    @Mapping(target = "createdAt", expression = "java(formatCreatedAt(user.getCreatedAt()))")
    AdminUserListDto toAdminUserListDto(User user);

    default String formatCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            return null;
        }
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

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