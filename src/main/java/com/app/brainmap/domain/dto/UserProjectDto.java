package com.app.brainmap.domain.dto;

import java.util.Map;
import java.util.UUID;

public record UserProjectDto(
        UUID userId,
        UUID projectId,
        String name,
        String role,
        String status,
        String position
) {

}
