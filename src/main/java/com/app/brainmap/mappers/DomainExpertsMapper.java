package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.DomainExpertsDto;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DomainExpertsMapper {
    DomainExpertsDto toDto(DomainExperts domainExperts);

    DomainExperts toEntity(DomainExpertsDto domainExpertsDto);

}
