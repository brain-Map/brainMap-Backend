package com.app.brainmap.domain.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record EventProjectDto(
        UUID eventId,
        UUID projectId,
        String title,
        String description,
        LocalDate createdDate,
        LocalDate dueDate,
        UUID userId,
        LocalTime createdTime,
        LocalTime dueTime


) {
}
