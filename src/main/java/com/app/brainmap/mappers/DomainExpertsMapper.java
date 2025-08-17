package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.DomainExpertsDto;
import com.app.brainmap.domain.entities.DomainExperts;
import com.app.brainmap.domain.entities.ServiceListingAvailability;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DomainExpertsMapper {
    DomainExpertsDto toDto(DomainExperts domainExperts);

    DomainExperts toEntity(DomainExpertsDto domainExpertsDto);

    Object toAvailabilityRequestDto(ServiceListingAvailability avail);

    Object toAvailabilityResponseDto(ServiceListingAvailability serviceListingAvailability);
}
