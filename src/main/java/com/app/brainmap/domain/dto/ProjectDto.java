package com.app.brainmap.domain.dto;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        LocalDateTime created_at,
        String description,
        String status,
        String title,
        LocalDateTime due_date,
        String priority
) {
}
