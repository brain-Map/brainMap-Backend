package com.app.brainmap.domain.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email,
        String mobileNumber
) {
}