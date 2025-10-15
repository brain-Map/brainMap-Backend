package com.app.brainmap.domain.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record UserAllDataDto(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email,
        String mobileNumber,
        Date dateOfBirth,
        String userRole,
        String createdAt,
        String status,
        String city,
        String gender,
        String bio,
        String avatar
) {
}
