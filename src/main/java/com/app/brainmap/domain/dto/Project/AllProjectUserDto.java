package com.app.brainmap.domain.dto.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;



public record AllProjectUserDto(
        UUID projectId,
        String description,
        UUID userId,
        String userName,
        String title,
        LocalDate dueDate,
        LocalDateTime createdAt,
        String priority,
        boolean isPublic,
        String projectStatus,
        String role,
        String avatar
) {
}
