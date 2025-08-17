package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.TaskDto;
import com.app.brainmap.domain.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "kanbanBoard.kanbanId", source = "kanbanId")
    @Mapping(target = "kanbanColumn.columnId", source = "kanbanColumnId")
    Task toEntity(TaskDto taskDto);


    @Mapping(target ="kanbanId", source = "kanbanBoard.kanbanId")
    @Mapping(target = "kanbanColumnId", source = "kanbanColumn.columnId")
    TaskDto toDto(Task task);
}
