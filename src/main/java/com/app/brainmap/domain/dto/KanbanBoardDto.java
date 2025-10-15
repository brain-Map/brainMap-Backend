package com.app.brainmap.domain.dto;

import java.util.List;
import java.util.UUID;


public record KanbanBoardDto(
        UUID kanbanId,
        UUID projectId,
        List<KanbanBoardColumnDto> columns
){
}

