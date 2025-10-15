package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.entities.User;

import java.util.UUID;

public record UserDto(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email,
        String avatar
) {
    public static UserDto fromEntity(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(),
                user.getUsername(), user.getEmail(), user.getAvatar());
    }
}