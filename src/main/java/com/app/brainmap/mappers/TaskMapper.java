package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.TaskDto;
import com.app.brainmap.domain.entities.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TaskMapper {

    Task toEntity(TaskDto taskDto);

    TaskDto toDto(Task task);
}
