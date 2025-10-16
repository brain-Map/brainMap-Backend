package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.entities.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record UserDto(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email,
        String avatar,
        String mobileNumber,
        Date dateOfBirth,
        String bio,
        String gender


) {
    public static UserDto fromEntity(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatar(),
                user.getMobileNumber(),
                user.getDateOfBirth(),
                user.getBio(),
                user.getGender()
        );
    }

}