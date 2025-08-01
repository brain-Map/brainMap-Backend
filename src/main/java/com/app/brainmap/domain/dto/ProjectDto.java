package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.entities.User;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        String description,
        UUID ownerId,
        String userName,
        String title,
        LocalDate dueDate,
        String priority

) {
}
