package com.app.brainmap.domain.dto;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        String description,
        String status,
        String title,
        LocalDateTime dueDate,
        String priority
) {
}
