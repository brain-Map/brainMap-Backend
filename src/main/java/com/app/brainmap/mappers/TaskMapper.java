package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.TaskDto;
import com.app.brainmap.domain.entities.Task;
import com.app.brainmap.domain.entities.UserTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "kanbanBoard.kanbanId", source = "kanbanId")
    @Mapping(target = "kanbanColumn.columnId", source = "kanbanColumnId")
    Task toEntity(TaskDto taskDto);


    @Mapping(target ="kanbanId", source = "kanbanBoard.kanbanId")
    @Mapping(target = "kanbanColumnId", source = "kanbanColumn.columnId")
//    @Mapping(target = "assignees", expression = "java(mapAssignees(task.getUserTasks()))")
    TaskDto toDto(Task task);

//    default List<String> mapAssignees(List<UserTask> userTasks) {
//        if (userTasks == null) return List.of();
//        return userTasks.stream()
//                .map(ut -> ut.getUser().getUserId().toString())
//                .toList();
//    }

}
