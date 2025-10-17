package com.app.brainmap.mappers;

import com.app.brainmap.domain.CreateNotesRequest;
import com.app.brainmap.domain.dto.CreateNotesRequestDto;
import com.app.brainmap.domain.dto.NotesDto;
import com.app.brainmap.domain.entities.Notes;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotesMapper {

    NotesDto toDto(Notes notes);
    
    CreateNotesRequest toCreateNotesRequest(CreateNotesRequestDto dto);
} 