package com.app.brainmap.mappers;

import com.app.brainmap.domain.CreateUser;
import com.app.brainmap.domain.UpdateUser;
import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.dto.*;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.services.UserService;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);
    User toEntity(UserDto userDto);

    public default UserAllDataDto toAllDataDto(User user) {
     if (user == null) return null;
     return new UserAllDataDto(
         user.getId(),
         user.getFirstName(),
         user.getLastName(),
         user.getUsername(),
         user.getEmail(),
         user.getMobileNumber(),
         user.getDateOfBirth(),
         user.getUserRole().toString(),
         user.getCreatedAt().toString(),
         user.getStatus().toString(),
         user.getCity(),
         user.getGender(),
         user.getBio(),
         user.getAvatar()
     );
 }

    @Mapping(target = "userRole", source = "userRole", qualifiedByName = "stringToUserRoleType")
    @Mapping(target = "userId", expression = "java(java.util.UUID.fromString(dto.getUserId()))")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    CreateUser toCreateUser(CreateUserDto dto);

    @Mapping(target = "socialLinks", source = "socialLinks")
    UpdateUser toUpdateUser(UpdateUserDto dto);

    @Mapping(target = "createdAt", expression = "java(formatTime(user.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(formatTime(user.getUpdatedAt()))")
    AdminUserListDto toAdminUserListDto(User user);


    default String formatTime(LocalDateTime formatTime) {
        if (formatTime == null) {
            return null;
        }
        return formatTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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