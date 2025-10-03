package com.app.brainmap.domain.dto;
import java.util.UUID;


public record KanbanBoardColumnDto(
        UUID columnId,
        String type
) {

}

