package com.app.brainmap.domain.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TaskDto(
        UUID taskId,
        UUID kanbanId,
        UUID kanbanColumnId,
        String title,
        String description,
        LocalDate createdDate,
        LocalTime createdTime,
        LocalDate dueDate,
        String priority,
        List<String> assignees
) {

}
