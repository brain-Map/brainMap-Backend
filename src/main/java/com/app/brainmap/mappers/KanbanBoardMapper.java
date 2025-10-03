package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.KanbanBoardColumnDto;
import com.app.brainmap.domain.dto.KanbanBoardDto;
import com.app.brainmap.domain.entities.KanbanBoard;
import com.app.brainmap.domain.entities.KanbanColumn;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KanbanBoardMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "columns", source = "columns")
    @Mapping(target = "kanbanId", source = "kanbanId")
    KanbanBoardDto toDto(KanbanBoard kanbanBoard);

    // Explicitly map columnId
    @Mapping(target = "columnId", source = "columnId")
    @Mapping(target = "type", source = "type")
    KanbanBoardColumnDto toDto(KanbanColumn column);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "columns", source = "columns")
    KanbanBoard toEntity(KanbanBoardDto dto);


    @Mapping(target = "columnId", source = "columnId")
    @Mapping(target = "type", source = "type")
    KanbanColumn toEntity(KanbanBoardColumnDto dto);
}
