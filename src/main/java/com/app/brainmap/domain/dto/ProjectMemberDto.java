package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.UserRoleType;
import jakarta.persistence.Enumerated;

import java.util.UUID;

public record ProjectMemberDto(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email,
        String mobileNumber,
        String address,
        String workPlace,
        String about,
        String location,
        UserRoleType userRole
) {


}
